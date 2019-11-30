package com.xanglong.frame.net;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class BodyParam {
	
	/**对象部分参数*/
	private JSONObject obejct;
	
	/**数组部分参数*/
	private JSONArray array;

	public JSONObject getObejct() {
		return obejct;
	}

	public void setObejct(JSONObject obejct) {
		this.obejct = obejct;
	}

	public JSONArray getArray() {
		return array;
	}

	public void setArray(JSONArray array) {
		this.array = array;
	}

}