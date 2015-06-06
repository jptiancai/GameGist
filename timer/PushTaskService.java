package service.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.NotificationProgressListener;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.NotificationThreads;

import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ClassUtils;

import cons.AttributeConstants;
import cons.GameData;
import cons.SystemConstants;
import exception.ErrorException;
import mongodb.dao.IOSPushUserInfoDao;
import mongodb.dao.PvpRankRewardDataDao;
import mongodb.domain.IOSPushUserInfo;
import mongodb.domain.PvpRankRewardData;
import mysql.dao.PvpRankRewardSwitchDao;
import mysql.dao.ServerZoneInfoDao;
import mysql.domain.IOSPushInfo;
import mysql.domain.PvpRankRewardSwitch;
import mysql.domain.ServerZoneInfo;
import service.PromotionService;
import service.UserDataService;
import service.timer.BaseTimerTask;
import utils.LogFactory;
import utils.TimeUtil;

public class PushTaskService extends BaseTimerTask {

	private static final Log logger = LogFactory.getLog(PushTaskService.class);

	@Autowired
	private IOSPushUserInfoDao iosPushUserInfoDao;
	
	@Autowired
    private PvpRankRewardDataDao pvpRankRewardDataDao;
	
	@Autowired
    private PvpRankRewardSwitchDao pvpRankRewardSwitchDao;
	
	@Autowired
	private PromotionService promotionService;

	@Autowired
	private MemcachedService memcachedService;

	private String keystore = "";// 证书的路径 不同的平台使用不同的证书 开发板 和 发布版 也使用不同的证书 itool
									// 和 快用 也使用不同的证书

	private String password = "123";// 推送的密码 设置成123

	private boolean production = false;// 设置true为正式服务地址，false为开发者地址 按照客户端的版本来 如：
										// 客户端是开发板，就要设置为true 如果客户端是发布版
										// 就要设置为false 否则收不到推送

	// 记录推送的id 和 推送的时间 这主要是为了不向mongo中写入 注意 在推送时间段内不要多次重启服务器，否则会造成对用户的多次推送，影响体验
	// private Map<String, Long> pushRecord = new HashMap<String, Long>();

	private static final String MEMCACHED_KEY = "PushTaskServiceTimer";
	
	@Override
	protected String getMemcachedKey() {
		return MEMCACHED_KEY;
	}
	
