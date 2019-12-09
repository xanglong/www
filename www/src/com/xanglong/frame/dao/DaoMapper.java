package com.xanglong.frame.dao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONObject;
import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.exception.ThrowableHandler;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.util.BaseUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

/**XML配置文件*/
public class DaoMapper {

	/**根节点缓存*/
	private static Map<String, Element> roots = new HashMap<>();
	/**XML文件名称缓存*/
	private static Map<String, String> mapperNames = new HashMap<>();
	/**二分查询节点缓存*/
	private static Map<String, Map<String, Node>> nodeCaches = new HashMap<>();

	/**当前文件ID*/
	private String currentNamespace = null;
	/**当前文件名称*/
	private String currentMapperName = null;
	/**当前根节点元素*/
	private Element currentRoot = null;
	/**当前文件节点缓存*/
	private Map<String, Node> currentNodeCahce = null;
	
	/**初始化方法*/
	public void init() {
		//递归获取所有文件，然后多线程操作文件读取
		List<File> files = new ArrayList<>();
		String classPath = BaseUtil.getClassPath();
		String[] mapperPackages = Sys.getConfig().getPackages().getMapper();
		for (String mapperPackage : mapperPackages) {
			File file = new File(classPath + mapperPackage.replace(".", "/"));
			if (file.exists()) {
				for (File f : file.listFiles()) {
					getMapperFiles(f, files);
				}
			}
		}
		//多线程同步解析XML文件
		setAllMapperFiles(files);
		//设置文档节点缓存
		setAllNodeCaches();
		//单线程串行校验XML文件
		for (Entry<String, Element> entry : roots.entrySet()) {
			currentRoot = entry.getValue();//当前文件根节点
			currentNamespace = currentRoot.getAttribute(MapperAttribute.NAMESPACE.getCode());//当前命名空间
			currentMapperName = mapperNames.get(currentNamespace);//当前文件名
			//校验根节点是否合法
			checkNode(currentRoot);
			currentNodeCahce = nodeCaches.get(currentNamespace);//当前节点缓存
			NodeList nodeList = currentRoot.getChildNodes();
			//对子节点做遍历校验
			for (int i = 0, length = nodeList.getLength(); i < length; i++) {
				Node node = nodeList.item(i);
				checkNode(node);
				deepChekNode(node);
			}
		}
		mapperNames= null;
		roots = null;
	}

	/**
	 * 获取预编译SQL语句
	 * @param namespace 命名空间
	 * @param id 节点id
	 * @param webParams 参数
	 * */
	protected static MapperSql getMapperSqlJo(String namespace, String id, JSONObject webParams) {
		Map<String, Node> nodes = nodeCaches.get(namespace);
		if (nodes == null) {
			throw new BizException(FrameException.FRAME_NAMESPACE_NULL, namespace);
		}
		Node node = nodes.get(id);
		if (node == null) {
			throw new BizException(FrameException.FRAME_NAMESPACE_SQL_NULL, namespace, id);
		}
		String nodeName = node.getNodeName();
		MapperSql mapperSqlJo = new MapperSql();
		boolean isFindSqlType = false;
		for (SqlType sqlType : SqlType.values()) {
			if (sqlType.getCode().equals(nodeName)) {
				isFindSqlType = true;
				mapperSqlJo.setSqlType(sqlType);
				break;
			}
		}
		if (!isFindSqlType) {
			throw new BizException(FrameException.FRAME_MAPPER_NODE_TYPE_INVALID, nodeName);
		}
		StringBuilder preSql = new StringBuilder();
		NodeList nodeList = node.getChildNodes();
		webParams = webParams == null ? new JSONObject() : webParams;
		for (int i = 0, length = nodeList.getLength(); i < length; i++) {
			Node subNode = nodeList.item(i);
			deepSetSubNodeText(namespace, subNode, preSql, webParams);
		}
		mapperSqlJo.setPreSql(preSql.toString());
		return mapperSqlJo;
	}

