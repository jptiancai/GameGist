- 数据同步service

- `功能描述`:
- `适用范围`:先进先出的线程
- `实际运行`:可放置在spring容器加载完毕后做
- `概念解释`:
 - LinkedBlockingQueue阻塞队列
 - ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
- `应用实例`:

```
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mongodb.dao.UserDataDao;
import mongodb.domain.LHFUserData;
import mongodb.domain.UserData;
import service.LHFUserDataService;
import service.LHFVersionService;
import service.UserDataService;
import utils.LogFactory;


@Service
public class DataSnycService {
	
	private static final Log logger = LogFactory.getLog(DataSnycService.class);
	
	//数据同步方式开关，为true则为异步同步，为false则为同步同步
	private static final boolean openAsynchronization = true;
	
	private static final int SLEEP_TIME = 100;
	private static final int RETRY_TIMES = 3;
	private static final int MAX_THREAD_NUM = 20;

	//同步队列保持的最大长度
	private static final int MAX_QUEUE_LENGTH = 5000;
	
	//存储待同步的LHFUserData阻塞队列
	private LinkedBlockingQueue<SyncObj> dataQueue = new LinkedBlockingQueue<SyncObj>();
	
	//用与标记待同步的数据的键值
	private static Set<String> queueSet = new HashSet<String>();
	
	@Autowired
	private LHFUserDataService lhfUserDataService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private UserDataDao userDataDao;
	
	@Autowired
	private LHFVersionService lhfVersionService;
	
	/**
	 * 同步UserData至LHFUserData
	 * @param userData
	 */
	public void sync(String globalUserId, String serverId) {
		if (!LHFLoadConfig.isLhfServer(serverId)) {
			return;
		}
		
		//如果是异步同步的话
		if (openAsynchronization) {
			putInQueue(globalUserId, serverId);
		}
		//否则使用同步同步
		else {
			UserData userData = userDataDao.findByID(globalUserId);
			LHFUserData lhfUserData = new LHFUserData(userData, userDataService);
			
			lhfUserDataService.syncLhfUserData(lhfUserData);
		}
	}
	
	/**
	 * 开始同步
	 */
	public void write() {
		//如果是异步同步的话
		if (openAsynchronization) {
			SyncThread syncThread = new SyncThread();
			syncThread.start();
		}
	}
	
	private void putInQueue(String globalUserId, String serverId) {
		if (dataQueue.size() < MAX_QUEUE_LENGTH) {
			if (!queueSet.contains(globalUserId)) {
				try {
					SyncObj syncObj = new SyncObj(serverId, globalUserId);
					dataQueue.put(syncObj);
					queueSet.add(globalUserId);
				}
				catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class WriteRunnable implements Runnable {
		@Override
		public void run() {
			SyncObj syncObj = null;
			try {
				syncObj = dataQueue.take();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			if (syncObj != null) {
				UserDataService.serverIdLocal.set(syncObj.getServerId());
				UserData userData = userDataDao.findByID(syncObj.getGlobalUserId());
				LHFUserData lhfUserData = new LHFUserData(userData, userDataService);
				int i = 0;
				while (true) {
					try {
						lhfUserDataService.syncLhfUserData(lhfUserData);
					}
					catch (Exception e) {
						logger.error("DataSync Error! ErrorData is:" + lhfUserData.getGlobalUserId(), e);
						if (i < RETRY_TIMES) {
							logger.error("DataSync Error! Error Msg is:" + e.getMessage());
							i++;
							continue;
						}
					}
					break;
				}
				queueSet.remove(syncObj.getGlobalUserId());
			}
		}
	}
	
	private class SyncThread extends Thread {
		@Override
		public void run() {
			logger.info("DataSync Thread Start Running!");
			ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
			while (true) {
				if (dataQueue.size() == 0) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					executor.execute(new WriteRunnable());
				}
			}
		}
	}
	
	private static class SyncObj {
		private String serverId;
		private String globalUserId;
		
		public SyncObj(String serverId, String globalUserId) {
			this.setServerId(serverId);
			this.setGlobalUserId(globalUserId);
		}

		public String getServerId() {
			return serverId;
		}

		public void setServerId(String serverId) {
			this.serverId = serverId;
		}

		public String getGlobalUserId() {
			return globalUserId;
		}

		public void setGlobalUserId(String globalUserId) {
			this.globalUserId = globalUserId;
		}
	}
}


```