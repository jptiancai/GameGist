package service.timer;

import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import cons.GameData;
import cons.SystemConstants;
import mysql.DynamicDataSource;
import mysql.dao.ServerZoneInfoDao;
import mysql.domain.ServerZoneInfo;
import service.UserDataService;
import service.server.MemcachedService;
import utils.LogFactory;

public abstract class BaseTimerTask extends TimerTask {
	private static final Log logger = LogFactory.getLog(BaseTimerTask.class);

	protected boolean skipUnusedServer = true;
	
	@Autowired
	private ServerZoneInfoDao serverZoneInfoDao;
	
	@Autowired
	private MemcachedService memcachedService;

	@Override
	public void run() {
		try{
			/**
			 * 
			 */
			UserDataService.serverIdLocal.set(null);
			List<ServerZoneInfo> serverZoneInfoList = null;
			try{
				serverZoneInfoList = serverZoneInfoDao.loadAll();
			}catch(Exception e){
				logger.error("Can not get serverZoneInfoList",e);
			}
			if(null != serverZoneInfoList && serverZoneInfoList.size() > 0){
				Collections.sort(serverZoneInfoList);
				for (ServerZoneInfo serverZoneInfo : serverZoneInfoList) {
					// 服务器状态检查
					String status = serverZoneInfo.getStatus();
					String serverIndex = serverZoneInfo.getIndex()+"";
					if (skipUnusedServer && 
							(status.equals(SystemConstants.SERVER_STATE_EXTRA) || status.equals(SystemConstants.SERVER_STATE_FORBIDDEN))){
						logger.info("skipUnusedServer ServerIndex:" + serverIndex);
						continue;
					}
					
					DynamicDataSource ds = GameData.webApplication.getBean(DynamicDataSource.class);
					if (!ds.hasDataSourceByServerId(serverIndex)){
						logger.warn("!!!!!!Not found DataSource in server: " + serverIndex);
						continue;
					}
					try{
						Thread handleThread = new Thread(new TimerRunnable(this,serverZoneInfo,serverIndex),getMemcachedKey() + "-server"+serverIndex);
						handleThread.start();
					}catch(Exception e){
						logger.error("start handleThread Error in ServerIndex:" + serverIndex,e);
					}
				}
			}					
		}catch(Throwable e){
			logger.error("BaseTimerTask Error",e);
		}
	}

	protected abstract String getMemcachedKey();

	protected abstract void runByServerZoneInfo(ServerZoneInfo serverZoneInfo);
	
	static class TimerRunnable implements Runnable{
		ServerZoneInfo serverZoneInfo;
		String serverIndex;
		BaseTimerTask baseTimerTask;
		
		public TimerRunnable(BaseTimerTask baseTimerTask, ServerZoneInfo serverZoneInfo, String serverIndex){
			this.serverZoneInfo = serverZoneInfo;
			this.serverIndex = serverIndex;
			this.baseTimerTask = baseTimerTask;
		}
		
		@Override
		public void run() {
			try{	
				UserDataService.serverIdLocal.set(serverIndex);
				this.baseTimerTask.runByServerZoneInfo(serverZoneInfo);
			}catch(Exception e){
				logger.error("runByServerZoneInfo Error ServerIndex:" + serverIndex,e);
			}
		}
		
	}
}
