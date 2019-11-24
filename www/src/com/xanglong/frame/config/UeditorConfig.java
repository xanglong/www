package com.xanglong.frame.config;

/**百度富文本插件配置对象*/
public class UeditorConfig {

	/*图片上传配置*/
	
	/**执行上传图片的action名称*/
	private String imageActionName;
	
	/**提交的图片表单名称*/
	private String imageFieldName;
	
	/**图片上传大小限制，单位B*/
	private int imageMaxSize;
	
	/**上传图片格式显示*/
	private String[] imageAllowFiles;
	
	/**是否压缩图片,默认是true*/
	private boolean imageCompressEnable;
	
	/**图片压缩最长边限制*/
	private int imageCompressBorder;
	
	/**插入的图片浮动方式*/
	private String imageInsertAlign;
	
	/**图片访问路径前缀*/
	private String imageUrlPrefix;
	
	/**
	 * 上传保存路径,可以自定义保存路径和文件名格式
	 * {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 
	 * {rand:6} 会替换成随机数,后面的数字是随机数的位数
	 * {time} 会替换成时间戳
	 * {yyyy} 会替换成四位年份
	 * {yy} 会替换成两位年份
	 * {mm} 会替换成两位月份
	 * {dd} 会替换成两位日期
	 * {hh} 会替换成两位小时
	 * {ii} 会替换成两位分钟
	 * {ss} 会替换成两位秒
	 * 非法字符 \ : * ? " < > |
	 * 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
	 * */
	private String imagePathFormat;
	
	/*涂鸦上传配置*/
	
	/**执行上传涂鸦的action名称*/
	private String scrawlActionName;
	
	/**提交的图片表单名称*/
	private String scrawlFieldName;
	
	/**上传保存路径,可以自定义保存路径和文件名格式*/
	private String scrawlPathFormat;
	
	/**上传大小限制，单位B*/
	private int scrawlMaxSize;
	
	/**图片访问路径前缀*/
	private String scrawlUrlPrefix;
	
	/**插入的涂鸦浮动方式*/
	private String scrawlInsertAlign;
	
	/*截图工具上传*/
	
	/**执行上传截图的action名称*/
	private String snapscreenActionName;
	
	/**上传保存路径,可以自定义保存路径和文件名格式*/
	private String snapscreenPathFormat;
	
	/**图片访问路径前缀*/
	private String snapscreenUrlPrefix;
	
	/**插入的图片浮动方式*/
	private String snapscreenInsertAlign;
	
	/*抓取远程图片配置*/
	
	/**远程图片服务器地址*/
	private String[] catcherLocalDomain;
	
	/**执行抓取远程图片的action名称*/
	private String catcherActionName;
	
	/**提交的图片列表表单名称*/
	private String catcherFieldName;
	
	/**上传保存路径,可以自定义保存路径和文件名格式
	 * {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 
	 * {rand:6} 会替换成随机数,后面的数字是随机数的位数
	 * {time} 会替换成时间戳
	 * {yyyy} 会替换成四位年份
	 * {yy} 会替换成两位年份
	 * {mm} 会替换成两位月份
	 * {dd} 会替换成两位日期
	 * {hh} 会替换成两位小时
	 * {ii} 会替换成两位分钟
	 * {ss} 会替换成两位秒
	 * 非法字符 \ : * ? " < > |
	 * 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
	 * */
	private String catcherPathFormat;
	
	/**图片访问路径前缀*/
	private String catcherUrlPrefix;
	
	/**上传大小限制，单位B*/
	private int catcherMaxSize;
	
	/**抓取图片格式显示*/
	private String[] catcherAllowFiles;
	
	/*上传视频配置*/
	
	/**执行上传视频的action名称*/
	private String videoActionName;
	
	/**提交的视频表单名称*/
	private String videoFieldName;
	
	/**上传保存路径,可以自定义保存路径和文件名格式
	 * {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 
	 * {rand:6} 会替换成随机数,后面的数字是随机数的位数
	 * {time} 会替换成时间戳
	 * {yyyy} 会替换成四位年份
	 * {yy} 会替换成两位年份
	 * {mm} 会替换成两位月份
	 * {dd} 会替换成两位日期
	 * {hh} 会替换成两位小时
	 * {ii} 会替换成两位分钟
	 * {ss} 会替换成两位秒
	 * 非法字符 \ : * ? " < > |
	 * 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
	 * */
	private String videoPathFormat;
	
