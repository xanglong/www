package com.xanglong.frame.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.CollectionUtil;
import com.xanglong.frame.util.RegExpUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;
import com.xanglong.i18n.zh_cn.SystemException;

public class HttpUtil {

	/**
	 * 返回字符
	 * @param response 响应对象
	 * @param text 内容
	 * */
	public static void responseText(HttpServletResponse response, ContentType contentType, String text) {
		response.resetBuffer();
		response.setContentType(contentType.getCode() + ";charset=" + Const.CHARSET_STR);
		try (PrintWriter printWriter = response.getWriter();) {
			printWriter.append(text);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 请求转发
	 * @param request 请求对象
	 * @param response 响应对象
	 * */
	public static void forward(HttpServletRequest request, HttpServletResponse response) {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(request.getRequestURI());
		try {
			requestDispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			throw new BizException(e);
		}    
	}

	/**
	 * 请求重定向
	 * @param request 请求对象
	 * @param response 响应对象
	 * */
	public static void redirect(HttpServletRequest request, HttpServletResponse response, String url) {
		try {
			response.sendRedirect(request.getContextPath() + url);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 返回图片
	 * @param response 响应对象
	 * @param bytes 二进制数据
	 * @param imageType 图片类型
	 * @param imageName 图片名称
	 * */
	public static void responseImage(HttpServletResponse response, byte[] bytes, ImageType imageType, String imageName) {
		response.resetBuffer();
		response.setContentType(imageType.getType());
		try (
			ServletOutputStream sos = response.getOutputStream();
			OutputStream outputStream = new BufferedOutputStream(sos);
		) {
			if (!StringUtil.isBlank(imageName)) {
				imageName = new String(imageName.getBytes(Const.CHARSET), "ISO-8859-1");
				response.setHeader(Header.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(imageName, Const.CHARSET_STR));
			}
			outputStream.write(bytes);
			outputStream.flush();
			response.flushBuffer();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 返回文件
	 * @param response 响应对象
	 * @param bytes 二进制数据
	 * @param 文件名称
	 * */
	public static void responseFile(HttpServletResponse response, byte[] bytes, String fileName) {
		response.resetBuffer();
		response.setContentType(ContentType.FORM_DATA.getCode());
		try (
			OutputStream outputStream = response.getOutputStream();
		){
			fileName = new String(fileName.getBytes("iso8859-1"), Const.CHARSET);
			response.setHeader(Header.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 请求校验
	 * @param requestDto 请求参数
	 * */
	private static void doRequestCheck(RequestDto requestDto) {
		if (requestDto == null) {
			throw new BizException(FrameException.FRAME_REQUEST_PARAM_NULL);
		}
		String url = requestDto.getUrl();
		if (!RegExpUtil.isUrl(url)) {
			throw new BizException(FrameException.FRAME_URL_ILLEGAL, url);
		}
		ContentType contentType = requestDto.getContentType();
		if (contentType == null) {
			throw new BizException(FrameException.FRAME_CONTENT_TYPE_NULL);
		}
	}
	
	/**
	 * 执行GET请求
	 * @param url 请求地址
	 * @return 响应结果
	 * */
	public static ResponseDto doGet(String url) {
		RequestDto requestDto = new RequestDto();
		requestDto.setUrl(url);
		return doGet(requestDto);
	}
	
	/**
	 * 执行POST请求
	 * @param url 请求地址
	 * @return 响应结果
	 * */
	public static ResponseDto doPost(String url) {
		RequestDto requestDto = new RequestDto();
		requestDto.setUrl(url);
		return doPost(requestDto);
	}
	
	/**
	 * 获取请求的query参数
	 * @param url 请求地址
	 * @param bodyParams 对象参数
	 * @return query参数
	 * */
	private static String getQuery(String url, JSONObject bodyParams) {
		int idx = url.indexOf("?");
		String query = idx == -1 ? "" : url.substring(idx + 1);
		if (bodyParams != null && !bodyParams.isEmpty()) {
			//处理有参无参时，第一个参数前是否需要加&
			query = query.length() > 0 ? query + "&" : query;
			StringBuilder queryBuilder = new StringBuilder();
			try {
	            for (String key : bodyParams.keySet()) {
	            	queryBuilder.append(key).append("=");
	            	String value = bodyParams.getString(key);
	            	//要处理好query参数的编码格式问题，不然解析会失败
					String encodeValue = URLEncoder.encode(value, Const.CHARSET_STR);
					queryBuilder.append(encodeValue).append("&");
	            }
			} catch (UnsupportedEncodingException e) {
				throw new BizException(e);
			}
			//截取bodyParams最后一个参数后面的&
			query += queryBuilder.substring(0, queryBuilder.length() - 1).toString();
		}
		return query;
	}

	/**
	 * 提取对象参数
	 * @param uriOrQuery URI地址或者查询参数
	 * @return 对象参数
	 * */
	private static JSONObject getParams(String uriOrQuery) {
		JSONObject params = new JSONObject();
		if (!StringUtil.isBlank(uriOrQuery)) {
			int indexOfQ = uriOrQuery.indexOf("?");
			String query = indexOfQ == -1 ? uriOrQuery : uriOrQuery.substring(indexOfQ + 1);
			String[] querys = query.split("&");
			for (String p : querys) {
				int idx = p.indexOf("=");
				if (idx == -1) {
					continue;
				}
				params.put(p.substring(0, idx), p.substring(idx + 1));
			}
		}
		return params;
	}
	
	/**
	 * 获取请求类型枚举
	 * @param contentType 请求类型字符串
	 * @return 请求类型枚举
	 * */
	public static ContentType getContentType(String contentType) {
		if (!StringUtil.isBlank(contentType)) {
			contentType = contentType.toLowerCase();
			if (contentType.contains("application/json")) {
				return ContentType.JSON;
			}
			if (contentType.contains("/html")) {
				return ContentType.HTML;
			}
			if (contentType.contains("/xml")) {
				return ContentType.XML;
			}
			if (contentType.contains("javascript")) {
				return ContentType.JS;
			}
			if (contentType.contains("/css")) {
				return ContentType.CSS;
			}
			if (contentType.contains("text/")) {
				return ContentType.FORM_TEXT;
			}
			if (contentType.contains("x-www-form-urlencoded")) {
				return ContentType.FORM_URLENCODED;
			}
			if (contentType.contains("multipart/form-data")) {
				return ContentType.FORM_DATA;
			}
			if (contentType.contains("video/") || contentType.contains("audio/")) {
				return ContentType.STREAM;
			}
		}
		return ContentType.JSON;
	}
	
	/**
	 * 是否是多文件请求
	 * @param request 请求对象
	 * @return 是否
	 * */
	public static boolean isMultipartContent(HttpServletRequest request) {
		//只有POST请求支持多文件传输
		if (!Method.POST.getCode().equals(request.getMethod())) {
			return false;
		}
		String contentType = request.getContentType();
		if (StringUtil.isBlank(contentType)) {
			return false;
		}
		//多文件请求目前标准是包含特殊字符串multipart
		if (contentType.toLowerCase(Locale.ENGLISH).startsWith(Header.MULTIPART)) {
            return true;
        }
		return false;
	}

	/**
	 * 设置请求头参数
	 * @param requestDto 请求参数
	 * @param httpURLConnection 连接
	 * */
	private static void setHeadData(RequestDto requestDto, HttpURLConnection httpURLConnection) {
		JSONObject headerParams = requestDto.getHeaderParams();
        if (headerParams !=null && !headerParams.isEmpty()) {
            for(String key : headerParams.keySet()){
            	httpURLConnection.setRequestProperty(key, headerParams.getString(key));
            }
        }
	}

	/**
	 * 重定向请求
	 * @param responseDto 响应结果
	 * @param method 请求方式
	 * */
	private static ResponseDto doRedirectRequest(ResponseDto responseDto, Method method) {
		RequestDto requestDto = new RequestDto();
		JSONObject headerParams = responseDto.getHeaderParams();
		requestDto.setUrl(responseDto.getHeader(Header.LOCATION));
    	try {
    		//记录原始请求地址
    		headerParams.put(Header.HOST, new URI(responseDto.getUrl()).getHost());
		} catch (URISyntaxException e) {
			throw new BizException(e);
		}
    	requestDto.setHeaderParams(headerParams);
    	if (Method.GET == method) {
    		return doGet(requestDto);
    	} else if (Method.POST == method) {
    		return doPost(requestDto);
    	} else if (Method.HEAD == method) {
    		return doHead(requestDto);
    	} else if (Method.PUT == method) {
    		return doPut(requestDto);
    	} else if (Method.DELETE == method) {
    		return doDelete(requestDto);
    	} else if (Method.PATCH == method) {
    		return doPatch(requestDto);
    	} else if (Method.OPTIONS == method) {
    		return doOptions(requestDto);
    	} else if (Method.CONNECT == method) {
    		return doConnect(requestDto);
    	} else if (Method.TRACE == method) {
    		return doTrace(requestDto);
    	}
    	return null;
	}
	
	/**
	 * 获取请求IP地址
	 * @param request 请求对象
	 * @return IP地址
	 * */
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader(Header.X_FORWARDED_FOR);
		if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader(Header.PROXY_CLIENT_IP);
		}
		if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader(Header.WL_PROXY_CLIENT_IP);
		}
		if (StringUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			ip = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
		}
		if (ip.indexOf(",") > 0) {
			ip = ip.substring(0, ip.indexOf(","));  
		}
		return ip;
	}

	/**
	 * 获取请求头参数参数
	 * @param request 请求对象
	 * @return 返回请求参数
	 * */
	public static JSONObject getHeaderParams(HttpServletRequest request) {
		JSONObject headerParams = new JSONObject();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			headerParams.put(key, value);
	    }
	    return headerParams;
	}
	
	/**
	 * 获取请求对象中的流
	 * @param request 请求对象
	 * @return 二进制数据
	 * */
	public static byte[] getBytes(HttpServletRequest request) {
		try (InputStream inputStream = request.getInputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		){
			byte[] buffer = new byte[Const.BUFFER_SIZE];
			int length;
			while ((length = inputStream.read(buffer)) > -1) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			byteArrayOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * 获取响应结果
	 * @param httpURLConnection 连接对象
	 * @param method 请求类型
	 * @return 响应结果
	 * */
	private static ResponseDto getResponseDto(HttpURLConnection httpURLConnection, Method method) {
		//响应状态编码
        int responseCode;
		try {
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			throw new BizException(e);
		}
		//开始接收响应数据
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUrl(httpURLConnection.getURL().toString());
        //由于重定向会返回一个网页回来，里面包含了重定向的地址，所以这里也要接收重定向的响应结果
        if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_MOVED_TEMP == responseCode) {
        	setResponseData(responseDto, httpURLConnection);
        } else {
        	//如果异常在枚举定义列表内则抛出定义内的异常，否则抛出自定义异常
        	for (SystemException systemException : SystemException.values()) {
        		if (systemException.name().equals("E" + responseCode)) {
        			throw new BizException(systemException);
        		}
        	}
        	throw new BizException(SystemException.E500);
        }
        //如果是重定向则递归调用请求方法
        if (HttpURLConnection.HTTP_MOVED_TEMP == responseCode) {
        	responseDto = doRedirectRequest(responseDto, method);
        }
		return responseDto;
	}

	/**
	 * 获取HTTP连接
	 * @param finallyUrl 最终请求的网址
	 * @param requestDto 请求参数
	 * @param BOUNDARY form-data请求标志位
	 * */
	private static HttpURLConnection getUrlConnection(String finallyUrl, RequestDto requestDto, Method method) {
		URLConnection urlConnection = null;
		try {
			urlConnection = new URL(finallyUrl).openConnection();
		} catch (IOException e) {
			throw new BizException(e);
		}
        urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestProperty(Header.CONTENT_ENCODING, Const.CHARSET_STR);
		//连接超时时间
		int connectionTimeout = requestDto.getConnectionTimeout();
		urlConnection.setConnectTimeout(connectionTimeout > 0 ? connectionTimeout : 30000);
		//读取超时时间
		int readTimeout = requestDto.getReadTimeout();
		urlConnection.setReadTimeout(readTimeout > 0 ? readTimeout : 30000);
		HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
		try {
			httpURLConnection.setRequestMethod(method.getCode());
		} catch (ProtocolException e) {
			throw new BizException(e);
		}
		ContentType contentType = requestDto.getContentType();
		httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, contentType.getCode() + "; charset=" + Const.CHARSET_STR);
		//默认请求头添加浏览器版本信息，因为像360问答没有这个就不会返回信息
		JSONObject headerParams = requestDto.getHeaderParams();
		if (headerParams == null || !headerParams.containsKey(Header.USER_AGENT)) {
			httpURLConnection.setRequestProperty(Header.USER_AGENT, UserAgent.Chrome62.getCode());
		}
		return httpURLConnection;
	}
	
	/**
	 * 执行GET请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doGet(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.GET);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.GET);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 发送POST请求数据
	 * @param httpURLConnection 连接对象
	 * @param postBytes 二进制数据
	 * @param requestDto 请求参数
	 * @param BOUNDARY form-data标志位
	 * */
	private static void sendPostData(HttpURLConnection httpURLConnection, byte[] postBytes, RequestDto requestDto, String BOUNDARY) {
		httpURLConnection.setDoOutput(true);
		try (
			OutputStream outputStream = httpURLConnection.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		) {
			if (postBytes != null) {
                dataOutputStream.write(postBytes);
            }
			//专门处理文件传输
			if (ContentType.FORM_DATA == requestDto.getContentType()) {
				setFormData(requestDto, BOUNDARY, dataOutputStream);
			}
            dataOutputStream.flush();
            outputStream.flush();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * 设置POST请求form-data二进制数据
	 * @param requestDto 请求参数
	 * @param BOUNDARY form-data标志位
	 * @param dataOutputStream 数据输送对象
	 * */
	private static void setFormData(RequestDto requestDto, String BOUNDARY, DataOutputStream dataOutputStream) throws IOException {
		List<RequestFileDto> files = requestDto.getFiles();
		if (!CollectionUtil.isEmpty(files)) {
			for (RequestFileDto file : files) {
				String name = file.getName(), fileName = file.getFileName(), fileType = "";
				if (!StringUtil.isBlank(fileName)) {
					int idx = fileName.lastIndexOf(".");
					fileType = idx == -1 ? "" : fileName.substring(idx + 1);
				}
				if (StringUtil.isBlank(name)) {
					name = fileName.replace("." + fileType, "");
				}
				StringBuilder paramBuilder = new StringBuilder();
				paramBuilder.append("\r\n--").append(BOUNDARY)
				.append("\r\nContent-Disposition: form-data; name=\"").append(name).append("\"; filename=\"").append(fileName).append("\"\r\n")
				.append(Header.CONTENT_TYPE).append(":").append(ContentType.STREAM.getCode()).append("\r\n\r\n");
				dataOutputStream.write(paramBuilder.toString().getBytes());
				//输入完文件头信息，再输入文件信息
				dataOutputStream.write(file.getBytes());
			}
			dataOutputStream.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
		}
	}
	
	/**
	 * 执行POST请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doPost(RequestDto requestDto) {
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		String BOUNDARY = "------------" + StringUtil.getUUID();
		if (postBytes == null) {
			postBytes = getPostBytes(requestDto, BOUNDARY);
		}
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.POST);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//发送POST请求数据
		sendPostData(httpURLConnection, postBytes, requestDto, BOUNDARY);
		ContentType contentType = requestDto.getContentType();
		String contentTypeValue = contentType.getCode() + "; charset=" + Const.CHARSET_STR;
		if (ContentType.FORM_DATA == contentType) {
			httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, contentTypeValue + "; boundary=" + BOUNDARY);
		}
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.GET);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 接收响应数据
	 * @param responseDto 响应结果
	 * @param httpURLConnection 连接
	 * @param requestDto 请求参数
	 * */
	private static void setResponseData(ResponseDto responseDto, HttpURLConnection httpURLConnection) {
		try (
        	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        	InputStream inputStream = httpURLConnection.getInputStream();
        ) {
			int len = 0;
			byte[] buffer = new byte[Const.BUFFER_SIZE];
			while ((len = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, len);
			}
			byteArrayOutputStream.flush();
			responseDto.setBytes(byteArrayOutputStream.toByteArray());
			JSONObject header = new JSONObject();
			Map<String, List<String>> fieldsMap = httpURLConnection.getHeaderFields();
			String contentType = null;
			for (String key : fieldsMap.keySet()) {
			    if (key != null) {
			    	String value = fieldsMap.get(key).get(0);
			    	value = new String(value.getBytes("iso-8859-1"), Const.CHARSET);
			    	if (contentType == null && key.toLowerCase().equals("content-type")) {
			    		contentType = value;
			    	}
			    	header.put(key, value);
			    }
			}
			responseDto.setHeaderParams(header);
			//考虑到对外接口响应的文档类型不在我的枚举定义范围之内，这里用字符串类型接收
			responseDto.setContentType(StringUtil.isBlank(contentType) ? httpURLConnection.getContentType() : contentType);
			byteArrayOutputStream.close();
        } catch (IOException e) {
        	throw new BizException(e);
		}
	}

	/**
	 * 提取请求体参数
	 * @param request 请求对象
	 * @return 请求体参数
	 * */
	public static BodyParam getBodyParams(HttpServletRequest request) {
		BodyParam bodyParam = new BodyParam();
		JSONObject object = new JSONObject();
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String key = parameterNames.nextElement();
			String value = request.getParameter(key);
			object.put(key, value);
		}
		String method = request.getMethod();
		if (Method.POST.getCode().equals(method)) {
			//如果请求头标记类型位JSON类型，则读取二进制参数，否则不读取
			String contentType = request.getContentType().toLowerCase();
			if (contentType.contains(ContentType.JSON.getCode())) {
				String json = null;
				try (InputStream inputStream = request.getInputStream();
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				){
					byte[] buffer = new byte[Const.BUFFER_SIZE];
					int length;
					while ((length = inputStream.read(buffer)) > -1) {
						byteArrayOutputStream.write(buffer, 0, length);
					}
					byteArrayOutputStream.flush();
					json = new String(byteArrayOutputStream.toByteArray(), Const.CHARSET);
				} catch (IOException e) {
					throw new BizException(e);
				}
				if (!StringUtil.isBlank(json)) {
					if (json.charAt(0) == '{') {
						object.putAll(JSONObject.parseObject(json));
					} else {
						bodyParam.setArray(JSONArray.parseArray(json));
					}
				}
			}
		}
		bodyParam.setObejct(object);
		return bodyParam;
	}
	
	/**
	 * 获取POST请求的参数的二进制格式
	 * @param requestDto 请求参数
	 * @param BOUNDARY formData参数标志位
	 * @return 参数的二进制
	 * */
	private static byte[] getPostBytes(RequestDto requestDto, String BOUNDARY) {
		byte[] requestBytes = null;
		String url = requestDto.getUrl();
		ContentType contentType = requestDto.getContentType();
		JSONObject bodyParams = requestDto.getBodyParams();
		bodyParams = bodyParams == null ? new JSONObject() : bodyParams;
		if (ContentType.JSON == contentType || ContentType.HTML == contentType 
			|| ContentType.CSS == contentType || ContentType.JS == contentType || ContentType.XML == contentType) {
			bodyParams.putAll(getParams(url));
			requestBytes = bodyParams.toJSONString().getBytes(Const.CHARSET);
		} else if (ContentType.FORM_URLENCODED == contentType || ContentType.FORM_TEXT == contentType) {
			requestBytes = getQuery(url, bodyParams).getBytes(Const.CHARSET);
		} else if (ContentType.FORM_DATA == contentType) {
			bodyParams.putAll(getParams(url));
			StringBuilder queryBuilder = new StringBuilder();
			for (String key : bodyParams.keySet()) {
				queryBuilder.append("\r\n--")
				.append(BOUNDARY)
				.append("\r\nContent-Disposition: form-data; name=\"")
				.append(key).append("\"\r\n\r\n")
				.append(bodyParams.getString(key));
			}
			requestBytes = queryBuilder.toString().getBytes(Const.CHARSET);
		}
		return requestBytes;
	}
	
	/**
	 * 返回返回结果
	 * @param response 响应对象
	 * @param responseDto 返回对象
	 * */
	public static void responseDto(HttpServletResponse response, ResponseDto responseDto) {
		String contentTypeString = responseDto.getContentType();
		ContentType contentType = getContentType(responseDto.getContentType()); 
		if (contentType == ContentType.HTML) {
			HttpUtil.responseText(response, ContentType.HTML, new String(responseDto.getBytes()));
		} else if (contentType == ContentType.CSS) {
			HttpUtil.responseText(response, ContentType.CSS, new String(responseDto.getBytes()));
		} else if (contentType == ContentType.JS) {
			HttpUtil.responseText(response, ContentType.JS, new String(responseDto.getBytes()));
		} else if (contentType == ContentType.FORM_TEXT) {
			HttpUtil.responseText(response, ContentType.FORM_TEXT, new String(responseDto.getBytes()));
		} else if (contentType == ContentType.FORM_DATA) {
			String disposition = responseDto.getHeader(Header.CONTENT_DISPOSITION);
			String fileName = disposition.substring(disposition.indexOf("filename") + 8);
			HttpUtil.responseFile(response, responseDto.getBytes(), fileName);
		} else {
			String disposition = responseDto.getHeader(Header.CONTENT_DISPOSITION);
			String fileName = null;
			if (!StringUtil.isBlank(disposition)) {
				fileName = disposition.substring(disposition.indexOf("filename") + 8);
			}
			boolean isImage = false;
			for (ImageType imageType : ImageType.values()) {
				if (contentTypeString.contains(imageType.getCode())) {
					isImage = true;
					HttpUtil.responseImage(response, responseDto.getBytes(), imageType, fileName);
					break;
				}
			}
			//由于getContentType默认值返回JSON，所以要最后处理
			if (!isImage && contentType == ContentType.JSON) {
				HttpUtil.responseText(response, ContentType.JSON, new String(responseDto.getBytes()));
			}
		}
	}
	
	/**
	 * 执行HEAD请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doHead(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.HEAD);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.HEAD);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}

	/**
	 * 执行PUT请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doPut(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.PUT);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.PUT);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 执行DELETE请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doDelete(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.DELETE);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.DELETE);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 执行PATCH请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doPatch(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.PATCH);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.PATCH);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 执行OPTIONS请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doOptions(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.OPTIONS);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.OPTIONS);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}
	
	/**
	 * 执行CONNECT请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doConnect(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.CONNECT);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.CONNECT);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}

	/**
	 * 执行TRACE请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doTrace(RequestDto requestDto) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		if (postBytes != null) {
			throw new BizException(FrameException.FRAME_GET_REQUEST_BYTES_DATA_INVALID);
		}
		int idx = url.indexOf("?");
		String origin = idx == -1 ? url : url.substring(0, idx);
		url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, Method.TRACE);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//获取响应结果
		ResponseDto responseDto = getResponseDto(httpURLConnection, Method.TRACE);
		//编码格式传递
		responseDto.setCharset(requestDto.getCharset());
		return responseDto;
	}

}