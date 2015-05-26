- ����ͬ��service

- `��������`:
- `���÷�Χ`:�Ƚ��ȳ����߳�
- `ʵ������`:�ɷ�����spring����������Ϻ���
- `�������`:
 - LinkedBlockingQueue��������
 - ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
- `Ӧ��ʵ��`:

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
	
	//����ͬ����ʽ���أ�Ϊtrue��Ϊ�첽ͬ����Ϊfalse��Ϊͬ��ͬ��
	private static final boolean openAsynchronization = true;
	
	private static final int SLEEP_TIME = 100;
	private static final int RETRY_TIMES = 3;
	private static final int MAX_THREAD_NUM = 20;

	//ͬ�����б��ֵ���󳤶�
	private static final int MAX_QUEUE_LENGTH = 5000;
	
	//�洢��ͬ����LHFUserData��������
	private LinkedBlockingQueue<SyncObj> dataQueue = new LinkedBlockingQueue<SyncObj>();
	
	//�����Ǵ�ͬ�������ݵļ�ֵ
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
	 * ͬ��UserData��LHFUserData
	 * @param userData
	 */
	public void sync(String globalUserId, String serverId) {
		if (!LHFLoadConfig.isLhfServer(serverId)) {
			return;
		}
		
		//������첽ͬ���Ļ�
		if (openAsynchronization) {
			putInQueue(globalUserId, serverId);
		}
		//����ʹ��ͬ��ͬ��
		else {
			UserData userData = userDataDao.findByID(globalUserId);
			LHFUserData lhfUserData = new LHFUserData(userData, userDataService);
			
			lhfUserDataService.syncLhfUserData(lhfUserData);
		}
	}
	
	/**
	 * ��ʼͬ��
	 */
	public void write() {
		//������첽ͬ���Ļ�
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