	/**视频访问路径前缀*/
	private String videoUrlPrefix;
	
	/**上传大小限制，单位B，默认100MB*/
	private int videoMaxSize;
	
	/**上传视频格式显示*/
	private String[] videoAllowFiles;
	
	/*上传文件配置*/
	
	/**执行上传视频的action名称*/
	private String fileActionName;
	
	/**提交的文件表单名称*/
	private String fileFieldName;
	
	/**上传保存路径,可以自定义保存路径和文件名格式
	 * {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 
	 * {rand:6} 会替换成随机数,后面的数字是随机数的位数
	 * {time} 会替换成时间戳
	 * {yyyy} 会替换成四位年份
	 * {yy} 会替换成两位年份
	 * {mm} 会替换成两位月份
	 * {dd} 会替换成两位日期
	 * {hh} 会替换成两位小时
	 * {ii} 会替换成两位分钟
	 * {ss} 会替换成两位秒
	 * 非法字符 \ : * ? " < > |
	 * 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename
	 * */
	private String filePathFormat;
	
	/**文件访问路径前缀*/
	private String fileUrlPrefix;
	
	/**上传大小限制，单位B，默认50MB*/
	private int fileMaxSize;
	
	/**上传文件格式显示*/
	private String[] fileAllowFiles;
	
	/*列出指定目录下的图片*/
	
	/**执行图片管理的action名称*/
	private String imageManagerActionName;
	
	/**指定要列出图片的目录*/
	private String imageManagerListPath;
	
	/**每次列出文件数量*/
	private int imageManagerListSize;
	
	/**图片访问路径前缀*/
	private String imageManagerUrlPrefix;
	
	/**插入的图片浮动方式*/
	private String imageManagerInsertAlign;
	
	/**列出的文件类型*/
	private String[] imageManagerAllowFiles;
	
	/*列出指定目录下的文件*/
	
	/**执行文件管理的action名称*/
	private String fileManagerActionName;
	
	/**指定要列出文件的目录*/
	private String fileManagerListPath;
	
	/**文件访问路径前缀*/
	private String fileManagerUrlPrefix;
	
	/**每次列出文件数量*/
	private int fileManagerListSize;
	
	/**列出的文件类型*/
	private String[] fileManagerAllowFiles;
	
	public String getImageActionName() {
		return imageActionName;
	}

	public void setImageActionName(String imageActionName) {
		this.imageActionName = imageActionName;
	}

	public String getImageFieldName() {
		return imageFieldName;
	}

	public void setImageFieldName(String imageFieldName) {
		this.imageFieldName = imageFieldName;
	}

	public int getImageMaxSize() {
		return imageMaxSize;
	}

	public void setImageMaxSize(int imageMaxSize) {
		this.imageMaxSize = imageMaxSize;
	}

	public String[] getImageAllowFiles() {
		return imageAllowFiles;
	}

	public void setImageAllowFiles(String[] imageAllowFiles) {
		this.imageAllowFiles = imageAllowFiles;
	}

	public boolean isImageCompressEnable() {
		return imageCompressEnable;
	}

	public void setImageCompressEnable(boolean imageCompressEnable) {
		this.imageCompressEnable = imageCompressEnable;
	}

	public int getImageCompressBorder() {
		return imageCompressBorder;
	}

	public void setImageCompressBorder(int imageCompressBorder) {
		this.imageCompressBorder = imageCompressBorder;
	}

	public String getImageInsertAlign() {
		return imageInsertAlign;
	}

	public void setImageInsertAlign(String imageInsertAlign) {
		this.imageInsertAlign = imageInsertAlign;
	}

	public String getImageUrlPrefix() {
		return imageUrlPrefix;
	}

	public void setImageUrlPrefix(String imageUrlPrefix) {
		this.imageUrlPrefix = imageUrlPrefix;
	}

	public String getImagePathFormat() {
		return imagePathFormat;
	}

	public void setImagePathFormat(String imagePathFormat) {
		this.imagePathFormat = imagePathFormat;
	}

