import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * 随机运算工具类
 * 
 * 
 */
public final class RandomUtils extends org.apache.commons.lang.math.RandomUtils {
	public static final int RATE_BASE = 1000000000;

	private RandomUtils() {
		throw new IllegalAccessError("该类不允许实例化");
	}

	/**
	 * 是否命中
	 * 
	 * @param rate
	 * @return
	 */
	public static boolean isHit(double rate) {
		if (rate <= 0.0D) {
			return false;
		}
		int limit = (int) (rate * 1000000000.0D);
		int value = org.apache.commons.lang.math.RandomUtils.nextInt(1000000000);
		if (value <= limit) {
			return true;
		}
		return false;
	}

	/**
	 * 命中
	 * 
	 * @param rate
	 * @param random
	 * @return
	 */
	public static boolean isHit(double rate, Random random) {
		if (rate <= 0.0D) {
			return false;
		}
		if (rate >= 1) {
			return true;
		}
		int limit = (int) (rate * 1000000000.0D);
		int value = org.apache.commons.lang.math.RandomUtils.nextInt(random, 1000000000);
		if (value <= limit) {
			return true;
		}
		return false;
	}

	/**
	 * 随机中间值
	 * 
	 * @param min
	 *            最大值
	 * @param max
	 *            最小值
	 * @param include
	 *            是否是闭区间如返回[1,100]值为true， 如(1,100)值为false，目前不支持左开右闭和左闭右开，
	 *            当true时，返回min到max之间的数包括 1 和 100，，当false时，返回min到max之间的数不包括 1 和
	 *            100，
	 * @return
	 */
	public static int betweenInt(int min, int max, boolean include) {
		if (min > max)
			throw new IllegalArgumentException("最小值[" + min + "]不能大于最大值[" + max + "]");
		if ((!include) && (min == max)) {
			throw new IllegalArgumentException("不包括边界值时最小值[" + min + "]不能等于最大值[" + max + "]");
		}

		if (include)
			max++;
		else {
			min++;
		}
		return (int) (min + Math.random() * (max - min));
	}

	/**
	 * 返回命中对象，按照随机基数取出命中对象
	 * 
	 * @param <T>
	 * @param randomList
	 *            命中对象随机概率，要求按顺序概率从小到大，随机列表是累加的，假设a概率为1000，b概率为1000，传入列表是[1000,
	 *            2000]
	 * @param list
	 *            命中对象 注：命中对象与随机概率对象长度一致，如果没有对象命中返回为空
	 * @param baseRandom
	 *            随机基数
	 * @return
	 */
	public static <T> T hitObject(List<Integer> randomList, List<T> objectList, int baseRandom) {
		// 如果随机基数小于等于0
		if (baseRandom <= 0) {
			return null;
		}

		// 如果随机列表长度为0
		if (randomList == null || randomList.size() <= 0) {
			return null;
		}

		// 如果随机对列表长度为0
		if (objectList == null || objectList.size() <= 0) {
			return null;
		}

		// 如果随机对象列表长度不等于随机列表长度
		if (randomList.size() != objectList.size()) {
			return null;
		}

		// 生成随机数
		int hitNum = RandomUtils.betweenInt(1, baseRandom, true);

		// 索引下标
		int index = 0;
		for (int random : randomList) {
			// 如果命中
			if (hitNum <= random) {
				return objectList.get(index);
			}
			index++;
		}

		// 如果没有命中，返回空对象
		return null;
	}

	/**
	 * 返回命中对象，从集合中抽取随机概率相等的数量为num的对象集合
	 * 
	 * @param <T>
	 * @param objectList
	 *            集合对象
	 * @param baseRandom
	 * @param num
	 * @return
	 */
	public static <T> List<T> hitObjects(List<T> objectList, int num) {
		List<T> result = new ArrayList<T>();

		// 如果抽取对象数量小于等于0
		if (num <= 0) {
			return null;
		}

		// 如果随机对列表长度为0
		if (objectList == null || objectList.size() <= 0) {
			return null;
		}

		// 当num数量大于列表中数量，修正num，num为列表长度
		int getNum = num;
		if (num > objectList.size()) {
			getNum = objectList.size();
		}

		// 克隆参数列表
		List<T> cloneObjectList = new ArrayList<T>();
		for (T t : objectList) {
			cloneObjectList.add(t);
		}
		
		//如果全部取出
		if(getNum == objectList.size()){
			return cloneObjectList;
		}

		// 抽取getNum次
		for (int i = 0; i < getNum; i++) {
			// 随机一个数，取下标0到列表数量-1的数，即列表下标
			int getIndex = RandomUtils.betweenInt(0, cloneObjectList.size() - 1, true);
			// 获得对象
			T t = cloneObjectList.get(getIndex);
			// 存入结果
			result.add(t);
			// 删除克隆列表中对象
			cloneObjectList.remove(getIndex);
		}

		return result;
	}
	
