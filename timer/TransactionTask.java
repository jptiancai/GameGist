package service.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.apache.commons.logging.Log;

import service.timer.BaseTimerTask;
import utils.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;

import cons.GameData;
import cons.SystemConstants;
import cons.TransactionConstants;
import exception.ErrorException;
import mysql.dao.DeviceBlackInfoDao;
import mysql.dao.TransactionIosCenterInfoDao;
import mysql.dao.TransactionIosInfoDao;
import mysql.domain.DeviceBlackInfo;
import mysql.domain.ServerZoneInfo;
import mysql.domain.TransactionCenterInfo;
import mysql.domain.TransactionIosCenterInfo;
import mysql.domain.TransactionIosInfo;

public class TransactionTask extends BaseTimerTask {

	private static final Log logger = LogFactory.getLog(TransactionTask.class);

	@Autowired
	private DeviceBlackInfoDao deviceBlackInfoDao;

	@Autowired
	private MemcachedService memcachedService;

	@Autowired
	private TransactionCenterService transactionCenterService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionIosInfoDao transactionIosInfoDao;

	@Autowired
	private TransactionIosCenterInfoDao transactionIosCenterInfoDao;

	private final int maxRetryTimes = 14;

	private final int retrySeconds[] = { 0,// 0sec
			10,// 10sec
			30, 60, 300,// 5min
			600,// 10min
			1800,// 30min
			3600,// 1h
			7200,// 2h
			14400,// 4h
			21600,// 6h
			28800,// 8h
			36000,// 10h
			43200,// 12h
			86400 // 24h
	};

	private static final String MEMCACHED_KEY = "TransactionTaskTimer";
	
	@Override
	protected String getMemcachedKey() {
		return MEMCACHED_KEY;
	}


	protected void runByServerZoneInfo(ServerZoneInfo serverZoneInfo) {
		logger.info("dispath thread start!");
		long lastDone = 0;
		while (true) {
			try {
				String mode = null;
				if (GameData.CONFIG_READY) {
					mode = GameData.getServerConfigMap().get(
							SystemConstants.SERVER_CONFIG_NAME_MODE);
					if (mode != null) {
						if (mode.equals(SystemConstants.SERVER_MODE_AND_MAIN)
								|| mode.equals(SystemConstants.SERVER_MODE_JAB_MAIN)) {
							logger.info("wait 30 min for dispathNotifyTask...");
							Thread.sleep(1800000);
							Object object = memcachedService.get(mode);
							if (object == null) {
								memcachedService.set(mode, 3600, mode);
								logger.info("andMainTask start...");
								dispathNotifyTask();
								memcachedService.del(mode);
							}
						} else if (mode
								.equals(SystemConstants.SERVER_MODE_IOS_NORMAL)
								|| mode.equals(SystemConstants.SERVER_MODE_IOS_BLACK)) {
							logger.info("wait 15 min for iosNotifyTask...");
							Thread.sleep(900000);
							Object object = memcachedService.get(mode);
							if (object == null) {
								memcachedService.set(mode, 3600, mode);
								logger.info("iosNormalTask start...");
								updateBlackList(lastDone);
								dispathIosTask();
								memcachedService.del(mode);
							}
						} else if (mode
								.equals(SystemConstants.SERVER_MODE_IOS_MAIN)) {
							logger.info("wait 30 min for iosNotifyTask...");
							Thread.sleep(1800000);
							Object object = memcachedService.get(mode);
							if (object == null) {
								memcachedService.set(mode, 3600, mode);
								logger.info("iosMainTask start...");
								checkBlackList();
								memcachedService.del(mode);
							}
						} else
							mode = null;
						lastDone = System.currentTimeMillis();
					}
				}
				if (mode == null)
					Thread.sleep(60000);
			} catch (Exception e) {
				logger.error("dispathNotifyTask exception!", e);
			}
		}

	}

	private void checkBlackList() {
		List<TransactionIosCenterInfo> loadAll = transactionIosCenterInfoDao
				.loadAll();
		Map<String, Set<String>> checkMap = new HashMap<String, Set<String>>();
		List<String> blackNew = new ArrayList<String>();
		for (TransactionIosCenterInfo transactionIosCenterInfo : loadAll) {
			String deviceId = transactionIosCenterInfo.getDeviceId();
			if (transactionIosCenterInfo.getUserId() != 0 && deviceId != null
					&& !deviceId.equals("")) {
				String check = deviceId + transactionIosCenterInfo.getUserId()
						+ transactionIosCenterInfo.getServerIndex();
				Set<String> checkSet = checkMap.get(deviceId);
				if (checkSet == null)
					checkSet = new HashSet<String>();
				checkSet.add(check);
				int count = checkSet.size();
				if (count > TransactionConstants.IOS_PAY_DEVICE_MAX
						&& !blackNew.contains(deviceId))
					blackNew.add(deviceId);
				checkMap.put(deviceId, checkSet);
			}
		}
		for (String deviceId : blackNew) {
			DeviceBlackInfo deviceBlackInfo = new DeviceBlackInfo();
			deviceBlackInfo.setDeviceId(deviceId);
			try {
				deviceBlackInfoDao.save(deviceBlackInfo);
			} catch (Exception e) {
			}
		}
	}

