package com.xanglong.frame.net;

public class SourceInfo {
	
	/**资源类型*/
	private SourceType sourceType;
	
	/**音频类型*/
	private AudioType audioType;
	
	/**字体类型*/
	private FontType fontType;
	
	/**图片类型*/
	private ImageType imageType;
	
	/**视频类型*/
	private VideoType videoType;
	
	/**请求地址*/
	private String requestURI;
	
	/**文本类型*/
	private TextType textType;

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public AudioType getAudioType() {
		return audioType;
	}

	public void setAudioType(AudioType audioType) {
		this.audioType = audioType;
	}

	public FontType getFontType() {
		return fontType;
	}

	public void setFontType(FontType fontType) {
		this.fontType = fontType;
	}

	public ImageType getImageType() {
		return imageType;
	}

	public void setImageType(ImageType imageType) {
		this.imageType = imageType;
	}

	public VideoType getVideoType() {
		return videoType;
	}

	public void setVideoType(VideoType videoType) {
		this.videoType = videoType;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public TextType getTextType() {
		return textType;
	}

	public void setTextType(TextType textType) {
		this.textType = textType;
	}

}