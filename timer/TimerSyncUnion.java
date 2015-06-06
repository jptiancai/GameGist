package service.server;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import cons.GameData;
import cons.SystemConstants;
import cons.UnionConstants;
import cons.WebConstants;
import mongodb.dao.UnionDataDao;
import mongodb.dao.UnionPlayerDao;
import mongodb.domain.UnionData;
import mongodb.domain.element.UnionBuildingData;
import mysql.dao.ServerZoneInfoDao;
import mysql.domain.BuildingUnionProperty;
import mysql.domain.ServerZoneInfo;
import output.BaseJson;
import output.UnionSyncInfo;
import output.UnionSyncServerInfo;
import service.UnionServerService;
import service.UserDataService;
import service.timer.BaseTimerTask;
import utils.HttpUtil;
import utils.LogFactory;

public class TimerSyncUnion extends BaseTimerTask{

	private static final Log logger = LogFactory.getLog(TimerSyncUnion.class);

	@Autowired
	private UnionDataDao unionDataDao;

	@Autowired
	private UnionServerService unionServerService;

	@Autowired
	private UnionPlayerDao unionPlayerDao;

	@Autowired
	private ServerZoneInfoDao serverZoneInfoDao;

	private Gson gson = new Gson();
	
	private static final String MEMCACHED_KEY = "TimerSyncUnionTimer";
	
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

			Gson gson = new Gson();
			int totalCount = unionDataDao.getUnionDataCount();
			int limit = 100;
			String currentPlatform = GameData.getUnionConstNameMapValue().get(UnionConstants.UNION_SERVER_CLIENT_CURRENTPLATFORM);
			UnionSyncServerInfo serverInfo = new UnionSyncServerInfo();
			for (int i = 0; i < totalCount; i += 100) {
				if ((i + 100) > totalCount) {
					limit = totalCount - i;
				}
				List<UnionData> unionDataList = unionDataDao.getUnionDataList(i, limit);
				List<UnionSyncInfo> syncInfoList = new ArrayList<UnionSyncInfo>();
				for (UnionData unionData : unionDataList) {
					String unionPersistUniqueId = unionServerService.getUnionPersistUniqueId(Integer.parseInt(serverIndex), unionData.getUnionId(), currentPlatform);
					int level = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_BASE).getLevel();
					UnionSyncInfo syncInfo = new UnionSyncInfo();

					syncInfo.setId(unionPersistUniqueId);
					syncInfo.setServerIndex(Integer.parseInt(serverIndex));
					syncInfo.setPlatform(currentPlatform);
					syncInfo.setName(unionData.getBaseInfo().getName());
					syncInfo.setLevel(level);
					syncInfo.setMaxJoinMemberNum(15);
					syncInfo.setMemberNum(unionPlayerDao.getUnionPlayerCount(unionData.getUnionId()));
					// 获取五行的建筑信息
					UnionBuildingData metalBuilding = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_PROPERTY_METAL);
					BuildingUnionProperty metalProperty = GameData.getBuildingUnionPropertyMap().get(UnionBuildingData.BUILDING_PROPERTY_METAL).get(metalBuilding.getLevel());
					syncInfo.setMetalLevel(metalBuilding.getLevel());
					syncInfo.setMetalHp(metalProperty.getHp());

					UnionBuildingData woodBuilding = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_PROPERTY_WOOD);
					BuildingUnionProperty woodProperty = GameData.getBuildingUnionPropertyMap().get(UnionBuildingData.BUILDING_PROPERTY_WOOD).get(woodBuilding.getLevel());
					syncInfo.setWoodLevel(woodBuilding.getLevel());
					syncInfo.setWoodHp(woodProperty.getHp());

					UnionBuildingData waterBuilding = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_PROPERTY_WATER);
					BuildingUnionProperty waterProperty = GameData.getBuildingUnionPropertyMap().get(UnionBuildingData.BUILDING_PROPERTY_WATER).get(waterBuilding.getLevel());
					syncInfo.setWaterLevel(waterBuilding.getLevel());
					syncInfo.setWaterHp(waterProperty.getHp());

					UnionBuildingData fireBuilding = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_PROPERTY_FIRE);
					BuildingUnionProperty fireProperty = GameData.getBuildingUnionPropertyMap().get(UnionBuildingData.BUILDING_PROPERTY_FIRE).get(fireBuilding.getLevel());
					syncInfo.setFireLevel(fireBuilding.getLevel());
					syncInfo.setFireHp(fireProperty.getHp());

					UnionBuildingData earthBuilding = unionData.getBuildingDataMap().get(UnionBuildingData.BUILDING_PROPERTY_EARTH);
					BuildingUnionProperty earthProperty = GameData.getBuildingUnionPropertyMap().get(UnionBuildingData.BUILDING_PROPERTY_EARTH).get(earthBuilding.getLevel());
					syncInfo.setEarthLevel(earthBuilding.getLevel());
					syncInfo.setEarthHp(earthProperty.getHp());
					syncInfoList.add(syncInfo);
				}
				serverInfo.setUnionSyncInfoList(syncInfoList);
				String syncInfoListStr = gson.toJson(serverInfo);
				try {
					syncInfoListStr = URLEncoder.encode(syncInfoListStr, "UTF-8");
				} catch (Exception e) {
					logger.error("",e);
				}
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("sync", syncInfoListStr);
				dispatchToUnionServer(paramMap);
			}

	}

	private BaseJson dispatchToUnionServer(Map<String, String> paramMap) {

		BaseJson resultJson = null;

		String unionServerIp = GameData.getUnionConstNameMapValue().get("UNION_SERVER");

		String url = "http://" + unionServerIp + "/dragon" + WebConstants.API_UNION_SERVER_SYNC;
		try {
			logger.info("<<<<>>>>>dispath notify to url=" + url);
			String jsonStr = HttpUtil.httpPost(url, paramMap);
			logger.info("<<<<>>>>> jsonString is " + jsonStr);
			resultJson = gson.fromJson(jsonStr, BaseJson.class);
		} catch (Exception e) {
			logger.error(e);
		}

		return resultJson;
	}

}