	public String getScrawlActionName() {
		return scrawlActionName;
	}

	public void setScrawlActionName(String scrawlActionName) {
		this.scrawlActionName = scrawlActionName;
	}

	public String getScrawlFieldName() {
		return scrawlFieldName;
	}

	public void setScrawlFieldName(String scrawlFieldName) {
		this.scrawlFieldName = scrawlFieldName;
	}

	public String getScrawlPathFormat() {
		return scrawlPathFormat;
	}

	public void setScrawlPathFormat(String scrawlPathFormat) {
		this.scrawlPathFormat = scrawlPathFormat;
	}

	public int getScrawlMaxSize() {
		return scrawlMaxSize;
	}

	public void setScrawlMaxSize(int scrawlMaxSize) {
		this.scrawlMaxSize = scrawlMaxSize;
	}

	public String getScrawlUrlPrefix() {
		return scrawlUrlPrefix;
	}

	public void setScrawlUrlPrefix(String scrawlUrlPrefix) {
		this.scrawlUrlPrefix = scrawlUrlPrefix;
	}

	public String getScrawlInsertAlign() {
		return scrawlInsertAlign;
	}

	public void setScrawlInsertAlign(String scrawlInsertAlign) {
		this.scrawlInsertAlign = scrawlInsertAlign;
	}

	public String getSnapscreenActionName() {
		return snapscreenActionName;
	}

	public void setSnapscreenActionName(String snapscreenActionName) {
		this.snapscreenActionName = snapscreenActionName;
	}

	public String getSnapscreenPathFormat() {
		return snapscreenPathFormat;
	}

	public void setSnapscreenPathFormat(String snapscreenPathFormat) {
		this.snapscreenPathFormat = snapscreenPathFormat;
	}

	public String getSnapscreenUrlPrefix() {
		return snapscreenUrlPrefix;
	}

	public void setSnapscreenUrlPrefix(String snapscreenUrlPrefix) {
		this.snapscreenUrlPrefix = snapscreenUrlPrefix;
	}

	public String getSnapscreenInsertAlign() {
		return snapscreenInsertAlign;
	}

	public void setSnapscreenInsertAlign(String snapscreenInsertAlign) {
		this.snapscreenInsertAlign = snapscreenInsertAlign;
	}

	public String[] getCatcherLocalDomain() {
		return catcherLocalDomain;
	}

	public void setCatcherLocalDomain(String[] catcherLocalDomain) {
		this.catcherLocalDomain = catcherLocalDomain;
	}

	public String getCatcherActionName() {
		return catcherActionName;
	}

	public void setCatcherActionName(String catcherActionName) {
		this.catcherActionName = catcherActionName;
	}

	public String getCatcherFieldName() {
		return catcherFieldName;
	}

	public void setCatcherFieldName(String catcherFieldName) {
		this.catcherFieldName = catcherFieldName;
	}

	public String getCatcherPathFormat() {
		return catcherPathFormat;
	}

	public void setCatcherPathFormat(String catcherPathFormat) {
		this.catcherPathFormat = catcherPathFormat;
	}

	public String getCatcherUrlPrefix() {
		return catcherUrlPrefix;
	}

	public void setCatcherUrlPrefix(String catcherUrlPrefix) {
		this.catcherUrlPrefix = catcherUrlPrefix;
	}

	public int getCatcherMaxSize() {
		return catcherMaxSize;
	}

	public void setCatcherMaxSize(int catcherMaxSize) {
		this.catcherMaxSize = catcherMaxSize;
	}

	public String[] getCatcherAllowFiles() {
		return catcherAllowFiles;
	}

	public void setCatcherAllowFiles(String[] catcherAllowFiles) {
		this.catcherAllowFiles = catcherAllowFiles;
	}

	public String getVideoActionName() {
		return videoActionName;
	}

	public void setVideoActionName(String videoActionName) {
		this.videoActionName = videoActionName;
	}

	public String getVideoFieldName() {
		return videoFieldName;
	}

	public void setVideoFieldName(String videoFieldName) {
		this.videoFieldName = videoFieldName;
	}

	public String getVideoPathFormat() {
		return videoPathFormat;
	}

	public void setVideoPathFormat(String videoPathFormat) {
		this.videoPathFormat = videoPathFormat;
	}