	/**
	 * 按照不同权重，从列表中随机needNum个对象，对象不会重复
	 * @param weightList 原始的每个T的权重列表，不需要加和。如a,b权重分别为50，50 则list里面就是50，50
	 * @param objectList T列表
	 * @param needNum 需要T的个数
	 * @return 含有needNum个T的列表，非法时返回null
	 */
	public static <T> List<T> hitObjectsWithWeightNum(List<Integer> weightList, List<T> objectList, int needNum) {
		List<T> result = new ArrayList<T>();
		if (needNum <= 0) {
			return null;
		}
		//数据格式非法
		if (weightList.size() != objectList.size()) {
			return null;
		}
		
		//需要全部数据
		if (needNum >= objectList.size()) {
			result.addAll(objectList);
			return result;
		}
		
		List<Integer> wList = new ArrayList<Integer>();
		wList.addAll(weightList);
		List<T> oList = new ArrayList<T>();
		oList.addAll(objectList);
		
		List<Integer> randList = new ArrayList<Integer>();
		
		for (int i = 0; i < needNum; i++) {
			randList.clear();
			int weight = 0;
			for (int j = 0; j < wList.size(); j++) {
				weight += wList.get(j);
				randList.add(weight);
			}
			
			int hitNum = RandomUtils.betweenInt(1, weight, true);
			
			// 索引下标
			int hitIndex = 0;
			for (int random : randList) {
				// 如果命中
				if (hitNum <= random) {
					result.add(oList.get(hitIndex));
					//命中后排除命中对象
					wList.remove(hitIndex);
					oList.remove(hitIndex);
					break;
				}
				hitIndex++;
			}
		}

		return result;
	}
	
//	/**
//	 * 返回类似剑气拉杆的结果列表，只适用于固定3列的拉杆
//	 * @param cList
//	 * @return 拉杆结果列表，详情参见DrawBarResultInfo字段注释
//	 */
//	public static List<DrawBarResultInfo> drawBarBingoResult(Collection<? extends IDrawBarTemplate> cList) {
//		// 复制一份数据，否则参数就被修改了
//		List<? extends IDrawBarTemplate> list = new ArrayList<IDrawBarTemplate>(cList);
//		IDrawBarTemplate[] cells = new IDrawBarTemplate[3];
//		for(int i=0; i<3; i++){
//			if(calDrawbar(cells, list)){
//				break;
//			}
//		}
//		
//		Map<IDrawBarTemplate, Integer> map = new LinkedHashMap<IDrawBarTemplate, Integer>();
//		for(int i=0; i<cells.length; i++){
//			IDrawBarTemplate temp = cells[i];
//			if(temp == null){
//				continue;
//			}
//			
//			Integer num = map.get(temp);
//			num = num == null ? 0 : num;
//			map.put(temp, num + 1);
//		}
//		
//		List<DrawBarResultInfo> resultList = new ArrayList<DrawBarResultInfo>();
//		for(Entry<IDrawBarTemplate, Integer> entry : map.entrySet()){
//			DrawBarResultInfo info = new DrawBarResultInfo();
//			int num = entry.getValue();
//			if(num == 3){				
//				info.setTargetValue(entry.getKey().getBigCritValue());
//				info.setCritState(CritStateType.BIG_CRIT.getIndex());
//			}else if(num == 2){
//				info.setTargetValue(entry.getKey().getSmallCritValue());
//				info.setCritState(CritStateType.SMALL_CRIT.getIndex());
//			}else{
//				info.setTargetValue(entry.getKey().getNormalCritValue());
//				info.setCritState(CritStateType.NORMAL.getIndex());
//			}
//			info.setTplId(entry.getKey().getId());
//			info.setTargetId(entry.getKey().getTargetId());
//			resultList.add(info);
//		}
//		return resultList;
//	}
//	
//	/**
//	 * 拉杆子方法
//	 * @param cells
//	 * @param list
//	 * @return true 表示结束随机
//	 */
//	protected static boolean calDrawbar(IDrawBarTemplate[] cells, List<? extends IDrawBarTemplate> list){
//		// 判定退出
//		boolean flag = true;
//		for(int i=0; i<cells.length; i++){
//			if(cells[i] == null){
//				flag = false;
//				break;
//			}
//		}
//		if(flag){
//			return true;
//		}
//		
//		if(list.isEmpty()){
//			// 允许里面有null
////			IDrawBarTemplate lastTemp = null;
////			for(int i=0; i<cells.length; i++){
////				if(cells[i] != null){
////					lastTemp = cells[i];
////				}else{
////					break;
////				}
////			}
////			
////			for(int i=0; i<cells.length; i++){
////				if(cells[i] == null){
////					cells[i] = lastTemp;
////				}
////			}
//			
//			return true;
//		}
//		
//		int[] weightArray = new int[list.size()];
//		for(int i=0; i<list.size(); i++){
//			weightArray[i] = list.get(i).getWeight();
//		}
//		
//		int index = MathUtils.random(weightArray);
//		if (index < 0 || index >= list.size()) {
//			// 越界，非法，一般是没有将0权重的排除掉造成的
//			Loggers.gameLogger.warn("#RandomUtils#calDrawbar#index is invalide!index=" + index);
//			return true;
//		}
//		
//		IDrawBarTemplate temp = list.remove(index);
//		if (temp.getWeightArray().length != 3) {
//			// 必须是3，否则非法
//			return true;
//		}
//		int weightIndex = MathUtils.random(temp.getWeightArray());
//		CritStateType type = CritStateType.valueOf(weightIndex + 1);
//		for(int i=0; i<type.getCellNum(); i++){
//			for (int j = 0; j < cells.length; j++) {
//				if(cells[j] == null){
//					cells[j] = temp;
//					break;
//				}
//			}
//		}
//		return false;
//	}

	public static void main(String[] args) throws Exception {
		String configFile = "game_server.cfg.js";
		GameServer server = new GameServer(configFile);
		server.init();
		PetTalentSkillPackTemplate tpl = Globals.getTemplateCacheService().get(1, PetTalentSkillPackTemplate.class);
		List<Integer> ret = RandomUtils.hitObjectsWithWeightNum(tpl.getWeightList(), tpl.getSkillIdList(), 5);
		System.out.println(ret);
	}
}