	private int getRetryInteralByRetryTimes(int times) {
		if (times < 0 || times >= retrySeconds.length) {
			return 0;
		}
		return retrySeconds[times];
	}

	private boolean txnNeedDispath(TransactionCenterInfo txn) {
		long now = System.currentTimeMillis();
		int retryTimes = txn.getRetryTimes();
		long startTime = txn.getStartTime();
		if (retryTimes >= maxRetryTimes) {// retry full times.
			return false;
		}
		int retryInteralSeconds = getRetryInteralByRetryTimes(retryTimes);
		if (now < startTime + retryInteralSeconds) {
			// now not to the retry time.
			return false;
		}
		return true;
	}

	private boolean txnNeedDispath(TransactionIosInfo txn) {
		long now = System.currentTimeMillis();
		int retryTimes = txn.getRetryTimes();
		long startTime = txn.getStartTime();
		if (retryTimes >= maxRetryTimes) {// retry full times.
			return false;
		}
		int retryInteralSeconds = getRetryInteralByRetryTimes(retryTimes);
		if (now < startTime + retryInteralSeconds) {
			// now not to the retry time.
			return false;
		}
		return true;
	}

	/**
	 * 主服->业务的通知,需要模式 andMain
	 */
	public void dispathNotifyTask() {
		List<TransactionCenterInfo> txnList = transactionCenterService
				.findDispathingTxn();
		if (txnList == null || txnList.size() == 0) {
			return;
		}
		for (TransactionCenterInfo txn1 : txnList) {
			TransactionCenterInfo txn = transactionCenterService.findById(txn1
					.getOrderId());// reload to refresh
			if (!txnNeedDispath(txn)) {
				continue;
			}
			try {
				transactionCenterService.dispathToZoneServer(txn);
				txn.setDispatchStatus(TransactionConstants.DISAPATCH_STATUS_SUCCESS);
			} catch (ErrorException e) {
				if (txn.getRetryTimes() >= maxRetryTimes) {
					txn.setDispatchStatus(TransactionConstants.DISAPATCH_STATUS_FAILED);
				}
			}
			txn.setRetryTimes(txn.getRetryTimes() + 1);
			transactionCenterService.updateTransactionCenter(txn);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("",e);
			}			
		}
	}

	/**
	 * 业务-><-主服 获取黑名单,需要模式 iosNormal
	 */
	public void updateBlackList(long lastUpdate) {
		try {
			List<DeviceBlackInfo> deviceBlackList = transactionService
					.getDeviceBlackListLastUpdate(lastUpdate);
			for (DeviceBlackInfo deviceBlackInfo : deviceBlackList) {
				if (deviceBlackInfo.getState() == TransactionConstants.DEVICE_BLACK_STATE_ACTIVE) {
					try {
						deviceBlackInfoDao.save(deviceBlackInfo);
					} catch (Exception e) {
						try {
							deviceBlackInfoDao.update(deviceBlackInfo);
						} catch (Exception e1) {
							logger.error("",e1);
							logger.error("",e);
						}
					}
				} else if (deviceBlackInfo.getState() == TransactionConstants.DEVICE_BLACK_STATE_INACTIVE)
					deviceBlackInfoDao.remove(deviceBlackInfo);
			}
		} catch (ErrorException e) {
			logger.error("Failed to get device black list from main server!");
		}
		List<DeviceBlackInfo> blackList = deviceBlackInfoDao.loadAll();
		GameData.getDeviceBlackSet().clear();
		for (DeviceBlackInfo deviceBlackInfo : blackList) {
			if (deviceBlackInfo.getState() == TransactionConstants.DEVICE_BLACK_STATE_ACTIVE)
				GameData.getDeviceBlackSet().add(deviceBlackInfo.getDeviceId());
		}
	}

	/**
	 * 业务->主服的通知,需要模式 iosNormal
	 */
	public void dispathIosTask() {
		List<TransactionIosInfo> txnList = transactionIosInfoDao
				.findDispathingTxn();
		if (txnList == null || txnList.size() == 0) {
			return;
		}
		for (TransactionIosInfo txn : txnList) {
			if (!txnNeedDispath(txn)) {
				continue;
			}
			try {
				transactionService.dispatchToMainServer(txn);
				txn.setDispatchStatus(TransactionConstants.DISAPATCH_STATUS_SUCCESS);
			} catch (ErrorException e) {
				if (txn.getRetryTimes() >= maxRetryTimes) {
					txn.setDispatchStatus(TransactionConstants.DISAPATCH_STATUS_FAILED);
				}
			}
			txn.setRetryTimes(txn.getRetryTimes() + 1);
			transactionIosInfoDao.update(txn);
		}
	}

}