	public String getVideoUrlPrefix() {
		return videoUrlPrefix;
	}

	public void setVideoUrlPrefix(String videoUrlPrefix) {
		this.videoUrlPrefix = videoUrlPrefix;
	}

	public int getVideoMaxSize() {
		return videoMaxSize;
	}

	public void setVideoMaxSize(int videoMaxSize) {
		this.videoMaxSize = videoMaxSize;
	}

	public String[] getVideoAllowFiles() {
		return videoAllowFiles;
	}

	public void setVideoAllowFiles(String[] videoAllowFiles) {
		this.videoAllowFiles = videoAllowFiles;
	}

	public String getFileActionName() {
		return fileActionName;
	}

	public void setFileActionName(String fileActionName) {
		this.fileActionName = fileActionName;
	}

	public String getFileFieldName() {
		return fileFieldName;
	}

	public void setFileFieldName(String fileFieldName) {
		this.fileFieldName = fileFieldName;
	}

	public String getFilePathFormat() {
		return filePathFormat;
	}

	public void setFilePathFormat(String filePathFormat) {
		this.filePathFormat = filePathFormat;
	}

	public String getFileUrlPrefix() {
		return fileUrlPrefix;
	}

	public void setFileUrlPrefix(String fileUrlPrefix) {
		this.fileUrlPrefix = fileUrlPrefix;
	}

	public int getFileMaxSize() {
		return fileMaxSize;
	}

	public void setFileMaxSize(int fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}

	public String[] getFileAllowFiles() {
		return fileAllowFiles;
	}

	public void setFileAllowFiles(String[] fileAllowFiles) {
		this.fileAllowFiles = fileAllowFiles;
	}

	public String getImageManagerActionName() {
		return imageManagerActionName;
	}

	public void setImageManagerActionName(String imageManagerActionName) {
		this.imageManagerActionName = imageManagerActionName;
	}

	public String getImageManagerListPath() {
		return imageManagerListPath;
	}

	public void setImageManagerListPath(String imageManagerListPath) {
		this.imageManagerListPath = imageManagerListPath;
	}

	public int getImageManagerListSize() {
		return imageManagerListSize;
	}

	public void setImageManagerListSize(int imageManagerListSize) {
		this.imageManagerListSize = imageManagerListSize;
	}

	public String getImageManagerUrlPrefix() {
		return imageManagerUrlPrefix;
	}

	public void setImageManagerUrlPrefix(String imageManagerUrlPrefix) {
		this.imageManagerUrlPrefix = imageManagerUrlPrefix;
	}

	public String getImageManagerInsertAlign() {
		return imageManagerInsertAlign;
	}

	public void setImageManagerInsertAlign(String imageManagerInsertAlign) {
		this.imageManagerInsertAlign = imageManagerInsertAlign;
	}

	public String[] getImageManagerAllowFiles() {
		return imageManagerAllowFiles;
	}

	public void setImageManagerAllowFiles(String[] imageManagerAllowFiles) {
		this.imageManagerAllowFiles = imageManagerAllowFiles;
	}

	public String getFileManagerActionName() {
		return fileManagerActionName;
	}

	public void setFileManagerActionName(String fileManagerActionName) {
		this.fileManagerActionName = fileManagerActionName;
	}

	public String getFileManagerListPath() {
		return fileManagerListPath;
	}

	public void setFileManagerListPath(String fileManagerListPath) {
		this.fileManagerListPath = fileManagerListPath;
	}

	public String getFileManagerUrlPrefix() {
		return fileManagerUrlPrefix;
	}

	public void setFileManagerUrlPrefix(String fileManagerUrlPrefix) {
		this.fileManagerUrlPrefix = fileManagerUrlPrefix;
	}

	public int getFileManagerListSize() {
		return fileManagerListSize;
	}

	public void setFileManagerListSize(int fileManagerListSize) {
		this.fileManagerListSize = fileManagerListSize;
	}

	public String[] getFileManagerAllowFiles() {
		return fileManagerAllowFiles;
	}

	public void setFileManagerAllowFiles(String[] fileManagerAllowFiles) {
		this.fileManagerAllowFiles = fileManagerAllowFiles;
	}

}