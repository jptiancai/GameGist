package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cons.SystemConstants;

public class NumUtil {

	// 单服务器唯一随机数出处
	public static final Random RANDOM = new Random(System.currentTimeMillis());

	/**
	 * 解析INT字符串，出错返0
	 * 
	 * @param strToInt
	 * @return
	 */
	public static int getInt(String strToInt) {
		int value = 0;
		try {
			value = Integer.parseInt(strToInt);
		} catch (NumberFormatException e) {
		}
		return value;
	}
	
	public static long getLong(String strToLong){
		long value = 0;
		try {
			value = Long.parseLong(strToLong);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		return value;
	}

	/**
	 * 解析INT字符串，出错返0
	 * 
	 * @param strToInt
	 * @return
	 */
	public static int getInt(String strToInt, int value) {
		try {
			value = Integer.parseInt(strToInt);
		} catch (NumberFormatException e) {
		}
		return value;
	}

	/**
	 * 解析DOUBLE字符串，出错返0
	 * 
	 * @param toDouble
	 * @return
	 */
	public static double getDouble(String toDouble) {
		double value = 0;
		try {
			value = Double.parseDouble(toDouble);
		} catch (NumberFormatException e) {
		}
		return value;
	}

	/**
	 * 解析百分比的字符串格式，返回原值的解析X100
	 * 
	 * @param percentsStr
	 * @return
	 */
	public static double parsePercents(String percentsStr) {
		try {
			double value = Double.parseDouble(percentsStr);
			return value / 100;
		} catch (Exception e) {
		}
		return 0;
	}

	public static List<Integer> getIntegerList(String userIds) {
		List<Integer> integerList = new ArrayList<Integer>();
		try {
			for (String intStr : userIds.split(SystemConstants.SPLIT_AND)) {
				integerList.add(Integer.parseInt(intStr));
			}
		} catch (Exception e) {
		}
		return integerList;
	}
	
	public static int randomRange(int min, int max) {
		return RANDOM.nextInt(max) % (max - min + 1) + min;
	}
}
