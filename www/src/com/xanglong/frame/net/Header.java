package com.xanglong.frame.net;

public class Header {
	
	/**请求响应公共部分*/
	public static final String CACHE_CONTROL = "Cache-Control";//用于指定沿着请求-响应链的所有缓存机制必须遵守的指令
	public static final String CONNECTION = "Connection";//当前连接的控制选项和逐跳请求字段的列表
	public static final String CONTENT_LENGTH = "Content-Length";//请求体、响应体的长度（八位字节）
	public static final String CONTENT_MD5 = "Content-MD5";//请求体、响应体内容的一种base64编码的二进制MD5的和
	public static final String CONTENT_TYPE = "Content-Type";//请求主体、响应主体的媒体类型(与POST和PUT请求一起使用)
	public static final String DATE = "Date";//消息发出的日期和时间(以RFC 7231日期/时间格式定义的“HTTP-date”格式)
	public static final String PRAGMA = "Pragma";//特定于实现的字段，在请求-响应链的任何位置都可能具有各种效果
	public static final String UPGRADE = "Upgrade";//要求服务器升级到另一个协议
	public static final String VIA = "Via";//通知服务器发送请求的代理
	public static final String WARNING = "Warning";//关于实体体可能出现问题的一般警告
	public static final String X_REQUEST_ID = "X-Request-ID";//在客户端和服务器之间关联HTTP请求ID
	public static final String X_CORRELATION_ID = "X-Correlation-ID";//在客户端和服务器之间关联HTTP请求ID
	
	/**标准请求字段*/
	public static final String A_IM = "A-IM";//可接受实例操作的请求
	public static final String ACCEPT = "Accept";//对于响应来说是(/是)可接受的媒体类型，看到内容协商
	public static final String ACCEPT_CHARSET = "Accept-Charset";//可接受的字符集
	public static final String ACCEPT_DATETIME = "Accept-Datetime";//可接受的版本
	public static final String ACCEPT_ENCODING = "Accept-Encoding";//可接受的编码列表，看到HTTP压缩
	public static final String ACCEPT_LANGUAGE = "Accept-Language";//可接受的人类语言列表，看到内容协商
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";//启动与Origin共享跨源资源的请求方法
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";//启动与Origin共享跨源资源的请求头类型
	public static final String AUTHORIZATION = "Authorization";//HTTP身份验证的身份验证凭据
	public static final String COOKIE = "Cookie";//先前服务器通过Set-Cookie发送的HTTP cookie
	public static final String EXPECT = "Expect";//指示客户端需要特定的服务器行为
	public static final String FORWARDED = "Forwarded";//通过HTTP代理.公开连接到web服务器的客户机的原始信息
	public static final String FROM = "From";//发出请求的用户的电子邮件地址
	public static final String HOST = "Host";//主机
	public static final String IF_MATCH = "If-Match";//只有当客户机提供的实体与服务器上的相同实体匹配时，才执行该操作
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";//如果内容不变，则允许返回未修改的304
	public static final String IF_NONE_MATCH = "If-None-Match";//如果内容不变，允许返回不修改的304，请参阅HTTP ETag
	public static final String IF_RANGE = "If-Range";//如果实体不变，请将我遗漏的部分发送给我;否则，将整个新实体发送给我
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";//仅当实体在特定时间后未被修改时发送响应
	public static final String MAX_FORWARDS = "Max-Forwards";//限制通过代理或网关转发邮件的次数
	public static final String ORIGIN = "Origin";//启动跨源资源共享请求（向服务器请求 Access-Control-*响应字段）
	public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";//用于连接到代理的授权凭据
	public static final String RANGE = "Range";//只请求实体的一部分，字节从0开始编号
	public static final String REFERER = "Referer";//这是上一个网页的地址，从中可以找到指向当前请求的网页的链接
	public static final String TE = "TE";//用户代理愿意接受的传输编码
	public static final String USER_AGENT = "User-Agent";//用户代理的用户代理字符串
	
	/**非标准请求字段*/
	public static final String UPGRADE_INSECURE_REQUESTS = "Upgrade-Insecure-Requests";//告诉服务器（可能在HTTP->HTTPS迁移的中间）托管混合内容，客户端希望重定向到HTTPS并可以处理内容安全策略
	public static final String X_REQUESTED_WITH = "X-Requested-With";//主要用于识别Ajax请求,大多数JavaScript框架都将这个字段的值发送给XMLHttpRequest
	public static final String DNT = "DNT";//请求web应用程序禁用对用户的跟踪
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";//一种事实上的标准，用于标识通过HTTP代理或负载平衡器连接到web服务器的客户端的原始IP地址
	public static final String X_FORWARDED_HOST = "X-Forwarded-Host";//用于在主机HTTP请求头中标识客户端请求的原始主机的事实标准，因为反向代理（负载平衡器）的主机名和/或端口可能与处理请求的原始服务器不同
	public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";//用于标识HTTP请求的原始协议的事实标准，因为反向代理（或负载平衡器）可以使用HTTP与web服务器通信，即使对反向代理的请求是HTTPS
	public static final String FRONT_END_HTTPS = "Front-End-Https";//Microsoft应用程序和负载平衡器使用的非标准头字段
	public static final String X_HTTP_METHOD_OVERRIDE = "X-Http-Method-Override";//请求web应用程序使用头字段中给定的方法（通常是放置或删除）重写请求中指定的方法（通常是POST
	public static final String X_ATT_DEVICEID = "X-ATT-DeviceId";//允许更轻松地分析通常在AT&T设备的用户代理字符串中找到的MakeModel/固件
	public static final String X_WAP_PROFILE = "X-Wap-Profile";//指向Internet上XML文件的链接，其中包含有关当前连接的设备的完整说明和详细信息
	public static final String PROXY_CONNECTION = "Proxy-Connection";//实现为对HTTP规范的误解，与标准连接字段具有完全相同的功能
	public static final String X_UIDH = "X-UIDH";//服务器端深度数据包插入一个标识，客户的唯一ID
	public static final String X_CSRF_TOKEN = "X-Csrf-Token";//用于防止跨站点请求伪造
	public static final String SAVE_DATA = "Save-Data";//提示请求头允许开发人员向选择在浏览器中使用数据保存模式的用户提供更轻、更快的应用程序
	
