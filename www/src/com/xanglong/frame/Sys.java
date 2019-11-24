package com.xanglong.frame;

import com.xanglong.frame.config.Config;

public class Sys {
	
	private static Config config;

	public static Config getConfig() {
		return config;
	}

	public static void setConfig(Config config) {
		Sys.config = config;
	}

}