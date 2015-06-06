package service.server;

import org.apache.commons.logging.Log;

import utils.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mysql.domain.ServerZoneInfo;
import service.PromotionService;
import service.RushService;
import service.timer.BaseTimerTask;

@Service
public class TimeZeroTask extends BaseTimerTask{

	private static final Log logger = LogFactory.getLog(TimeZeroTask.class);

	@Autowired
	private RushService rushService;
	
	@Autowired
	protected PromotionService promotionService;
	
	private static final String MEMCACHED_KEY = "ZeroTaskTimer";
	
	@Override
	protected String getMemcachedKey() {
		return MEMCACHED_KEY;
	}
	
	
	public void execute() {
		this.run();
	}
	@Override
	public void runByServerZoneInfo(ServerZoneInfo serverZoneInfo) {
		try {
			logger.info(System.currentTimeMillis());
			rushService.rewardEveryDay();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