	/**标准响应字段*/
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";//指定哪些网站可以参与跨源资源共享
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";//指定哪些网站可以参与跨源资源共享
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";//指定哪些网站可以参与跨源资源共享
	public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";//指定哪些网站可以参与跨源资源共享
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";//指定哪些网站可以参与跨源资源共享
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";//指定哪些网站可以参与跨源资源共享
	public static final String ACCEPT_PATCH = "Accept-Patch";//此服务器通过字节服务支持哪些部分内容范围类型
	public static final String ACCEPT_RANGES = "Accept-Ranges";//指定哪些网站可以参与跨源资源共享
	public static final String AGE = "Age";//对象在代理缓存中的时间（秒）
	public static final String ALLOW = "Allow";//指定资源的有效方法，不允许用于405方法
	public static final String ALT_SVC = "Alt-Svc";//指示其资源也可以在不同的网络位置（主机或端口）或使用不同的协议进行访问
	public static final String CONTENT_DISPOSITION = "Content-Disposition";//一个为二进制格式的已知MIME类型打开“文件下载”对话框或为动态内容建议文件名的机会，特殊字符需要引号
	public static final String CONTENT_ENCODING = "Content-Encoding";//数据上使用的编码类型
	public static final String CONTENT_LANGUAGE = "Content-Language";//所附内容的一种或多种自然语言
	public static final String CONTENT_RANGE = "Content-Range";//在正文消息中，此部分消息所属的位置
	public static final String DELTA_BASE = "Delta-Base";//指定响应的增量编码实体标记
	public static final String ETAG = "ETag";//资源特定版本的标识符，通常是消息摘要
	public static final String EXPIRES = "Expires";//给出认为响应过时的日期/时间（采用RFC 7231定义的“HTTP日期”格式）
	public static final String IM = "IM";//应用于响应的实例操作
	public static final String LAST_MODIFIED = "Last-Modified";//请求对象的最后修改日期（采用RFC 7231定义的“HTTP日期”格式）
	public static final String LINK = "Link";//用于表示与另一资源的类型化关系，其中关系类型由RFC 5988定义
	public static final String LOCATION = "Location";//在重定向中使用，或在创建新资源时使用
	public static final String P3P = "P3P";//设置P3P策略
	public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";//请求身份验证以访问代理
	public static final String PUBLIC_KEY_PINS = "Public-Key-Pins";//HTTP公钥锁定，公布网站可信TLS证书哈希
	public static final String RETRY_AFTER = "Retry-After";//如果实体暂时不可用，则指示客户端稍后再试。值可以是指定的时间段（以秒为单位）或HTTP日期
	public static final String SERVER = "Server";//服务器名称
	public static final String SET_COOKIE = "Set-Cookie";//设置会话信息
	public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";//一种HSTS策略，通知HTTP客户机仅缓存HTTPS策略的时间以及该策略是否适用于子域
	public static final String TRAILER = "Trailer";//指示给定的头字段集存在于用分块传输编码编码的消息的尾部中
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";//用于将实体安全传输给用户的编码形式
	public static final String TK = "Tk";//跟踪状态标题，建议发送值以响应DNT（不跟踪）
	public static final String VARY = "Vary";//告诉下游代理如何匹配未来的请求报头，以决定缓存的响应是否可以使用，而不是从源服务器请求新鲜的响应
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";//指示应用于访问请求的实体的身份验证方案
	public static final String X_FRAME_OPTIONS = "X-Frame-Options";//单击顶进保护
	
	/**非标准响应字段*/
	public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy,";//内容安全策略
	public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";//跨站内容安全策略
	public static final String X_WEBKIT_CSP = "X-WebKit-CSP";//内容安全策略定义
	public static final String REFRESH = "Refresh";//用于重定向，或在创建新资源时使用，此刷新在5秒后重定向
	public static final String STATUS = "Status";//指定HTTP响应状态的CGI头字段。正常的HTTP响应使用一个单独的“状态行”，由RFC 7230定义
	public static final String TIMING_ALLOW_ORIGIN = "Timing-Allow-Origin";//指定允许查看通过资源计时API的功能检索的属性值的源，否则，由于跨源限制，这些属性值将报告为零
	public static final String X_CONTENT_DURATION = "X-Content-Duration";//提供音频或视频的持续时间（秒）
	public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";//唯一定义的值防止浏览器从声明的内容类型嗅探响应
	public static final String X_POWERED_BY = "X-Powered-By";//指定支持web应用程序的技术
	public static final String X_UA_COMPATIBLE = "X-UA-Compatible";//建议使用首选的呈现引擎（通常是向后兼容模式）来显示内容
	public static final String X_XSS_PROTECTION = "X-XSS-Protection";//跨站点脚本（XSS）筛选器
	
	/**Apache HTTP服务*/
	public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";//代理客户端ID
	public static final String WL_PROXY_CLIENT_IP = "WL- Proxy-Client-IP";//代理来源IP

}