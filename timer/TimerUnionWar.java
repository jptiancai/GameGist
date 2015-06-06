package service.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import utils.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cons.AttributeConstants;
import cons.ErrorConstants;
import cons.GameData;
import cons.MailConstants;
import cons.SystemConstants;
import cons.UnionConstants;
import cons.WebConstants;
import exception.ErrorException;
import mongodb.dao.UnionDataDao;
import mongodb.dao.UnionPlayerDao;
import mongodb.dao.UnionWarInfoDao;
import mongodb.domain.MailNormal;
import mongodb.domain.UnionData;
import mongodb.domain.UnionEvent;
import mongodb.domain.UnionFightResult;
import mongodb.domain.UnionPlayer;
import mongodb.domain.UnionWarInfo;
import mongodb.domain.UserData;
import mongodb.domain.element.UnionPlayerFightInfo;
import mongodb.domain.element.UnionWarCommonAward;
import mongodb.domain.element.UnionWarContribute;
import mongodb.domain.element.UnionWarPlantTopAward;
import mongodb.domain.element.UnionWarTopAward;
import mysql.dao.ServerZoneInfoDao;
import mysql.domain.ServerZoneInfo;
import output.BaseJson;
import output.ResultObject;
import output.union.UnionDeclareResultOut;
import output.union.UnionDeclareResultOut.UnionDeclareUnitOut;
import output.union.UnionFightAttackTopOut;
import output.union.UnionFightPlayer;
import output.union.UnionFightResultOutS2S;
import service.MailService;
import service.ProductService;
import service.UnionService;
import service.UserDataService;
import service.timer.BaseTimerTask;
import utils.HttpUtil;
import utils.NumUtil;
import utils.WebUtil;

@Service
public class TimerUnionWar extends BaseTimerTask{

	private static final Log logger = LogFactory.getLog(TimerUnionWar.class);

	public static final int WINNER_SCORE = 50;

	private Gson gson = new Gson();

	@Autowired
	private UnionDataDao unionDataDao;

	@Autowired
	private UnionService unionService;

	// @Autowired
	// private UnionServerService unionServerService;

	@Autowired
	private MailService mailService;

	@Autowired
	private UnionPlayerDao unionPlayerDao;

	@Autowired
	private UnionWarInfoDao unionWarInfoDao;

	// @Autowired
	// private UserDataService userDataService;

	@Autowired
	private MemcachedService memcachedService;

	@Autowired
	private ServerZoneInfoDao serverZoneInfoDao;
	
	public static final String UNION_WAR_KEY = "union_war_key";

	private static final String MEMCACHED_KEY = "UnionWarTimer";
	
	@Override
	protected String getMemcachedKey() {
		return MEMCACHED_KEY;
	}
	
