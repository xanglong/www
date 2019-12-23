package com.xanglong.frame.io;

 import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Redis;
import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.util.CollectionUtil;
import com.xanglong.frame.util.StringUtil;
import com.xanglong.i18n.zh_cn.FrameException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

	private static JedisPool jedisPool = null;
	private static String password = null;

	/**
	 * 获取Jedis对象
	 * @return Jedis对象
	 * */
	public static Jedis getJedis() {
		if (jedisPool == null) {
			Redis redis = Sys.getConfig().getRedis();
			password = redis.getPassword();
			password = StringUtil.isBlank(password) ? null : password;
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(redis.getMaxIdle());//最大闲置数
			jedisPoolConfig.setMinIdle(redis.getMinIdle());//最小闲置数
			jedisPoolConfig.setMaxTotal(redis.getMaxTotal());//最大连接数
			jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort());
		}
		Jedis jedis = jedisPool.getResource();
		if (jedis == null) {
			throw new BizException(FrameException.FRAME_CAN_NOT_GET_REDIS);
		}
		if (password != null) {
			jedis.auth(password);
		}
		return jedis;
	}

	/** 
     * 设置 
     * @param key 键
     * @param value 值 
     * @param time 过期时间：秒 
     * @return 长整数
     */  
    public static Long expire(String key, int time) {
    	Jedis jedis = getJedis();
		Long result = jedis.expire(key.getBytes(), time);
		jedis.close();
		return result;
    }

	/**
	 * 设置
	 * @param key 键
	 * @param value 值
	 * @return 字符串
	 * */
	public static String set(String key, Object value) {
		Jedis jedis = getJedis();
		byte[] bytes = SerializeUtil.getBytes(value);
		String result = jedis.set(key.getBytes(), bytes);
		jedis.close();
		return result;
	}

	/**
	 * 获取
	 * @param key 键
	 * @return 对象
	 * */
	public static Object get(String key) {
		Jedis jedis = getJedis();
		byte[] bytes = jedis.get(key.getBytes());
		jedis.close();
		return SerializeUtil.getObject(bytes);
	}

	/**
	 * 删除
	 * @param key 键
	 * @return 长整型
	 * */
	public static Long del(String key) {
		Jedis jedis = getJedis();
		Long result = jedis.del(key);
		jedis.close();
		return result;
	}

	/**
     * 设置map中的值
     * @param mapKey map的key
     * @param key 值的key
     * @param value 值
     * */
    public static Long mapSet(String mapKey, String key, Object value) {
    	Jedis jedis = getJedis();
    	byte[] bytes = SerializeUtil.getBytes(value);
    	Long result = jedis.hset(mapKey.getBytes(), key.getBytes(), bytes);
    	jedis.close();
    	return result;
    }

    /**
     * 获取map中的值
     * @param mapKey map的key
     * @param key 值的key
     * @return 对象
     * */
    public static Object mapGet(String mapKey, String key) {
    	Jedis jedis = getJedis();
    	List<byte[]> listBytes = jedis.hmget(mapKey.getBytes(), key.getBytes());
    	jedis.close();
    	if (listBytes == null) {
    		return null;
    	}
    	return CollectionUtil.isEmpty(listBytes) ? null : SerializeUtil.getObject(listBytes.get(0));
    }
    
    /**
     * 删除map中的值
     * @param mapKey map的key
     * @param key 值的key
     * @return 对象
     * */
    public static Long mapDel(String mapKey, String key) {
    	Jedis jedis = getJedis();
    	Long result = jedis.hdel(mapKey.getBytes(), key.getBytes());
    	jedis.close();
    	return result;
    }

    /**
     * 存入一个map
     * @param map 哈希对象
     * @param mapKey 哈希键
     * @return 字符串
     * */
    public static String setMap(Map<byte[], byte[]> map, String mapKey) {
    	Jedis jedis = getJedis();
    	String result = jedis.hmset(mapKey.getBytes(), map);
    	jedis.close();
    	return result;
    }

    /**
     * 取出一个map
     * @param mapKey map的键
     * */
    public static List<Object> getMap(String mapKey) {
    	Jedis jedis = getJedis();
    	List<byte[]> listBytes = jedis.hvals(mapKey.getBytes());
    	jedis.close();
    	if (listBytes == null) {
    		return null;
    	}
    	List<Object> list = new ArrayList<>(listBytes.size());
    	for (byte[] bytes : listBytes) {
    		list.add(SerializeUtil.getObject(bytes));
    	}
    	return list;
    }

    /**
     * 取出map的所有key
     * @param mapKey map的键
     * @return map中的所有键
     * */
    public static Set<String> getMapKeys(String mapKey) {
    	Jedis jedis = getJedis();
    	Set<byte[]> keysBytes = jedis.hkeys(mapKey.getBytes());
    	jedis.close();
    	if (keysBytes == null) {
    		return null;
    	}
    	Set<String> keys = new HashSet<>(keysBytes.size());
    	for (byte[] keysByte : keysBytes) {
    		keys.add(new String(keysByte));
    	}
    	return keys;
    }
    
    /**
     * 取map的大小
     * @param mapKey map的键
     * @return map的大小
     * */
    public static Long getMapSize(String mapKey) {
    	Jedis jedis = getJedis();
    	Long size = jedis.hlen(mapKey);
    	jedis.close();
    	return size;
    }
    
    /**
     * 判断某个键是否存在
     * @param mapKey map的键
     * @param key 键
     * @return 是否存在
     * */
    public static Boolean mapExists(String mapkey, String key) {
    	Jedis jedis = getJedis();
    	Boolean exist = jedis.hexists(mapkey.getBytes(), key.getBytes());
    	jedis.close();
    	return exist;
    }

    /**
     * 添加set的值
     * @param setKey set的key
     * @param value 字符串
     * @return 长整型
     * */
    public static Long setAdd(String setKey, String value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.sadd(setKey, value);
    	jedis.close();
    	return result;
    }
    
    /**
     * 获取整个set
     * @param setKey set的键
     * @return 整个set
     * */
    public static Set<Object> getSet(String setKey) {
    	Jedis jedis = getJedis();
    	Set<byte[]> setBytes = jedis.smembers(setKey.getBytes());
    	jedis.close();
    	if (setBytes == null) {
    		return null;
    	}
    	Set<Object> set = new HashSet<>(setBytes.size());
    	for (byte[] setByte : setBytes) {
    		set.add(SerializeUtil.getObject(setByte));
    	}
    	return set;
    }
    
    /**
     * 删除set某个值
     * @param setKey set的键
     * @param value 值
     * @return 长整型
     * */
    public static Long setDel(String setKey, Object value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.srem(setKey.getBytes(), SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }
    
    /**
     * set出栈
     * @param setKey set的键
     * @return 对象
     * */
    public static Object setPop(String setKey) {
    	Jedis jedis = getJedis();
    	byte[] bytes = jedis.spop(setKey.getBytes());
    	jedis.close();
    	return SerializeUtil.getObject(bytes);
    }
    
    /**
     * 两个set的交集
     * @param setKey1 set的键1
     * @param setKey2 set的键2
     * @return set的交集
     * */
    public static Set<Object> getSetInter(String setKey1, String setKey2) {
    	Jedis jedis = getJedis();
    	Set<byte[]> setBytes = jedis.sinter(setKey1.getBytes(), setKey2.getBytes());
    	Set<Object> set = new HashSet<>(setBytes.size());
    	for (byte[] setByte : setBytes) {
    		set.add(SerializeUtil.getObject(setByte));
    	}
    	jedis.close();
    	return null;
    }
    
    /**
     * 两个set的并集
     * @param setKey1 set的键1
     * @param setKey2 set的键2
     * @return set的并集
     * */
    public static Set<Object> getSetUnion(String setKey1, String setKey2) {
    	Jedis jedis = getJedis();
    	Set<byte[]> setBytes = jedis.sunion(setKey1.getBytes(), setKey2.getBytes());
    	Set<Object> set = new HashSet<>(setBytes.size());
    	for (byte[] setByte : setBytes) {
    		set.add(SerializeUtil.getObject(setByte));
    	}
    	jedis.close();
    	return null;
    }
    
    /**
     * 两个set的差集
     * @param setKey1 set的键1
     * @param setKey2 set的键2
     * @return set的差集
     * */
    public static Set<Object> getSetDiff(String setKey1, String setKey2) {
    	Jedis jedis = getJedis();
    	Set<byte[]> setBytes = jedis.sdiff(setKey1.getBytes(), setKey2.getBytes());
    	Set<Object> set = new HashSet<>(setBytes.size());
    	for (byte[] setByte : setBytes) {
    		set.add(SerializeUtil.getObject(setByte));
    	}
    	jedis.close();
    	return null;
    }

    /**
     * 添加list的值
     * @param listKey list的键
     * @return 长整形
     * */
    public static Long listAdd(String listKey, Object value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.lpush(listKey.getBytes(), SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }

    /**
     * 获取list指定下标的值
     * @param listKey list的键
     * @param index 索引下标
     * @return 对象
     * */
    public static Object listGet(String listKey, int index) {
    	Jedis jedis = getJedis();
    	byte[] bytes = jedis.lindex(listKey.getBytes(), index);
    	jedis.close();
    	return SerializeUtil.getObject(bytes);
    }

    /**
     * 删除list指定下标的值
     * @param listKey list的键
     * @param count 
     * 	count > 0 : 从表头开始向表尾搜索，移除与VALUE相等的元素，数量为COUNT
     *	count < 0 : 从表尾开始向表头搜索，移除与VALUE相等的元素，数量为COUNT的绝对值
     *	count = 0 : 移除表中所有与 VALUE 相等的值。
     * @param index 索引下标
     * @return 长整型
     * */
    public static Long listDel(String listKey, int count, Object value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.lrem(listKey.getBytes(), count, SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }

    /**
     * 列表出栈
     * @param listKey list的键
     * @return 对象
     * */
    public static Object listPop(String listKey) {
    	Jedis jedis = getJedis();
    	byte[] bytes = jedis.lpop(listKey.getBytes());
    	jedis.close();
    	return SerializeUtil.getObject(bytes);
    }

    /**
     * 获取整个list
     * @param listKey list的键
     * @return 整个list
     * */
    public static List<Object> getList(String listKey) {
    	Jedis jedis = getJedis();
    	List<byte[]> listBytes = jedis.lrange(listKey.getBytes(), 0, -1);
    	jedis.close();
    	if (listBytes == null) {
    		return null;
    	}
    	List<Object> list = new ArrayList<>(listBytes.size());
    	for (byte[] listByte : listBytes) {
    		list.add(SerializeUtil.getObject(listByte));
    	}
    	return list;
    }

    /**
     * 修改列表中单个值
     * @param listKey list的键
     * @param index 索引下标
     * @param value 值
     * @return 字符串
     * */
    public static String listSet(String listKey, int index, Object value) {
    	Jedis jedis = getJedis();
    	String result = jedis.lset(listKey.getBytes(), index, SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }

    /**
     * 获取列表大小
     * @param listKey list的键
     * @return 列表大小
     * */
    public static Long getListSize(String listkey) {
    	Jedis jedis = getJedis();
    	Long size = jedis.llen(listkey.getBytes());
    	jedis.close();
    	return size;
    }
    
    /**
     * 添加有序set的值
     * @param setKey set的键
     * @param double 分数值，插入位置
     * @param value 值
     * @return 长整形
     * */
    public static Long sortedSetAdd(String listKey, double score, Object value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.zadd(listKey.getBytes(), score, SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }
    
    /**
     * 获取有序set的大小
     * @param setKey set的键
     * @return 有序set的大小
     * */
    public static Long getSortedSetSize(String setKey) {
    	Jedis jedis = getJedis();
    	Long size = jedis.zcard(setKey.getBytes());
    	jedis.close();
    	return size;
    }
    
    /**
     * 删除有序set的值
     * @param setKey set的键
     * @param value 值
     * @return 长整型
     * */
    public static Long sortedSetDel(String setKey, Object value) {
    	Jedis jedis = getJedis();
    	Long result = jedis.zrem(setKey.getBytes(), SerializeUtil.getBytes(value));
    	jedis.close();
    	return result;
    }
    
    /**
     * 获取整个有序set
     * @param setKey set的键
     * @return 整个有序set
     * */
    public static Set<Object> getSortedSet(String setkey) {
    	Jedis jedis = getJedis();
    	Set<byte[]> setBytes = jedis.zrange(setkey.getBytes(), 0, -1);
    	jedis.close();
    	if (setBytes == null) {
    		return null;
    	}
    	Set<Object> set = new HashSet<>(setBytes.size());
    	for (byte[] setByte : setBytes) {
    		set.add(SerializeUtil.getObject(setByte));
    	}
    	return set;
    }

}