	/**
	 * 递归获取节点文本
	 * @param namespace 命名空间
	 * @param node 节点
	 * @param preSql 预编译SQL
	 * @param webParams 参数
	 * */
	private static void deepSetSubNodeText(String namespace, Node node, StringBuilder preSql, JSONObject webParams) {
		short nodeType = node.getNodeType();
		if (nodeType == 3) {
			String text = node.getTextContent();
			if (text.length() > 0) {
				preSql.append(text);
				if (text.charAt(text.length() - 1) != '\n') {
					preSql.append("\n");
				}
			}
		} else if (nodeType == 1) {
			String nodeName = node.getNodeName();
			if (MapperTag.INCLUDE.getCode().equals(nodeName)) {
				NamedNodeMap namedNodeMap = node.getAttributes();
				String refid = null;
				for (int i = 0, length = namedNodeMap.getLength(); i< length; i++) {
					Node attribute = namedNodeMap.item(i);
					if (MapperAttribute.REFID.getCode().equals(attribute.getNodeName())) {
						refid = attribute.getNodeValue();
						break;
					}
				}
				if (refid.contains(".")) {
					int lastIndexOfPoint = refid.lastIndexOf(".");
					String otherNamespace = refid.substring(0, lastIndexOfPoint);
					Map<String, Node> nodeCahce = nodeCaches.get(otherNamespace);
					String nodeId = refid.substring(lastIndexOfPoint + 1);
					Node includeNode = nodeCahce.get(nodeId);
					//找到远程节点，继续递归
					deepSetSubNodeText(otherNamespace, includeNode, preSql, webParams);
				} else {
					Map<String, Node> nodeCahce = nodeCaches.get(namespace);
					Node includeNode = nodeCahce.get(refid);
					deepSetSubNodeText(namespace, includeNode, preSql, webParams);
				}
			} else if (MapperTag.SQL.getCode().equals(nodeName)) {
				NodeList nodeList = node.getChildNodes();
				for (int i = 0, length = nodeList.getLength(); i < length; i++) {
					Node subNode = nodeList.item(i);
					deepSetSubNodeText(namespace, subNode, preSql, webParams);
				}
			} else if (MapperTag.WHERE.getCode().equals(nodeName)) {
				preSql.append("WHERE 1 = 1 \n");
				NodeList nodeList = node.getChildNodes();
				for (int i = 0, length = nodeList.getLength(); i < length; i++) {
					Node subNode = nodeList.item(i);
					deepSetSubNodeText(namespace, subNode, preSql, webParams);
				}
			} else if (MapperTag.IF.getCode().equals(nodeName)) {
				NamedNodeMap namedNodeMap = node.getAttributes();
				String express = null;
				for (int i = 0, length = namedNodeMap.getLength(); i< length; i++) {
					Node attribute = namedNodeMap.item(i);
					if (MapperAttribute.TEST.getCode().equals(attribute.getNodeName())) {
						express = attribute.getNodeValue();
						break;
					}
				}
				//如果表达式校验通过，则递归获取节点内容
				if (DaoOgnl.test(express, webParams)) {
					NodeList nodeList = node.getChildNodes();
					for (int i = 0, length = nodeList.getLength(); i < length; i++) {
						Node subNode = nodeList.item(i);
						deepSetSubNodeText(namespace, subNode, preSql, webParams);
					}
				}
			} else if (MapperTag.FOREACH.getCode().equals(nodeName)) {
				//针对集合，需要单独处理解析当前节点下的所有文本，之后再拼接成一个
				StringBuilder forEachPreSql = new StringBuilder();
				NodeList nodeList = node.getChildNodes();
				for (int i = 0, length = nodeList.getLength(); i < length; i++) {
					Node subNode = nodeList.item(i);
					deepSetSubNodeText(namespace, subNode, forEachPreSql, webParams);
				}
				String sql = forEachPreSql.toString();
				NamedNodeMap attributes = node.getAttributes();
				String sqls = DaoOgnl.foreach(sql, webParams, attributes);
				preSql.append(sqls);
			} else {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_SQL_INVALID, nodeName);
			}
		}
	}

	/**先设置二级节点缓存*/
	private void setAllNodeCaches() {
		for (Entry<String, Element> entry : roots.entrySet()) {
			Map<String, Node> nodeCache = new HashMap<>();
			Element root = entry.getValue();
			String nameSpace = root.getAttribute(MapperAttribute.NAMESPACE.getCode());
			NodeList nodeList = root.getChildNodes();
			for (int i = 0, length = nodeList.getLength(); i < length; i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() != 1) {
					continue;
				}
				NamedNodeMap namedNodeMap = node.getAttributes();
				for (int j = 0; j < namedNodeMap.getLength(); j++) {
					Node attribute = namedNodeMap.item(j);
					String attributeName = attribute.getNodeName();
					if (!MapperAttribute.ID.getCode().equals(attributeName)) {
						continue;
					}
					String id = attribute.getNodeValue();
					if (nodeCache.containsKey(id)) {
						throw new BizException(FrameException.FRAME_MAPPER_NAMESPACE_ID_REPEAT, nameSpace, id);
					}
					//每个节点明细按照id划分
					nodeCache.put(id, node);
					break;
				}
			}
			//节点缓存按照命名空间划分
			nodeCaches.put(nameSpace, nodeCache);
		}
	}

	/**
	 * 递归校验XML节点是否合理
	 * @param node 节点对象
	 * */
	private void deepChekNode(Node node) {
		if (node.getNodeType() != 1) {
			return;
		}
		String nodeName = node.getNodeName();
		if (MapperTag.SELECT.getCode().equals(nodeName) || MapperTag.INSERT.getCode().equals(nodeName)
			|| MapperTag.UPDATE.getCode().equals(nodeName) 	|| MapperTag.IF.getCode().equals(nodeName)
			|| MapperTag.INCLUDE.getCode().equals(nodeName) || MapperTag.WHERE.getCode().equals(nodeName)
			|| MapperTag.FOREACH.getCode().equals(nodeName) || MapperTag.DELETE.getCode().equals(nodeName) 
			|| MapperTag.SQL.getCode().equals(nodeName) || MapperTag.SELECTKEY.getCode().equals(nodeName)) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node subNode = nodeList.item(i);
				checkNode(subNode);
				deepChekNode(subNode);
			}
		} else {
			throw new BizException(FrameException.FRAME_MAPPER_NODE_RECURSION_CHECK_NULL, currentMapperName, nodeName);
		}
	}

	/**
	 * 校验XML节点是否合法
	 * @param node XML文档节点
	 * */
	private void checkNode(Node node) {
		if (node.getNodeType() != 1) {
			return;
		}
		//校验标签
		MapperTag mapperTag = checkTag(node.getNodeName());
		NamedNodeMap attributes = node.getAttributes();
		if (attributes.getLength() > 0) {
			for (MapperNode mapperNode : MapperNode.values()) {
				if (mapperNode.getTag() != mapperTag) {
					continue;
				}
				MapperAttribute mapperAttribute = mapperNode.getAttribute();
				//校验节点下的属性是否合法，只能限定的属性才可以配置
				boolean isAttributeExist = false;
				String nodeValue = null;
				for (int i = 0; i < attributes.getLength(); i++) {
					Node nodeAttribute = attributes.item(i);
					MapperAttribute attribute = checkAttribute(nodeAttribute.getNodeName());
					//找到目标属性及其值
					if (mapperNode.getAttribute() == attribute) {
						isAttributeExist = true;
						nodeValue = nodeAttribute.getNodeValue();
						break;
					}
				}
				//必填属性的键不能为空
				if (mapperNode.getIsRequire()) {
					if (isAttributeExist) {
						//存在则校验合法性
						checkNodeAttribute(mapperTag, mapperAttribute, nodeValue);
					} else {
						//不存在则提示异常
						throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), mapperAttribute.getCode());
					}
				}
			}
		} else {
			//校验节点属性值
			checkNodeAttribute(mapperTag, null, null);	
		}
	}

	/**
	 * 校验节点必填属性值
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkNodeAttribute(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (MapperTag.SELECT == mapperTag) {//校验查询
			checkIdNotPoint(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.INSERT == mapperTag) {//校验新增
			checkIdNotPoint(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.UPDATE == mapperTag) {//校验编辑
			checkIdNotPoint(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.IF == mapperTag) {//判断条件
			checkTestNoNull(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.WHERE == mapperTag) {
		} else if (MapperTag.DELETE == mapperTag) {//校验删除
			checkIdNotPoint(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.MAPPER == mapperTag) {//校验命名空间
			checkNamespace(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.INCLUDE == mapperTag) {//校验依赖语句
			checkRefId(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.SQL == mapperTag) {//校验SQL语句
			checkIdNotPoint(mapperTag, mapperAttribute, nodeValue);
		} else if (MapperTag.FOREACH == mapperTag) {//校验集合
			checkCollectionNotNull(mapperTag, mapperAttribute, nodeValue);//校验集合键
			checkIndexNotNull(mapperTag, mapperAttribute, nodeValue);//校验集合索引
		} else if (MapperTag.SELECTKEY == mapperTag) {//校验键
			checkResultType(mapperTag, mapperAttribute, nodeValue);
		} else {
			throw new BizException(FrameException.FRAME_MAPPER_NODE_CHECK_NULL, currentMapperName, mapperTag.getCode());
		}
	}

	/**
	 * 校验集合索引
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkIndexNotNull(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.INDEX == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(
					FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL,
					currentMapperName, mapperTag.getCode(), MapperAttribute.INDEX.getCode()
				);
			}
		}
	}

	/**
	 * 校验集合键
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkCollectionNotNull(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.COLLECTION == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(
					FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL,
					currentMapperName, mapperTag.getCode(), MapperAttribute.COLLECTION.getCode()
				);
			}
		}
	}

	/**
	 * 校验依赖ID
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkRefId(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.REFID == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), MapperAttribute.REFID.getCode());
			} else if (!nodeValue.contains(".")) {
				if (!currentNodeCahce.containsKey(nodeValue)) {
					throw new BizException(FrameException.FRAME_MAPPER_NODE_REFID_NULL, currentMapperName, mapperTag.getCode(), nodeValue);
				}
			} else {
				//包含有点的依赖ID，默认最后一段为子ID,前面的算命名空间
				int lastIndexOfPoint = nodeValue.lastIndexOf(".");
				String namespace = nodeValue.substring(0, lastIndexOfPoint);
				Map<String, Node> nodeCahce = nodeCaches.get(namespace);
				if (nodeCahce == null) {
					throw new BizException(FrameException.FRAME_MAPPER_NODE_REFID_FILE_NULL, currentMapperName, mapperTag.getCode(), nodeValue);
				}
				String nodeId = nodeValue.substring(lastIndexOfPoint + 1);
				if (nodeCahce.get(nodeId) == null) {
					throw new BizException(FrameException.FRAME_MAPPER_NODE_REFID_FILE_NULL, currentMapperName, mapperTag.getCode(), nodeValue, nodeId);
				}
			}
		}
	}

	/**
	 * 校验判断条件不能为空
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkTestNoNull(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.TEST == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), MapperAttribute.TEST.getCode());
			}
		}
	}

	/**
	 * 校验返回类型
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkResultType(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.RESULTTYPE == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), MapperAttribute.RESULTTYPE.getCode());
			} else if (",long,int,short,double,string,boolean,".indexOf("," + nodeValue + ",") == -1) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_RESULT_TYPE_INVALID, currentMapperName, mapperTag.getCode(), nodeValue);
			}
		}
	}

	/**
	 * 校验ID
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkIdNotPoint(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.ID == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), MapperAttribute.ID.getCode());
			} else if (nodeValue.contains(".")) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ID_INVALID, currentMapperName, mapperTag.getCode(), nodeValue);
			}
		}
	}

	/**
	 * 校验命名空间
	 * @param mapperTag 标签枚举
	 * @param mapperAttribute 属性枚举
	 * @param nodeValue 属性值
	 * */
	private void checkNamespace(MapperTag mapperTag, MapperAttribute mapperAttribute, String nodeValue) {
		if (mapperAttribute == null || MapperAttribute.NAMESPACE == mapperAttribute) {
			if (StringUtil.isBlank(nodeValue)) {
				throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_VALUE_NULL, currentMapperName, mapperTag.getCode(), MapperAttribute.NAMESPACE.getCode());	
			}
			try {
				Class.forName(nodeValue);
			} catch (ClassNotFoundException e) {
				throw new BizException(FrameException.FRAME_MAPPER_NAMESPACE_CLASS_NULL, currentMapperName, nodeValue);
			}
		}
	}

	/**
	 * 校验属性
	 * @param attributeName 属性名称
	 * @return 支持的属性枚举
	 * */
	private MapperAttribute checkAttribute(String attributeName) {
		for (MapperAttribute MapperAttribute : MapperAttribute.values()) {
			if (MapperAttribute.getCode().equals(attributeName)) {
				return MapperAttribute;
			}
		}
		throw new BizException(FrameException.FRAME_MAPPER_NODE_ATTRIBUTE_INVALID, currentMapperName, attributeName);
	}

	/**
	 * 校验节点标签
	 * @param nodeName 标签名称
	 * @return 支持的标签枚举
	 * */
	private MapperTag checkTag(String nodeName) {
		for (MapperTag mapperTag : MapperTag.values()) {
			if (mapperTag.getCode().equals(nodeName)) {
				return mapperTag;
			}
		}
		throw new BizException(FrameException.FRAME_MAPPER_NODE_INVALID, currentMapperName, nodeName);
	}

	/**
	 * 解析获取所有XML文件，根据文件数量分配每个线程处理的文件数量
	 * @param listFiles 文件列表
	 * */
	private void setAllMapperFiles(List<File> listFiles) {
		int threadSize = 30, fileSize = listFiles.size();
		HandelThreadFactory handelThreadFactory = new HandelThreadFactory();
		ExecutorService executorService = Executors.newCachedThreadPool(handelThreadFactory);
		int yushu = fileSize % threadSize;
		int shang = (fileSize - yushu) / threadSize;
		for (int i = 0; i < shang; i++) {
			File[] files = new File[threadSize];
			for (int j = 0; j < threadSize; j++) {
				files[j] = listFiles.get(i * threadSize + j);
			}
			executorService.execute(new ThredMapper(files));
		}
		if (yushu > 0) {
			File[] files = new File[yushu];
			for (int i = shang * threadSize; i < fileSize; i++) {
				files[fileSize - i - 1] = listFiles.get(i);
			}
			executorService.execute(new ThredMapper(files));
		}
		executorService.shutdown();
		try {
			//等待同步线程执行完
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {}
        } catch (InterruptedException e) {
            throw new BizException(e);
        }
	}

	/**递归获取文件*/
	private void getMapperFiles(File file, List<File> files) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				getMapperFiles(f, files);
			}
		} else {
			if (!file.getName().endsWith(".xml")) {
				return;
			}
			files.add(file);
		}
	}

	/**异常捕获回调类*/
	class MyUncaughtExceptionhandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread threadt, Throwable throwable) {
			ThrowableHandler.dealException(throwable);
		}
	}

	/**线程回调工厂类*/
	class HandelThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setUncaughtExceptionHandler(new MyUncaughtExceptionhandler());
			return thread;
		}
		
	}
	
	/**忽略DTD文件校验的内部类*/
	class IgnoreDTDEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId) {
			String dtdName = "";
			if (!StringUtil.isBlank(systemId)) {
				int lastIdx = systemId.lastIndexOf("/");
				dtdName = systemId.substring(lastIdx);
			}
			String savePath = BaseUtil.getSavePath();
			File dtdFile = new File(savePath + Const.DTD_FOLDER_NAME + dtdName);
			if (dtdFile.exists()) {
				return new InputSource(new ByteArrayInputStream(FileUtil.readByte(dtdFile)));
			}
			return null;
		}
	}

	/**线程解析XML文件类*/
	class ThredMapper implements Runnable {
		private File[] files;
		public ThredMapper(File[] files) {
			this.files = files;
		}
		@Override
		public void run() {
			try {
				DocumentBuilder documentBuilder = null;
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				//不校验DTD文件，网络不通的时候会失败
				documentBuilderFactory.setValidating(false);
				try {
					documentBuilder = documentBuilderFactory.newDocumentBuilder();
					documentBuilder.setEntityResolver(new IgnoreDTDEntityResolver());
				} catch (ParserConfigurationException e) {
					throw new BizException(e);
				}
				for (File file : files) {
					String basePath = Const.BASE_PACKAGE_NAME.replaceAll("\\.", "\\\\");
					String filePath = file.getPath();
					filePath = filePath.substring(filePath.indexOf("\\classes\\" + basePath) + 9);
					String mapper = filePath.replace("\\", ".");
					Document document = documentBuilder.parse(file);
					document.normalize();
					Element rootElement = document.getDocumentElement();
					String tagName = rootElement.getTagName();
					if (!MapperTag.MAPPER.getCode().equals(tagName)) {
						throw new BizException(FrameException.FRAME_MAPPER_MISS_ROOT_NODE, mapper);
					}
					String namespace = rootElement.getAttribute(MapperAttribute.NAMESPACE.getCode());
					if (StringUtil.isBlank(namespace)) {
						throw new BizException(FrameException.FRAME_MAPPER_NAMESPACE_NULL, mapper);
					}
					if (roots.containsKey(namespace)) {
						throw new BizException(FrameException.FRAME_MAPPER_NAMESPACE_REPEAT, mapper, namespace);
					}
					roots.put(namespace, rootElement);
					mapperNames.put(namespace, mapper);
				}
			} catch (SAXException | IOException e) {
				throw new BizException(e);
			}
		}
	}

}