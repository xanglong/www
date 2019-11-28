package com.xanglong.frame.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		response.setContentType(contentType.getCode() + ";charset=" + Const.CHARSET_STR);
		try (PrintWriter printWriter = response.getWriter();) {
			printWriter.append(text);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	/**
	 * 重定向
	 * @param request 请求对象
	 * @param response 响应对象
	 * */
	public static void forward(HttpServletRequest request, HttpServletResponse response, String uri) {
		try {
			response.sendRedirect(request.getContextPath() + uri);
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
		response.addHeader(Header.CONTENT_LENGTH, "" + bytes.length);
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
	 * @param file 文件对象
	 * @param 文件名称
	 * */
	public static void responseFile(HttpServletResponse response, File file, String fileName) {
		response.setContentType(ContentType.FORM_DATA.getCode());
		try (
			OutputStream outputStream = response.getOutputStream();
			InputStream inputStream = new FileInputStream(file);
		){
			fileName = new String(fileName.getBytes("iso8859-1"), Const.CHARSET);
			response.setHeader(Header.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
			byte[] buffer = new byte[Const.BUFFER_SIZE];
			int i = -1;
			while ((i = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, i);
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new BizException(e);
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
		return doRequest(requestDto, Method.GET);
	}
	
	/**
	 * 执行GET请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doGet(RequestDto requestDto) {
		return doRequest(requestDto, Method.GET);
	}

	/**
	 * 执行POST请求
	 * @param requestDto 请求参数体
	 * @return 响应结果
	 * */
	public static ResponseDto doPost(RequestDto requestDto) {
		return doRequest(requestDto, Method.POST);
	}

	/**
	 * 请求校验
	 * @param requestDto 请求参数
	 * */
	private static void doRequestCheck(RequestDto requestDto) {
		if (requestDto == null) {
			throw new BizException(FrameException.FRAME_REQUEST_PARAM_CANT_NOT_NULL);
		}
		String url = requestDto.getUrl();
		if (!RegExpUtil.isUrl(url)) {
			throw new BizException(FrameException.FRAME_URL_ILLEGAL, url);
		}
		ContentType contentType = requestDto.getContentType();
		if (contentType == null) {
			throw new BizException(FrameException.FRAME_CONTENT_TYPE_CANT_NOT_NULL);
		}
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
		if (ContentType.FORM_URLENCODED == contentType) {
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
		} else if (ContentType.FORM_TEXT == contentType) {
			requestBytes = getQuery(url, bodyParams).getBytes(Const.CHARSET);
		} else if (ContentType.JSON == contentType) {
			bodyParams.putAll(getParams(url));
			requestBytes = bodyParams.toJSONString().getBytes(Const.CHARSET);
		}
		return requestBytes;
	}
	
	/**
	 * 获取HTTP连接
	 * @param finallyUrl 最终请求的网址
	 * @param requestDto 请求参数
	 * @param BOUNDARY form-data请求标志位
	 * */
	private static HttpURLConnection getUrlConnection(String finallyUrl, RequestDto requestDto, Method method, String BOUNDARY) {
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
		String contentTypeValue = contentType.getCode() + "; charset=" + Const.CHARSET_STR;
		if (ContentType.FORM_DATA == contentType) {
			httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, contentTypeValue + "; boundary=" + BOUNDARY);
		} else {
			httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, contentTypeValue);
		}
		//默认请求头添加浏览器版本信息，因为像360问答没有这个就不会返回信息
        httpURLConnection.setRequestProperty(Header.USER_AGENT, UserAgent.Chrome62.getCode());
		return httpURLConnection;
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
	 * 接收响应数据
	 * @param responseDto 响应结果
	 * @param httpURLConnection 连接
	 * @param requestDto 请求参数
	 * */
	private static void setResponseData(ResponseDto responseDto, HttpURLConnection httpURLConnection, RequestDto requestDto) {
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
			Charset charset = requestDto.getCharset();
			responseDto.setBytes(byteArrayOutputStream.toByteArray());
			responseDto.setCharset(charset);
			JSONObject header = new JSONObject();
			Map<String, List<String>> fieldsMap = httpURLConnection.getHeaderFields();
			for (String key : fieldsMap.keySet()) {
			    if (key != null) {
			    	String value = fieldsMap.get(key).get(0);
			    	header.put(key, new String(value.getBytes("iso-8859-1"), Const.CHARSET));
			    }
			}
			responseDto.setHeaderParams(header);
			//获取响应头的文档类型,正常来说请求头是什么文档类型返回也应该是什么文档类型，但是以防万一还是要去取一下响应头的文档类型
			String contentType = header.getString(Header.CONTENT_TYPE);
			responseDto.setContentType(StringUtil.isBlank(contentType) ? httpURLConnection.getContentType() : contentType);
			byteArrayOutputStream.close();
        } catch (IOException e) {
        	throw new BizException(e);
		}
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
	 * 执行请求
	 * @param requestDto 请求参数体
	 * @param method 请求方法
	 * @return 响应结果
	 * */
	private static ResponseDto doRequest(RequestDto requestDto, Method method) {
		//参数校验
		doRequestCheck(requestDto);
		String url = requestDto.getUrl();
		byte[] postBytes = requestDto.getBytes();
		String BOUNDARY = "------------" + StringUtil.getUUID();
		if (Method.GET == method) {
			int idx = url.indexOf("?");
			String origin = idx == -1 ? url : url.substring(0, idx);
			url = origin + "?" + getQuery(url, requestDto.getBodyParams());
		} else if (Method.POST == method && postBytes == null) {
			postBytes = getPostBytes(requestDto, BOUNDARY);
		}
		//获取连接
		HttpURLConnection httpURLConnection = getUrlConnection(url, requestDto, method, BOUNDARY);
		//设置头参数
		setHeadData(requestDto, httpURLConnection);
		//发送POST请求数据
        if (Method.POST == method) {
        	sendPostData(httpURLConnection, postBytes, requestDto, BOUNDARY);
		}
        //响应状态编码
        int responseCode;
		try {
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			throw new BizException(e);
		}
		//开始接收响应数据
        ResponseDto responseDto = new ResponseDto();
        responseDto.setUrl(url);
        //由于重定向会返回一个网页回来，里面包含了重定向的地址，所以这里也要接收重定向的响应结果
        if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_MOVED_TEMP == responseCode) {
        	setResponseData(responseDto, httpURLConnection, requestDto);
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
    	return doRequest(requestDto, method);
	}

}