	private boolean init() {

		logger.info("push init!!");
		
		password = "123";

		boolean bRet = false;

		try {

			while (!GameData.CONFIG_READY)
				Thread.sleep(10000);

			// 此处要获取gamedata里面的数据 所以尽量延迟启动 否则有可能造成gamedata还没有初始化，就去读取数据 造成崩溃
			String platform = GameData.getServerConfigMap().get("platForm");

			if (platform == null) {
				return bRet;
			}

			if (platform.equals("IOS")) {

				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("push_appstore_pro.p12").toURI().getPath();
				production = true;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			}else if(platform.equalsIgnoreCase("IOSVietnam")) {
				password= "longzhu";
				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("APN_dev.p12").toURI().getPath();
				production = true;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			}else if(platform.equalsIgnoreCase("IOSVietnamDebug")) {
				password= "longzhu";
				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("APN_pro.p12").toURI().getPath();
				production = true;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			}else if (platform.equals("IOS_DEBUG")) {

				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("push_appstore_deve.p12").toURI()
						.getPath();
				production = false;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			}
			else if (platform.equals("JailBreak_ITools")) {

				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("push_itools_deve.p12").toURI().getPath();
				production = false;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			} else if (platform.equals("JailBreak_KuaiYong")) {

				keystore = ClassUtils.getDefaultClassLoader()
						.getResource("push_kuaiyong_deve.p12").toURI()
						.getPath();
				production = false;// 设置true为正式服务地址，false为开发者地址
				bRet = true;
			} else if (platform.equals("Android")) {

			} else {

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return bRet;
	}
	
	public void checkPvpRankReward()
	{
	    List<PvpRankRewardSwitch> prrswitchlist = pvpRankRewardSwitchDao.loadAll();
	    
	    PvpRankRewardSwitch prrsbean = null;
	    for(PvpRankRewardSwitch prrs : prrswitchlist)
	    {
	        long now = System.currentTimeMillis();
			long end = TimeUtil.getTimeByString(prrs.getEnd(), TimeUtil.yyyyMMddHHmmss_FORMAT);
			long diff = now - end;
			//reward after ten minutes 
            if(end < now && diff < 3600000*24)
            {
                Query query = new Query(Criteria.where("oldId").in(prrs.getId())
                        .and(AttributeConstants.serverId).is(UserDataService.serverIdLocal.get()));
                List<PvpRankRewardData> pvpdatalist = pvpRankRewardDataDao.find(query);
                if(pvpdatalist.size() <= 0)
                {
                    prrsbean = prrs;
                    break;
                }
            }
	    }
	    if(prrsbean != null)
	    {
	        try
            {
	            //TODO  增加性能校验,减少频繁查询
                promotionService.pvpRankRewardResult(prrsbean.getId());
            }
            catch (ErrorException e)
            {
                logger.info("check pvp rank reward error.");
            }
	    }
	}

	@Override
	public void runByServerZoneInfo(ServerZoneInfo serverZoneInfo) {
		logger.info("threadID: " + Thread.currentThread().getId()  + "  PushTaskService timer thread start");
			while (!GameData.CONFIG_READY)
			{
			    sleep(10000);
			}
			String serverIndex = String.valueOf(serverZoneInfo.getIndex());
			
			// 重构 定时任务中需要将serverIndex放到线程中
			UserDataService.serverIdLocal.set(serverIndex);
			//推送中增加检测pvp排名活动
			checkPvpRankReward();
			
			List<IOSPushInfo> iosPushInfoList = GameData.getIosPushInfoList();
			for (IOSPushInfo iosPushInfo : iosPushInfoList) {

				if (checkTime(iosPushInfo.getStartTime(),
						iosPushInfo.getEndTime())) {
					if (checkHasPushed(iosPushInfo.getId(),
							iosPushInfo.getStartTime(),
							iosPushInfo.getEndTime())) {

					} else {

						logger.info("ios push star . startime:"
								+ iosPushInfo.getStartTime());

						// pushRecord.put(iosPushInfo.getId(),
						// System.currentTimeMillis());
						int endhour = Integer
								.valueOf(iosPushInfo.getEndTime()
										.substring(
												0,
												iosPushInfo.getEndTime().split(
														":").length));
						int starhour = Integer
								.valueOf(iosPushInfo.getStartTime()
										.substring(
												0,
												iosPushInfo.getStartTime()
														.split(":").length));

						memcachedService.set(iosPushInfo.getId(),
								60 * 60 * (endhour - starhour + 1),
								System.currentTimeMillis());

						pushRecallMsg(iosPushInfo.getPushMsg());

						logger.info("ios push end . .endtime:"
								+ iosPushInfo.getEndTime());
					}
				}
			}
		logger.info("PushTaskService timer thread end");			
	}

	public static void sleep(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e1) {
		}
	}

	// 检测当前时间段内是否有推送
	public boolean checkTime(String startTime, String endTime) {

		try {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);

			String[] startTimeAry = startTime.split(":");
			String[] endTimeAry = endTime.split(":");

			int sHour = Integer.parseInt(startTimeAry[0]);
			int sMinute = Integer.parseInt(startTimeAry[1]);

			int eHour = Integer.parseInt(endTimeAry[0]);
			int eMinute = Integer.parseInt(endTimeAry[1]);

			if ((hour > sHour || (hour == sHour && minute >= sMinute))
					&& (hour < eHour || (hour == eHour && minute <= eMinute))) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	// 检测在 该时间段内 是否已经进行过这次推送了
	public boolean checkHasPushed(String pushId, String startTime,
			String endTime) {
		// if(pushRecord.containsKey(pushId)){

		long cmemdata = memcachedService.get(pushId) == null ? 0
				: (Long) memcachedService.get(pushId);
		if (cmemdata > 0) {
			try {

				int dayDiff = TimeUtil.checkDaysDifference(cmemdata);

				if (dayDiff != 0) {
					return false;
				}

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(cmemdata);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);

				String[] startTimeAry = startTime.split(":");
				String[] endTimeAry = endTime.split(":");

				int sHour = Integer.parseInt(startTimeAry[0]);
				int sMinute = Integer.parseInt(startTimeAry[1]);

				int eHour = Integer.parseInt(endTimeAry[0]);
				int eMinute = Integer.parseInt(endTimeAry[1]);

				if ((hour > sHour || (hour == sHour && minute >= sMinute))
						&& (hour < eHour || (hour == eHour && minute <= eMinute))) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private void pushRecallMsg(String msg) {

		int minId = getMinUserId();
		int maxId = getMaxUserId();

		if (minId == -1 || maxId == -1) {
			return;
		}

		if (!init()) {
			return;
		}

		logger.info("minId is : " + minId);
		logger.info("maxId is : " + maxId);

		int step = 2000;
		for (int idIndex = minId; idIndex <= maxId;) {

			Query query = new Query();
			Criteria criteria1 = Criteria.where(AttributeConstants.userId)
					.gte(idIndex).lte(idIndex + step);
			query.addCriteria(criteria1);

			List<IOSPushUserInfo> iosPushUserInfoList = iosPushUserInfoDao
					.find(query);

			logger.info(iosPushUserInfoList.size() + "");

			pushMsg(iosPushUserInfoList, msg);

			idIndex += step;
		}

	}

	private int getMinUserId() {
		Query query = new Query();
		Criteria criteria1 = Criteria.where(AttributeConstants._id).gte(
				100000000);

		query.addCriteria(criteria1);

		query.limit(1);
		query.sort().on(AttributeConstants._id, Order.ASCENDING);
		List<IOSPushUserInfo> iosPushUserInfoList = iosPushUserInfoDao
				.find(query);

		int retMinId = -1;
		if (!iosPushUserInfoList.isEmpty()) {
			retMinId = iosPushUserInfoList.get(0).getUserId();
		}

		return retMinId;
	}

	private int getMaxUserId() {

		Query query = new Query();
		Criteria criteria1 = Criteria.where(AttributeConstants._id).gte(
				100000000);

		query.addCriteria(criteria1);

		query.limit(1);
		query.sort().on(AttributeConstants._id, Order.DESCENDING);
		List<IOSPushUserInfo> iosPushUserInfoList = iosPushUserInfoDao
				.find(query);

		int retMaxId = -1;
		if (!iosPushUserInfoList.isEmpty()) {
			retMaxId = iosPushUserInfoList.get(0).getUserId();
		}

		return retMaxId;
	}

	// /////////////////////////////////////////////IOS推送辅助
	private final NotificationProgressListener DEBUGGING_PROGRESS_LISTENER = new NotificationProgressListener() {
		public void eventThreadStarted(NotificationThread notificationThread) {
			logger.info("   [EVENT]: thread #"
					+ notificationThread.getThreadNumber() + " started with "
					+ " devices beginning at message id #"
					+ notificationThread.getFirstMessageIdentifier());
		}

		public void eventThreadFinished(NotificationThread thread) {
			logger.info("   [EVENT]: thread #" + thread.getThreadNumber()
					+ " finished: pushed messages #"
					+ thread.getFirstMessageIdentifier() + " to "
					+ thread.getLastMessageIdentifier() + " toward "
					+ " devices");
		}

		public void eventConnectionRestarted(NotificationThread thread) {
			logger.info("   [EVENT]: connection restarted in thread #"
					+ thread.getThreadNumber() + " because it reached "
					+ thread.getMaxNotificationsPerConnection()
					+ " notifications per connection");
		}

		public void eventAllThreadsStarted(
				NotificationThreads notificationThreads) {
			logger.info("   [EVENT]: all threads started: "
					+ notificationThreads.getThreads().size());
		}

		public void eventAllThreadsFinished(
				NotificationThreads notificationThreads) {
			logger.info("   [EVENT]: all threads finished: "
					+ notificationThreads.getThreads().size());
		}

		public void eventCriticalException(
				NotificationThread notificationThread, Exception exception) {
			logger.info("   [EVENT]: critical exception occurred: " + exception);
		}
	};

	private void pushMsg(List<IOSPushUserInfo> iosPushUserInfoList, String msg) {
		int threadThreads = 10; // 线程数
		try {
			// 建立与Apple服务器连接
			AppleNotificationServer server = new AppleNotificationServerBasicImpl(
					keystore, password, production);
			List<PayloadPerDevice> list = new ArrayList<PayloadPerDevice>();

			PushNotificationPayload payload = new PushNotificationPayload();
			payload.addAlert(msg);
			payload.addSound("default");// 声音
			payload.addBadge(1);// 图标小红圈的数值
			payload.addCustomDictionary("url", "www.baidu.com");// 添加字典
			for (IOSPushUserInfo iosPushUserInfo : iosPushUserInfoList) {
				if (iosPushUserInfo.getDeviceToken() != "") {
					PayloadPerDevice pay = new PayloadPerDevice(payload,
							iosPushUserInfo.getDeviceToken());// 将要推送的消息和手机唯一标识绑定
					list.add(pay);
				}
			}
			if (list.size() > 0) {
				NotificationThreads work = new NotificationThreads(server,
						list, threadThreads);//
				work.setListener(DEBUGGING_PROGRESS_LISTENER);// 对线程的监听，一定要加上这个监听
				work.start(); // 启动线程
				work.waitForAllThreads();// 等待所有线程启动完成
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

}
