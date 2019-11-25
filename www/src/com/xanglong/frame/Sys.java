package com.xanglong.frame;

import com.xanglong.frame.config.Config;
import com.xanglong.frame.config.ConfigManager;

public class Sys {
	
	public static Config getConfig() {
		return ConfigManager.getConfig();
	}

}