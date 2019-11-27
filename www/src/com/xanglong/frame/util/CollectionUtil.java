package com.xanglong.frame.util;

import java.util.Collection;

/**数组工具类*/
public class CollectionUtil {

	/**是否为空*/
	public static boolean isEmpty(Collection<?> objects) {
		return objects == null || objects.isEmpty();
	}

	/**是否有交集*/
	public static boolean isExist(Collection<?> items1, Collection<?> items2) {
		if (isEmpty(items1) || isEmpty(items1)) {
			return false;
		}
		boolean[] isExists = new boolean[] {false};
		items1.forEach(item1 -> {
			items2.forEach(item2 -> {
				if (item1.equals(item2)) {
					isExists[0] = true;
					return;
				}
			});
			if (isExists[0]) {
				return;
			}
		});
		return isExists[0];
	}

}