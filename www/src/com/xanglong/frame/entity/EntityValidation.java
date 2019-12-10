package com.xanglong.frame.entity;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

/**实体对象校验*/
public class EntityValidation {

	/**
	 * 字段校验
	 * @param obj 实体对象
	 * */
	public static void doVerificatio(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			MyVerification myVerification = field.getDeclaredAnnotation(MyVerification.class);
			if (myVerification == null) {
				continue;
			}
			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			Class<?> fieldClass = field.getType();
			try {
				if (int.class == fieldClass) {
					checkInt(myVerification, field.getInt(obj));
					continue;
				} else if (long.class == fieldClass) {
					checkLong(myVerification, field.getLong(obj));
					continue;
				} else if (boolean.class == fieldClass) {
					checkBoolean(myVerification, field.getBoolean(obj));
					continue;
				} else if (double.class == fieldClass) {
					checkDouble(myVerification, field.getDouble(obj));
					continue;
				} else if (float.class == fieldClass) {
					checkFloat(myVerification, field.getFloat(obj));
					continue;
				} else if (short.class == fieldClass) {
					checkShort(myVerification, field.getShort(obj));
				} else if (byte.class == fieldClass) {
					continue;
				} else if (char.class == fieldClass) {
					checkChar(myVerification, field.getChar(obj));
					continue;
				} else {
					Object value = field.get(obj);
					if (value == null) {
						if (myVerification.notNull()) {
							throw new BizException(FrameException.FRAME_VALIDATION_VALUE_CONNOT_BE_NULL, field.getName());
						}
					} else {
						if (String.class == fieldClass) {
							//防跨站XSS攻击
							boolean XSS = myVerification.XSS();
							if (XSS) {
								value = ((String) value).trim();
								field.set(obj, Jsoup.clean((String) value, new Whitelist()));
							}
							//如果字符串要首位去除空格，那么必然应该防跨站攻击
							checkString(myVerification, (String) value);
							continue;
						} else if (Integer.class == fieldClass) {
							checkInt(myVerification, ((Integer) value).intValue());
							continue;
						} else if (Long.class == fieldClass) {
							checkLong(myVerification, ((Long) value).longValue());
							continue;
						} else if (Boolean.class == fieldClass) {
							checkBoolean(myVerification, ((Boolean) value).booleanValue());
							continue;
						} else if (Double.class == fieldClass) {
							checkDouble(myVerification, ((Double) value).doubleValue());
							continue;
						} else if (Float.class == fieldClass) {
							checkFloat(myVerification, ((Float) value).floatValue());
							continue;
						} else if (Short.class == fieldClass) {
							checkShort(myVerification, ((Short) value).shortValue());
							continue;
						} else if (Character.class == fieldClass) {
							checkChar(myVerification, ((Character) value).charValue());
							continue;
						} else if (Byte.class == fieldClass) {
							continue;
						} else {
							//递归校验内部对象
							if (fieldClass.isArray()) {
								//校验数组
								for (int i = 0, length = Array.getLength(value); i < length; i++) {
									doVerificatio(Array.get(value, 0));
								}
							} else {
								doVerificatio(value);
							}
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new BizException(e);
			}
			field.setAccessible(isAccessible);
		}
	}
	
	/**
	 * 校验字符
	 * @param myVerification 校验注解类
	 * @param value 字符
	 * */
	private static void checkChar(MyVerification myVerification, char value) {
		char maxValue = myVerification.maxChar();
		if (maxValue != Integer.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		char minValue = myVerification.minChar();
		if (minValue != Integer.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验字符串
	 * @param myVerification 校验注解类
	 * @param value 字符串
	 * */
	private static void checkString(MyVerification myVerification, String value) {
		int length = value.length();
		if (!myVerification.notNull() && length == 0) {
			return;
		}
		int maxLength = myVerification.maxLength();
		if (maxLength != -1 && length > maxLength) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_LENGTH_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(maxLength));
		}
		int minLength = myVerification.minLength();
		if (minLength != -1 && length < minLength) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_LENGTH_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(minLength));
		}
		String regexp = myVerification.regexp();
		if (!StringUtil.isBlank(regexp)) {
			if (!Pattern.compile(regexp).matcher(value).matches()) {
				throw new BizException(FrameException.FRAME_VALIDATION_VALUE_INVALID, myVerification.name());
			}
		}
	}
	
	/**
	 * 校验单精度
	 * @param myVerification 校验注解类
	 * @param value 单精度小数
	 * */
	private static void checkFloat(MyVerification myVerification, float value) {
		float maxValue = myVerification.maxFloat();
		if (maxValue != Float.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		float minValue = myVerification.minFloat();
		if (minValue != Float.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验双精度
	 * @param myVerification 校验注解类
	 * @param value 双精度小数
	 * */
	private static void checkDouble(MyVerification myVerification, double value) {
		double maxValue = myVerification.maxDouble();
		if (maxValue != Double.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		double minValue = myVerification.minDouble();
		if (minValue != Double.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验布尔类型
	 * @param myVerification 校验注解类
	 * @param value 布尔值
	 * */
	private static void checkBoolean(MyVerification myVerification, boolean value) {
		if (myVerification.notTrue() && value) {
			throw new BizException(FrameException.FRAME_VALIDATION_VALUE_CONNOT_BE_TRUE, myVerification.name(), String.valueOf(value));
		}
		if (myVerification.notFlase() && !value) {
			throw new BizException(FrameException.FRAME_VALIDATION_VALUE_CONNOT_BE_FLASE, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验短整型
	 * @param myVerification 校验注解类
	 * @param value 短整形
	 * */
	private static void checkShort(MyVerification myVerification, short value) {
		short maxValue = myVerification.maxShort();
		if (maxValue != Long.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		short minValue = myVerification.minShort();
		if (minValue != Long.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验长整型
	 * @param myVerification 校验注解类
	 * @param value 长整形
	 * */
	private static void checkLong(MyVerification myVerification, long value) {
		long maxValue = myVerification.maxLong();
		if (maxValue != Long.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		long minValue = myVerification.minLong();
		if (minValue != Long.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
	/**
	 * 校验整型
	 * @param myVerification 校验注解类
	 * @param value 整形
	 * */
	private static void checkInt(MyVerification myVerification, int value) {
		int maxValue = myVerification.maxInt();
		if (maxValue != Integer.MIN_VALUE && value > maxValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MAX_VALUE_CANNOT_BIGGER_THAN, myVerification.name(), String.valueOf(value));
		}
		int minValue = myVerification.minInt();
		if (minValue != Integer.MAX_VALUE && value < minValue) {
			throw new BizException(FrameException.FRAME_VALIDATION_MIN_VALUE_CANNOT_LESS_THAN, myVerification.name(), String.valueOf(value));
		}
	}
	
}