package com.xanglong;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.dao.Dao;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.net.HttpUtil;
import com.xanglong.frame.net.RequestDto;
import com.xanglong.frame.net.ResponseDto;

public class Test {

	public static void main(String[] args) {
		try {
			Dao.delete("DELETE FROM ISSUES");
			JSONArray datas1 = getData("http://jira.rd.800best.com/issues/?filter=-4&jql=Sprint%20%3D%202909%20order%20by%20created%20DESC");
			insert(datas1);
			JSONArray datas2 = getData("http://jira.rd.800best.com/issues/?filter=-4&jql=Sprint%20%3D%202909%20order%20by%20created%20DESC&startIndex=50");
			insert(datas2);
			Dao.commit();
		} catch (Throwable throwable) {
			ThrowableHandler.dealException(throwable);
		}
	}
	
	private static void insert(JSONArray datas) {
		for (int i = 0; i < datas.size(); i ++) {
			JSONObject data = datas.getJSONObject(i);
			String sql1 = " INSERT INTO ISSUES ( ";
			String sql2 = " VALUES(";
			for (String key : data.keySet()) {
				sql1 += "`" + key + "`" + ",";
				sql2 += "'" + data.getString(key) + "',";
			}
			sql1 = sql1.substring(0, sql1.length() - 1);
			sql2 = sql2.substring(0, sql2.length() - 1);
			Dao.insert(sql1 + ")" +  sql2 + ")");
		}
	}
	
	private static JSONArray getData(String url) {
		RequestDto requestDto = new RequestDto();
		JSONObject headerParams = new JSONObject();
		headerParams.put("Cookie", "UM_distinctid=170c831b35429b-0d86a4dcfc435d-376b4502-1fa400-170c831b355477; JSESSIONID=9A253A8CD9D2A0E9B03E4EA13C8CFE59; atlassian.xsrf.token=AQUI-VC9G-11AZ-ZZAK|58bb07cca16db92ca81a39a22c35ec653b0707ff|lin");
		requestDto.setHeaderParams(headerParams);
		requestDto.setUrl(url);
		ResponseDto responseDto = HttpUtil.doGet(requestDto);
		String html = new String(responseDto.getBytes());
		Document document = Jsoup.parse(html);
		Element table = document.getElementById("issuetable");
		Elements ths = table.child(0).children().get(0).children();
		List<String> keys = new ArrayList<>();
		for (int i = 0; i < ths.size(); i++) {
			keys.add(ths.get(i).getElementsByTag("span").html());
		}
		System.out.println(keys);
		JSONArray datas = new JSONArray();
		Elements trs = table.child(1).children();
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).children();
			JSONObject data = new JSONObject();
			for (int j = 0; j < tds.size(); j++) {
				setData(tds.get(j), data, keys.get(j));
			}
			datas.add(data);
			System.out.println(data);
		}
		return datas;
	}
	
	private static void setData(Element td, JSONObject data, String key) {
		if ("T".equals(key)) {
			data.put(key, td.child(1).child(0).attr("alt"));
		} else if ("Key".equals(key)) {
			data.put(key, td.child(0).html());
		} else if ("Assignee".equals(key)) {
			data.put(key, td.child(0).child(0).html());
		} else if ("Summary".equals(key)) {
			Element p = td.child(0);
			data.put(key, p.child(p.children().size() - 1).html());
		} else if ("Sprint".equals(key)) {
			data.put(key, td.html());
		} else if ("Reporter".equals(key)) {
			data.put(key, td.child(0).child(0).html());
		} else if ("P".equals(key)) {
			data.put(key, td.child(0).attr("alt"));
		} else if ("Status".equals(key)) {
			data.put(key, td.child(0).html());
		} else if ("Story Points".equals(key)) {
			data.put(key, td.html());
		} else if ("Updated".equals(key)) {
			data.put(key, td.child(0).child(0).attr("datetime").replace("T", " ").substring(0, 19));
		} else if ("Fix Version/s".equals(key)) {
			if (td.children().size() > 0) {
				data.put(key, td.child(0).html());
			}
		} else if ("提测PlanDate".equals(key)) {
			if (td.children().size() > 0) {
				data.put(key, td.child(0).child(0).attr("datetime"));
			}
		} else if ("Original Estimate".equals(key) || "Remaining Estimate".equals(key) || "Time Spent".equals(key)) {
			int totalMinutes = 0;
			for (String time : td.html().split(",")) {
				if (!StringUtil.isBlank(time)) {
					time = time.trim();
					int number = Integer.parseInt(time.substring(0, time.indexOf(" ")));
					if (time.endsWith("minutes")) {
						totalMinutes += number;
					} else if (time.endsWith("hours")) {
						totalMinutes += number * 60;
					} else if (time.endsWith("day")) {
						totalMinutes += number * 480;
					} else if (time.endsWith("week")) {
						totalMinutes += number * 2400;
					}
				}
			}
			data.put(key, totalMinutes);
		}
	}
}