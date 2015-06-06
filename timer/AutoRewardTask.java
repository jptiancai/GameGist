package service.server;

import org.apache.commons.logging.Log;

import utils.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mysql.dao.ServerZoneInfoDao;
import mysql.domain.ServerZoneInfo;
import service.PromotionService;
import service.timer.BaseTimerTask;

@Service
public class AutoRewardTask extends BaseTimerTask{
	private static final Log logger = LogFactory.getLog(TimeZeroTask.class);	
	@Autowired
	protected PromotionService promotionService;
	
	@Autowired
	private ServerZoneInfoDao serverZoneInfoDao;

	@Autowired
	private LoadConfig loadConifg;

	private static final String MEMCACHED_KEY = "AutoRewardTaskTimer";
	
	@Override
	protected String getMemcachedKey() {
		return MEMCACHED_KEY;
	}
	
	public void execute() {
		this.run();
	}
	
	@Override
	public void run() {
		loadConifg.loadDicData();
		super.run();
	}
	
	@Override
	public void runByServerZoneInfo(ServerZoneInfo serverZoneInfo) {
	
		logger.info(System.currentTimeMillis() + "auto reward task enter");
		try {
				String serverIndex = String.valueOf(serverZoneInfo.getIndex());
				
				loadConifg.reloadGameData(serverIndex);
				//holiday lottery award
				logger.info(System.currentTimeMillis() + "  calling holidayLotteryReward");
				promotionService.holidayLotteryReward();
				//holiday diamond consume award
				
				logger.info(System.currentTimeMillis() + "  calling diamondConsumeReward");
				promotionService.diamondConsumeReward();
				//lucky lottery reward
				logger.info(System.currentTimeMillis() + "  calling luckyLotteryReward");
				promotionService.luckyLotteryReward();
				//pvp reward
				logger.info(System.currentTimeMillis() + "  calling pvpRankingReward");
	            promotionService.pvpRankingReward();
				logger.info(System.currentTimeMillis() + "  calling newServerRushReward");
	            promotionService.newServerRushReward();
	            
	            //充值奖励功能暂时不需要
//				logger.info(System.currentTimeMillis() + "  calling payPromotionReward");
//	            promotionService.payPromotionReward();
	            
	            //武功奖励功能暂不开放
//				logger.info(System.currentTimeMillis() + "  calling wugongPromotionReward");
//	            promotionService.wugongPromotionReward();
	            
	            
				logger.info(System.currentTimeMillis() + "  calling warriorPromotionReward");
	            promotionService.warriorPromotionReward();
		} catch (Exception ex) {
			logger.warn("error! AutoRewardTask ",ex);
		}
		logger.info(System.currentTimeMillis() + "auto reward task exit");		
	}
}