	public void execute() {
		this.run();
	}
	@Override
	public void runByServerZoneInfo(ServerZoneInfo serverZoneInfo) {	
				String serverIndex = String.valueOf(serverZoneInfo.getIndex());
				
				Map<String, String> sendDataMap = new HashMap<String, String>();
					sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_SERVERINDEX, serverIndex);
				BaseJson result = dispatchToUnionServer(WebConstants.API_UNION_SERVER_QUERY_STATE, sendDataMap);
				if (result != null && result.getErrorCode() == ErrorConstants.SUCCESS) {
					Map<String, Object> mapResult = gson.fromJson(gson.toJson(result.getResult()), new TypeToken<Map<String, Object>>() {
					}.getType());
					int status = (int) Double.parseDouble(String.valueOf(mapResult.get("curState")));
					String fightId = mapResult.get("unionFightResultUniqueId").toString();
					long count = unionWarInfoDao.count(new Query(Criteria.where(AttributeConstants.serverId).is(UserDataService.serverIdLocal.get())));
						UnionWarInfo unionWarInfo = unionWarInfoDao.getById(fightId, serverIndex);
					if (unionWarInfo == null && count != 0) {
						// 首先获取到上一次的fightId
							boolean requestResult = false;
						logger.info("unionWarInfo is null and count is not 0, fightId is " + fightId);
							UnionWarInfo latest = unionWarInfoDao.latest(serverIndex);
						unionWarInfo = new UnionWarInfo();
						unionWarInfo.setOldId(fightId);
						unionWarInfo.setId(UnionWarInfo.convertOldIdToNewId(fightId));
						unionWarInfo.setCreateTime(System.currentTimeMillis());
							unionWarInfo.setServerId(serverIndex);
						if (latest.getIsEndMessageAndAwardSend() == 0) {
								requestResult = warEnd(latest, serverIndex);
								if (requestResult) {
									latest.setIsEndMessageAndAwardSend(1);
									unionWarInfoDao.save(latest);
									unionWarInfoDao.save(unionWarInfo);
								}
							}
					} else if (unionWarInfo == null && count == 0) {
						logger.info("unionWarInfo is null and count is 0, fightId is " + fightId);
						unionWarInfo = new UnionWarInfo();
						unionWarInfo.setId(UnionWarInfo.convertOldIdToNewId(fightId));
						unionWarInfo.setOldId(fightId);
						unionWarInfo.setCreateTime(System.currentTimeMillis());
						unionWarInfo.setServerId(serverIndex);
						unionWarInfoDao.save(unionWarInfo);
					} else if (unionWarInfo != null) {
						logger.info("unionWarInfo is not null fightId is " + fightId);
						if (status == UnionConstants.UNION_STATE_SELTWO_SHOWTIME) {
							if (unionWarInfo.getIsStartMessageSend() == 0) {
									warStart(fightId, serverIndex);
								unionWarInfo.setIsStartMessageSend(1);
								unionWarInfoDao.save(unionWarInfo);
							}
						}
					}
				}
	}
	
	public void warStart(String fightId, String currentServerId) {
		logger.info("war Start and sendMail fightId is " + fightId);
		String currentPlatform = GameData.getUnionConstNameMapValue().get(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM);
		Map<String, String> sendDataMap = new HashMap<String, String>();
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_SERVERINDEX, currentServerId);
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM, currentPlatform);
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_TIME, System.currentTimeMillis() + "");
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_UNION_PLANT_PREFIX, fightId);

		String sig = WebUtil.generateSig(sendDataMap);
		sendDataMap.put(WebConstants.PARAM_SIG, sig);
		BaseJson resultJson = dispatchToUnionServer(WebConstants.API_UNION_SERVER_GET_PLANTS_DECLARE_UNION_S2S, sendDataMap);

		if (resultJson.getErrorCode() == ErrorConstants.SUCCESS) {

			Map<String, List<String>> map = new HashMap<String, List<String>>();
			String str = gson.toJson(resultJson.getResult());
			UnionDeclareResultOut declareResultOut = gson.fromJson(str, UnionDeclareResultOut.class);
			if (declareResultOut.getCurState() >= UnionConstants.UNION_STATE_SELTWO_SHOWTIME && declareResultOut.getCurState() < UnionConstants.UNION_STATE_FIGHTING) {
				getTop2(declareResultOut.getUnionDeclareUnitOut001List(), map, UnionConstants.UNION_PLANT_001);
				getTop2(declareResultOut.getUnionDeclareUnitOut002List(), map, UnionConstants.UNION_PLANT_002);
				getTop2(declareResultOut.getUnionDeclareUnitOut003List(), map, UnionConstants.UNION_PLANT_003);
				getTop2(declareResultOut.getUnionDeclareUnitOut004List(), map, UnionConstants.UNION_PLANT_004);
				getTop2(declareResultOut.getUnionDeclareUnitOut005List(), map, UnionConstants.UNION_PLANT_005);
				getTop2(declareResultOut.getUnionDeclareUnitOut006List(), map, UnionConstants.UNION_PLANT_006);
				getTop2(declareResultOut.getUnionDeclareUnitOut007List(), map, UnionConstants.UNION_PLANT_007);
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					String key = entry.getKey();
					String starName = unionService.getPlantName(key);
					List<String> value = entry.getValue();
					for (String id : value) {
						if (!id.equals("")) {
							String serverId = getUnionServerId(id);
							String unionId = getUnionId(id);
							if (currentServerId.equals(serverId)) {
								List<UnionPlayer> unionPlayerList = unionPlayerDao.getUnionPlayerIds(unionId);
								// 获取对手id信息
								String otherId = getOtherUnionId(value, id).equals("") ? "" : getUnionId(getOtherUnionId(value, id));
								UnionData unionData = unionDataDao.findByID(UnionData.convertOldToNew(otherId));
								String otherName = unionData == null ? "" : unionData.getBaseInfo().getName();
								
								insertUnionEvent(serverId,unionId,GameData.getString(UnionConstants.UNION_EVENT_MESSAGE_WAR_START),starName);
								
								// 根据unionId来获取全体所有员工
								for (UnionPlayer up : unionPlayerList) {
									logger.info("war start top2 mail, userId is " + up.getUserId() + " enemyName is " + otherName + "starName is " + starName);
									MailNormal mailNormal = new MailNormal();
									mailNormal.setUserId(up.getUserId());
									mailNormal.setFrom(MailConstants.FROM_SYSTEM_USER_ID);
									mailNormal.setFromName(GameData.getString(MailConstants.FROM_SYSTEM_USER_NAME));
									mailNormal.setTime(System.currentTimeMillis());
									String contents = String.format(GameData.getString("union_war_start"), otherName, starName);
									mailNormal.setContents(contents);
									mailNormal.setGet(MailConstants.READ_NOT);
									try {
										mailService.saveMail(mailNormal);
									} catch (ErrorException e) {
										logger.error("",e);
									}
								}
							}
						}
					}
				}

				// 给没有资格参加的公会发消息
				sendMailList(declareResultOut.getUnionDeclareUnitOut001List(), map,UnionConstants.UNION_PLANT_001, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut002List(), map,UnionConstants.UNION_PLANT_002, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut003List(), map,UnionConstants.UNION_PLANT_003, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut004List(), map,UnionConstants.UNION_PLANT_004, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut005List(), map,UnionConstants.UNION_PLANT_005, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut006List(), map,UnionConstants.UNION_PLANT_006, fightId, currentServerId);
				sendMailList(declareResultOut.getUnionDeclareUnitOut007List(), map,UnionConstants.UNION_PLANT_007, fightId, currentServerId);
			}
		}
	}

	private void getTop2(List<UnionDeclareUnitOut> declareList, Map<String, List<String>> map, String plantId) {
		List<String> top2List = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			if (declareList.size() > i) {
				String id = declareList.get(i).getUnionFightUnit().getId();
				if (id != null && !id.equals("")) {
					top2List.add(id);
				}
			}
		}
		map.put(plantId, top2List);
	}
	
	private void sendMailList(List<UnionDeclareUnitOut> declareList, Map<String, List<String>> map,String key, String fightId, String currentServerId) {
		List<String> top2List = map.get(key);
		String starName = unionService.getPlantName(key);		
		for (UnionDeclareUnitOut unionDeclareUnitOut : declareList) {
			if (!top2List.contains(unionDeclareUnitOut.getUnionFightUnit().getId())) {
				String id = unionDeclareUnitOut.getUnionFightUnit().getId();
				String serverId = getUnionServerId(id);
				String unionId = getUnionId(id);
				insertUnionEvent(serverId,unionId,GameData.getString(UnionConstants.UNION_EVENT_MESSAGE_WAR_END),starName);
				
				sendMail(fightId, id, String.format(GameData.getString("union_war_start_faile")), currentServerId);
			}
		}
	}

	private void sendMail(String fightId, String id, String contents, String currentServerId) {
		String serverId = getUnionServerId(id);
		String unionId = getUnionId(id);
		if (currentServerId.equals(serverId)) {
			List<UnionPlayer> unionPlayerList = unionPlayerDao.getUnionPlayerIds(unionId);
			for (UnionPlayer up : unionPlayerList) {
				logger.info("war start remain mail, userId is " + up.getUserId());
				MailNormal mailNormal = new MailNormal();
				mailNormal.setUserId(up.getUserId());
				mailNormal.setFrom(MailConstants.FROM_SYSTEM_USER_ID);
				mailNormal.setFromName(GameData.getString(MailConstants.FROM_SYSTEM_USER_NAME));
				mailNormal.setTime(System.currentTimeMillis());
				mailNormal.setContents(contents);
				mailNormal.setGet(MailConstants.READ_NOT);
				try {
					mailService.saveMail(mailNormal);
				} catch (ErrorException e) {
					logger.error("",e);
				}
			}
		}
	}

	private String getOtherUnionId(List<String> list, String string) {
		if (list.size() == 2) {
			int index = list.indexOf(string);
			int otherIndex = list.size() - index - 1;
			return list.get(otherIndex);
		} else if (list.size() == 1) {
			return "";
		}
		return "";
	}

	private String getUnionServerId(String id) {
		if (id == null || id.equals("")) {
			return "";
		}
		String[] strs = id.split("_");
		String serverId = strs[1];
		return serverId;
	}

	private String getUnionId(String id) {
		if (id == null || id.equals("")) {
			return "";
		}
		String[] strs = id.split("_");
		String unionId = strs[2];
		return unionId;
	}

	private int getUserId(String id) {
		if (id == null || id.equals("")) {
			return 0;
		}
		String[] strs = id.split("_");
		Integer userId = Integer.parseInt(strs[1]);
		return userId;
	}

	private String getUserServerId(String id) {
		if (id == null || id.equals("")) {
			return "";
		}
		String[] strs = id.split("_");
		String serverId = strs[0];
		return serverId;
	}

	public BaseJson dispatchToUnionServer(String api, Map<String, String> sendDataMap) {

		BaseJson resultJson = null;

		String unionServerIp = GameData.getUnionConstNameMapValue().get("UNION_SERVER");

		String url = "http://" + unionServerIp + "/dragon" + api;
		// String url = "http://192.168.1.50:8082/dragon" + api;
		try {
			logger.info("<<<<>>>>>dispath notify to url=" + url);
			String currentPlatform = GameData.getUnionConstNameMapValue().get(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM);
			sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM, currentPlatform);
			sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_TIME, System.currentTimeMillis() + "");

			String sig = WebUtil.generateSig(sendDataMap);
			sendDataMap.put(WebConstants.PARAM_SIG, sig);
			String jsonStr = HttpUtil.httpPost(url, sendDataMap);
			logger.info("<<<<>>>>> jsonString is " + jsonStr);
			resultJson = gson.fromJson(jsonStr, BaseJson.class);
		} catch (Exception e) {
			logger.error(e);
		}

		return resultJson;
	}
	
	public boolean warEnd(UnionWarInfo unionWarInfo, String currentServerId) {
		return warEnd(unionWarInfo,true,
				GameData.getString("union_war_end_success_no_enemy"),
				GameData.getString("union_war_end_success"),
				GameData.getString("union_war_end_faile"),
				GameData.getString("union_top_award"),
				GameData.getString("union_common_award"),
				currentServerId);
	}

	public boolean warEnd(UnionWarInfo unionWarInfo) {
		return warEnd(unionWarInfo,true,
				GameData.getString("union_war_end_success_no_enemy"),
				GameData.getString("union_war_end_success"),
				GameData.getString("union_war_end_faile"),
				GameData.getString("union_top_award"),
				GameData.getString("union_common_award"),
				GameData.getServerConfigMap().get(SystemConstants.SERVER_CONFIG_NAME_SERVER_INDEX));
	}
	
	public boolean warEnd(UnionWarInfo unionWarInfo,boolean needInsertUnionEvent,String noEnemy,String endSuccess,String endFailed,String topAwardContentsTemplate,String commonAwardContentsTemplate, String currentServerId) {
		boolean requestResult = false;
		String fightId = unionWarInfo.getOldId();
		String currentPlatform = GameData.getUnionConstNameMapValue().get(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM);
		Map<String, String> sendDataMap = new HashMap<String, String>();
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_SERVERINDEX, currentServerId);
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM, currentPlatform);
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_TIME, System.currentTimeMillis() + "");
		sendDataMap.put(UnionConstants.UNION_SERVER_CLIENT_UNION_PLANT_PREFIX, fightId);

		String sig = WebUtil.generateSig(sendDataMap);
		sendDataMap.put(WebConstants.PARAM_SIG, sig);
		BaseJson resultJson = dispatchToUnionServer(WebConstants.API_UNION_SERVER_GET_FIGHTS_RESULT_UNION_S2S, sendDataMap);
		if (resultJson.getErrorCode() == ErrorConstants.SUCCESS) {
			String str = gson.toJson(resultJson.getResult());
			UnionFightResultOutS2S fightResultOut = gson.fromJson(str, UnionFightResultOutS2S.class);
			List<UnionFightResult> fightResultList = fightResultOut.getUnionFightResultList();
			List<UnionWarPlantTopAward> plantAwards = new ArrayList<UnionWarPlantTopAward>();
			List<UnionWarContribute> cList = new ArrayList<UnionWarContribute>();
			for (UnionFightResult fightResult : fightResultList) {
				String winnerId = fightResult.getUnionFight01();
				String faileId = fightResult.getUnionFight02();
				int result = fightResult.getUnionFightResult();
				if (result == 2) {
					winnerId = fightResult.getUnionFight02();
					faileId = fightResult.getUnionFight01();
				}

				String serverIdWinner = "";
				String unionIdWinner = "";
				if (!winnerId.equals("")) {
					serverIdWinner = getUnionServerId(winnerId);
					if (currentServerId.equals(serverIdWinner)) {
						unionIdWinner = getUnionId(winnerId);
					}
				}

				String serverIdFaile = "";
				String unionIdFaile = "";
				if (!faileId.equals("")) {
					serverIdFaile = getUnionServerId(faileId);
					if (currentServerId.equals(serverIdFaile)) {
						unionIdFaile = getUnionId(faileId);
					}
				}

				UnionData winnerData = unionDataDao.findByID(UnionData.convertOldToNew(unionIdWinner));
				UnionData faileData = unionDataDao.findByID(UnionData.convertOldToNew(unionIdFaile));
				String winnerName = winnerData == null ? "" : winnerData.getBaseInfo().getName();
				String faileName = faileData == null ? "" : faileData.getBaseInfo().getName();
				String succContents = "";
				String starName = unionService.getPlantName(getPlantId(fightResult));
				if(!StringUtils.isEmpty(noEnemy) && !StringUtils.isEmpty(endSuccess) && !StringUtils.isEmpty(endFailed)){
				if (faileName.equals("")) {
						succContents = String.format(noEnemy, starName);
				} else {
						succContents = String.format(endSuccess, faileName, starName);
				}

					if(needInsertUnionEvent){
				insertUnionEvent(serverIdWinner,unionIdWinner,GameData.getString(UnionConstants.UNION_EVENT_MESSAGE_WAR_WIN),starName);
				insertUnionEvent(serverIdFaile,unionIdFaile,GameData.getString(UnionConstants.UNION_EVENT_MESSAGE_WAR_FAILED),starName);
					}
					sendWarEndMail(winnerData, currentServerId, succContents, serverIdWinner, unionIdWinner);
					sendWarEndMail(faileData, currentServerId, String.format(endFailed, winnerName), serverIdFaile, unionIdFaile);
				
			}
				calculateTop(fightResult, unionWarInfo, plantAwards, cList, winnerData,topAwardContentsTemplate, commonAwardContentsTemplate,currentServerId);
			}
			requestResult = true;
		}
		return requestResult;
	}

	private void sendWarEndMail(UnionData unionData, String currentServerId, String contents, String serverId, String unionId) {
		if (unionData != null) {
			if (currentServerId.equals(serverId)) {
				List<UnionPlayer> unionPlayerList = unionPlayerDao.getUnionPlayerIds(unionId);
				// 根据unionId来获取全体所有员工
				for (UnionPlayer up : unionPlayerList) {
					logger.info("war end mail userId is " + up.getUserId());
					MailNormal mailNormal = new MailNormal();
					mailNormal.setUserId(up.getUserId());
					mailNormal.setFrom(MailConstants.FROM_SYSTEM_USER_ID);
					mailNormal.setFromName(GameData.getString(MailConstants.FROM_SYSTEM_USER_NAME));
					mailNormal.setTime(System.currentTimeMillis());
					mailNormal.setContents(contents);
					mailNormal.setGet(MailConstants.READ_NOT);
					try {
						mailService.saveMail(mailNormal);
					} catch (ErrorException e) {
						logger.error("",e);
					}
				}
			}
		}
	}

	private void calculateTop(UnionFightResult unionFightResult, UnionWarInfo unionWarInfo, List<UnionWarPlantTopAward> plantAwards, List<UnionWarContribute> cList, UnionData winnerData,String topAwardContentsTemplate ,String commonAwardContentsTemplate, String currentServerId) {
		UnionWarPlantTopAward plantTopAward = new UnionWarPlantTopAward();
		Map<String, Integer> attackRank01Map = new HashMap<String, Integer>();
		Map<String, Integer> attackRank02Map = new HashMap<String, Integer>();
		List<UnionFightPlayer> unionFightPlayer01List = new ArrayList<UnionFightPlayer>();
		List<UnionFightPlayer> unionFightPlayer02List = new ArrayList<UnionFightPlayer>();
		Map<String, Integer> userGainContributeMap = new HashMap<String, Integer>();
		Map<String, String> unionConstMap = GameData.getUnionConstNameMapValue();
		final String UNION_WAR_CONTRIBUTE_WIN_STR = unionConstMap.get("UNION_WAR_CONTRIBUTE_WIN");
		final String UNION_WAR_CONTRIBUTE_LOST_STR = unionConstMap.get("UNION_WAR_CONTRIBUTE_LOST");
		final String UNION_WAR_WINNER_AWARD_SCORE_STR = unionConstMap.get("UNION_WAR_WINNER_AWARD_SCORE");
		final String UNION_WAR_COMMON_AWARD_STR = unionConstMap.get("UNION_WAR_COMMON_AWARD");
		final int UNION_WAR_CONTRIBUTE_WIN = NumUtil.getInt(UNION_WAR_CONTRIBUTE_WIN_STR, 3);
		final int UNION_WAR_CONTRIBUTE_LOST = NumUtil.getInt(UNION_WAR_CONTRIBUTE_LOST_STR, 1);
		final int WAR_WINNER_AWARD_SCORE =  NumUtil.getInt(UNION_WAR_WINNER_AWARD_SCORE_STR,UnionData.WAR_WINNER_AWARD_SCORE);
		
		for (UnionPlayerFightInfo unionPlayerFight : unionFightResult.getUnionPlayerFightInfoList()) {

			String winnerId = "";
			String faileId = "";
			if (unionPlayerFight.getFightResult() == 1) {
				// 如果是01方胜利
				winnerId = unionPlayerFight.getUnionPlayer01Id();
				faileId = unionPlayerFight.getUnionPlayer02Id();
			} else if (unionPlayerFight.getFightResult() == 2) {
				winnerId = unionPlayerFight.getUnionPlayer02Id();
				faileId = unionPlayerFight.getUnionPlayer01Id();
			}
			if (!winnerId.equals("")) {
				// String serverId = getUserServerId(winnerId);
				// Integer userId = getUserId(winnerId);
				if (unionPlayerFight.getFightResult() == 1) {
					Integer attackCount = attackRank01Map.get(winnerId);
					if (attackCount == null) {
						attackRank01Map.put(winnerId, 1);
						UnionFightPlayer unionFightPlayer = new UnionFightPlayer();
						unionFightPlayer.setUserUnique(winnerId);
						unionFightPlayer01List.add(unionFightPlayer);
					} else {
						attackRank01Map.put(winnerId, attackCount + 1);
					}
				} else if (unionPlayerFight.getFightResult() == 2) {
					Integer attackCount = attackRank02Map.get(winnerId);
					if (attackCount == null) {
						attackRank02Map.put(winnerId, 1);
						UnionFightPlayer unionFightPlayer = new UnionFightPlayer();
						unionFightPlayer.setUserUnique(winnerId);
						unionFightPlayer02List.add(unionFightPlayer);
					} else {
						attackRank02Map.put(winnerId, attackCount + 1);
					}
				}

				Integer gainContribute = userGainContributeMap.get(winnerId);
				if (gainContribute == null) {
					userGainContributeMap.put(winnerId, UNION_WAR_CONTRIBUTE_WIN);
				} else {
					userGainContributeMap.put(winnerId, gainContribute + UNION_WAR_CONTRIBUTE_WIN);
				}
			}
			if (!faileId.equals("")) {
				// String serverId = getUserServerId(faileId);
				// Integer userId = getUserId(faileId);
				Integer gainContribute = userGainContributeMap.get(faileId);
				if (gainContribute == null) {
					userGainContributeMap.put(faileId, UNION_WAR_CONTRIBUTE_LOST);
				} else {
					userGainContributeMap.put(faileId, gainContribute + UNION_WAR_CONTRIBUTE_LOST);
				}
			}

		}
		// 对attackRankMap按照value进行倒序排序
		for (UnionFightPlayer unionFightPlayer : unionFightPlayer01List) {
			Integer attackCount = attackRank01Map.get(unionFightPlayer.getUserUnique());
			unionFightPlayer.setScore(attackCount);
		}
		UnionService.sort(unionFightPlayer01List);
		for (UnionFightPlayer unionFightPlayer : unionFightPlayer02List) {
			Integer attackCount = attackRank02Map.get(unionFightPlayer.getUserUnique());
			unionFightPlayer.setScore(attackCount);
		}
		UnionService.sort(unionFightPlayer02List);

		String plantId = getPlantId(unionFightResult);
		// 获取前3个
		plantTopAward.setAwards01(getAttackTop3(unionFightPlayer01List, plantId, currentServerId));
		plantTopAward.setAwards02(getAttackTop3(unionFightPlayer02List, plantId, currentServerId));
		plantTopAward.setPlantId(plantId);
		plantAwards.add(plantTopAward);
		unionWarInfo.setTopAwards(plantAwards);

		for (Map.Entry<String, Integer> entry : userGainContributeMap.entrySet()) {
			String kuafuUserId = entry.getKey();
			String serverId = getUserServerId(kuafuUserId);
			Integer userId = getUserId(kuafuUserId);
			if (currentServerId.equals(serverId)) {
				UnionWarContribute warContribute = new UnionWarContribute();
				warContribute.setGainContribute(entry.getValue());
				warContribute.setUserId(userId);
				cList.add(warContribute);
			}
		}
		unionWarInfo.setGainContributes(cList);

		List<UnionWarCommonAward> union01CommonAwardList = getCommonAwards(unionFightPlayer01List, currentServerId,UNION_WAR_COMMON_AWARD_STR);
		List<UnionWarCommonAward> union02CommonAwardList = getCommonAwards(unionFightPlayer02List, currentServerId,UNION_WAR_COMMON_AWARD_STR);
		union01CommonAwardList.addAll(union02CommonAwardList);
		unionWarInfo.getCommonAwards().addAll(union01CommonAwardList);

		giveContribute(cList);
		giveTopAward(plantTopAward, topAwardContentsTemplate);
		giveUnionScore(winnerData, plantTopAward,WAR_WINNER_AWARD_SCORE);
		giveCommonAward(union01CommonAwardList,commonAwardContentsTemplate);
	}

	private List<UnionWarTopAward> getAttackTop3(List<UnionFightPlayer> unionFightPlayerList, String plantId, String currentServerId) {
		List<UnionWarTopAward> awardList = new ArrayList<UnionWarTopAward>();
		for (int i = 0; i < 3; i++) {
			String awardItem = "";
			UnionWarTopAward topAward = new UnionWarTopAward();

			if (unionFightPlayerList.size() > i) {
				UnionFightPlayer unionFightPlayer = unionFightPlayerList.get(i);
				String sortUserId = unionFightPlayer.getUserUnique();
				Integer userId = getUserId(sortUserId);
				String serverId = getUserServerId(sortUserId);
				if (currentServerId.equals(serverId)) {
					UnionFightAttackTopOut attackTopOut = new UnionFightAttackTopOut();
					attackTopOut.setRank(i);
					if (i == 0) {
						awardItem = GameData.getUnionWarAttackAwardMap().get(plantId).getFirstAward();
					} else if (i == 1) {
						awardItem = GameData.getUnionWarAttackAwardMap().get(plantId).getSecondAward();
					} else if (i == 2) {
						awardItem = GameData.getUnionWarAttackAwardMap().get(plantId).getThirdAward();
					}
					topAward.setAwardItem(awardItem);
					topAward.setStatus(0);
					topAward.setUserId(userId);
					awardList.add(topAward);
				}
			}
		}
		return awardList;
	}

	public List<UnionWarCommonAward> getCommonAwards(List<UnionFightPlayer> unionFightPlayerList, String currentServerId, String union_war_common_award_str) {
		/**
		 * 读取配置信息 根据配置字符串 创建一个Map 和 List
		 */
		if(StringUtils.isEmpty(union_war_common_award_str)){
			union_war_common_award_str = "20=TypeItem,5,item_012;15=TypeItem,4,item_012;10=TypeItem,3,item_012;5=TypeItem,2,item_012;0=TypeItem,1,item_012";
		}
		String[] configs = union_war_common_award_str.split(";");
		Map<Integer,String> awardConfigMap = new HashMap<Integer, String>();
		List<Integer> awardConfigKeyList = new ArrayList<Integer>();
		for (int i = 0; i < configs.length; i++) {
			if(!StringUtils.isEmpty(configs[i])){//20=TypeItem,5,item_012
				String[] configStr = configs[i].split("=");
				awardConfigMap.put(NumUtil.getInt(configStr[0]), configStr[1]);
				awardConfigKeyList.add(NumUtil.getInt(configStr[0]));
			}
		}
		/**
		 * 对List进行从小到大排序 方便计算
		 */
		Collections.sort(awardConfigKeyList);
		
		List<UnionWarCommonAward> commonAwards = new ArrayList<UnionWarCommonAward>();
		for (int i = 0; i < unionFightPlayerList.size(); i++) {
			UnionFightPlayer unionFightPlayer = unionFightPlayerList.get(i);
			String sortUserId = unionFightPlayer.getUserUnique();
			Integer userId = getUserId(sortUserId);
			String serverId = getUserServerId(sortUserId);
			int attackCount = unionFightPlayer.getScore();
			if (currentServerId.equals(serverId)) {
				commonAwards.add(calculateCommonAward(userId, attackCount,awardConfigMap,awardConfigKeyList));
			}
		}
		return commonAwards;
	}

	private UnionWarCommonAward calculateCommonAward(int userId, int attackCount, Map<Integer, String> awardConfigMap, List<Integer> awardConfigKeyList) {
		UnionWarCommonAward commonAward = new UnionWarCommonAward();
		Integer lastKey = awardConfigKeyList.get(0);
		String awards = "";
		if(attackCount > 0){
			awards = awardConfigMap.get(lastKey);
			for (Integer integer : awardConfigKeyList) {
				if(attackCount >= integer){
					lastKey = integer;
		}
				awards = awardConfigMap.get(lastKey);
			}
		}
		commonAward.setAwards(awards);
		commonAward.setUserId(userId);
		commonAward.setStatus(0);
		return commonAward;
	}

	private String getPlantId(UnionFightResult unionFightResult) {
		String plantId = unionFightResult.getId().split("_")[2];
		return plantId;
	}

	// 发送前三的奖励
	private void giveTopAward(UnionWarPlantTopAward plantTopAward, String topAwardContentsTemplate) {
		if (plantTopAward.getStatus() == UnionWarPlantTopAward.UN_GIVE_AWARD) {
			List<UnionWarTopAward> topAwards01 = plantTopAward.getAwards01();
			for (UnionWarTopAward topAward : topAwards01) {
				if (topAward.getStatus() == UnionWarPlantTopAward.UN_GIVE_AWARD) {
					sendAwardMail(topAward,topAwardContentsTemplate);
				}
			}
			List<UnionWarTopAward> topAwards02 = plantTopAward.getAwards02();
			for (UnionWarTopAward topAward : topAwards02) {
				if (topAward.getStatus() == UnionWarPlantTopAward.UN_GIVE_AWARD) {
					sendAwardMail(topAward, topAwardContentsTemplate);
				}
			}
			plantTopAward.setStatus(UnionWarPlantTopAward.GIVE_AWARD);
		}
	}

	private void sendAwardMail(UnionWarTopAward topAward, String topAwardContentsTemplate) {
		MailNormal mailNormal = new MailNormal();
		int userId = topAward.getUserId();
		mailNormal.setUserId(topAward.getUserId());
		mailNormal.setFrom(MailConstants.FROM_SYSTEM_USER_ID);
		mailNormal.setFromName(GameData.getString(MailConstants.FROM_SYSTEM_USER_NAME));
		mailNormal.setTime(System.currentTimeMillis());
		String awardItem = topAward.getAwardItem();
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		try {
			resultObjectList = ProductService.getResultObjectByTypicalStr(awardItem, true);
			String contents = String.format(topAwardContentsTemplate, genernateAwardName(resultObjectList));
			mailNormal.setContents(contents);
		} catch (ErrorException e) {
			logger.error("",e);
		}

		mailNormal.setGetList(resultObjectList);
		mailNormal.setGet(MailConstants.READ_NOT);
		try {
			mailService.saveMail(mailNormal);
			topAward.setStatus(UnionWarPlantTopAward.GIVE_AWARD);
			logger.info("send top attack award userId is " + userId + " award is " + awardItem);
		} catch (ErrorException e) {
			logger.error("",e);
		}
	}

	private String genernateAwardName(List<ResultObject> itemList) {
		String awardName = "";
		if (!itemList.isEmpty()) {
			for (ResultObject resultObject : itemList) {
				if (StringUtils.isEmpty(awardName))
					awardName = "";
				else
					awardName += ",";
				if (resultObject.getNum() > 0) {
					awardName += ProductService.getNameByResult(resultObject);
				}
			}
		}
		return awardName;
	}

	private void giveContribute(List<UnionWarContribute> warContributeList) {
		for (UnionWarContribute warContribute : warContributeList) {
			if (warContribute.getStatus() == UnionWarPlantTopAward.UN_GIVE_AWARD) {
				int userId = warContribute.getUserId();
				int gainContribute = warContribute.getGainContribute();
				UnionPlayer unionPlayer = unionPlayerDao.findByID(UserData.convertOldToNew(userId));
				unionPlayer.setDkp(unionPlayer.getDkp() + gainContribute);
				unionPlayerDao.save(unionPlayer);
				warContribute.setStatus(UnionWarPlantTopAward.GIVE_AWARD);
				logger.info("send contribute userId is " + userId + " contribute is " + gainContribute);
			}
		}
	}

	private void giveUnionScore(UnionData unionData, UnionWarPlantTopAward plantTopAward, int warWinnerAwardScore) {
		if (unionData != null) {
			if (plantTopAward.getIsAwardUnionScore() == UnionWarPlantTopAward.UN_GIVE_AWARD) {
				unionData.getBaseInfo().setJifen(unionData.getBaseInfo().getJifen() + 50);
				Map<String, Object> queryConditions = new HashMap<String, Object>();
				queryConditions.put("_id", unionData.getGlobalUnionId());
				Map<String, Map<String, Object>> fields = new HashMap<String, Map<String, Object>>();
				Map<String, Object> incFields = new HashMap<String, Object>();
				incFields.put("baseInfo.jifen", warWinnerAwardScore);
				fields.put("inc", incFields);
				try {
					unionService.updateByFields(queryConditions, fields);
					plantTopAward.setIsAwardUnionScore(UnionWarPlantTopAward.GIVE_AWARD);
					logger.info("send union score unionId is " + unionData.getGlobalUnionId() + " score is " + warWinnerAwardScore);
				} catch (ErrorException e) {
					logger.error("failed! send union score unionId is " + unionData.getGlobalUnionId() + " score is " + warWinnerAwardScore, e);
				}
			}
		}

	}

	private void sendCommonAwardMail(UnionWarCommonAward commonAward,String commonAwardContentsTemplate) {
		MailNormal mailNormal = new MailNormal();
		int userId = commonAward.getUserId();
		mailNormal.setUserId(userId);
		mailNormal.setFrom(MailConstants.FROM_SYSTEM_USER_ID);
		mailNormal.setFromName(GameData.getString(MailConstants.FROM_SYSTEM_USER_NAME));
		mailNormal.setTime(System.currentTimeMillis());
		List<ResultObject> list = new ArrayList<ResultObject>();
		String awardItem = commonAward.getAwards();
		List<ResultObject> resultObjectList;
		try {
			resultObjectList = ProductService.getResultObjectByTypicalStr(awardItem, true);
			ResultObject resultObject = resultObjectList.get(0);
			list.add(resultObject);
			String contents = String.format(commonAwardContentsTemplate, ProductService.getNameByResult(resultObject));
			mailNormal.setContents(contents);
		} catch (ErrorException e) {
			logger.error("",e);
		}

		mailNormal.setGetList(list);
		mailNormal.setGet(MailConstants.READ_NOT);
		try {
			mailService.saveMail(mailNormal);
			commonAward.setStatus(1);
			logger.info("send common attack award userId is " + userId + " award is " + awardItem);
		} catch (ErrorException e) {
			logger.error("",e);
		}
	}

	private void giveCommonAward(List<UnionWarCommonAward> commonAwardList,String commonAwardContentsTemplate) {
		for (UnionWarCommonAward commonAward : commonAwardList) {
			if (commonAward.getStatus() == 0) {
				sendCommonAwardMail(commonAward,commonAwardContentsTemplate);
			}
		}
	}
	
	private void insertUnionEvent(String serverId, String unionId, String template, String... parame){
		String currentServerId = GameData.getServerConfigMap().get(SystemConstants.SERVER_CONFIG_NAME_SERVER_INDEX);
		if (currentServerId.equals(serverId)) {
			/**
			 * 公会未能进入决赛
			 */					
			String message = unionService.parse(template, parame);
			unionService.insertUnionEvent(unionId,UnionEvent.TYPE_FIGHT,message);
		}
	}
}
