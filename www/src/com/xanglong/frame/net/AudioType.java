package com.xanglong.frame.net;

/**音频类型*/
public enum AudioType {
	
	ACP("acp", "音频", "audio/x-mei-aac"),
	
	AIF("aif", "音频", "audio/aiff"),
	
	AIFF("aiff", "音频", "audio/aiff"),
	
	AIFC("aifc", "音频", "audio/aiff"),
	
	AU("au", "音频", "audio/au"),
	
	LA1("la1", "音频", "audio/x-liquid-file"),
	
	LAVS("lavs", "音频", "audio/x-liquid-secure"),
	
	LMSFF("lmsff", "音频", "audio/x-la-lms"),
	
	M3U("m3u", "音频", "audio/mpegurl"),
	
	MIDI("midi", "音频", "audio/mid"),
	
	MID("mid", "音频", "audio/mid"),
	
	MP1("mp1", "音频", "audio/mp1"),
	
	MP2("mp2", "音频", "audio/mp2"),
	
	MP3("mp3", "音频", "audio/mp3"),
	
	MND("mnd", "音频", "audio/x-musicnet-download"),
	
	MNS("mns", "音频", "audio/x-musicnet-stream"),
	
	MPGA("mpga", "音频", "audio/rn-mpeg"),
	
	PLS("pls", "音频", "audio/scpls"),
	
	RAM("ram", "音频", "audio/x-pn-realaudio"),
	
	RMI("rmi", "音频", "audio/mid"),
	
	rmm("rmm", "音频", "audio/x-pn-realaudio"),
	
	SND("snd", "音频", "audio/basic"),
	
	WAV("wav", "音频", "audio/wav"),
	
	WAX("wax", "音频", "audio/x-ms-wax"),
	
	WMA("wma", "音频", "audio/x-ms-wma");
	
	AudioType (String code, String name, String type) {
		this.code = code;
		this.name = name;
		this.type = type;
	}
	
	private String code;
	
	private String name;
	
	private String type;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}