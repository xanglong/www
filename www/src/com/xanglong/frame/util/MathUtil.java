package com.xanglong.frame.util;

import java.math.BigDecimal;
import java.util.Random;

/**数学工具类*/
public class MathUtil {

	/**随机种子，47魔法数*/
	private static Random random = new Random();

	/**
	 * 获取随机索引
	 * @param 索引长度，例如数组长度
	 * @return 随机返回数组的索引
	 * */
	public static int getRandomIndex(int length) {
		return random.nextInt(length);
	}
	
	/**
	 * 两数相除，保留2位小数
	 * @param beichushu 被除数
	 * @param chushu 除数
	 * @return 商
	 * */
	public static double divide(double beichushu, double chushu) {
		BigDecimal bigDecimal1 = new BigDecimal(Double.toString(beichushu));  
		BigDecimal bigDecimal2 = new BigDecimal(Double.toString(chushu));
		return bigDecimal1.divide(bigDecimal2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
}