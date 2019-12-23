package com.xanglong.frame.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.xanglong.frame.exception.BizException;

public class SerializeUtil {

	/**
	 * 把对象序列化成二进制
	 * @param object 对象
	 * @return 二进制数据
	 * */
	public static byte[] getBytes(Object object) {
		byte[] bytes = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
		){
			oos.writeObject(object);
			bytes = baos.toByteArray();
		} catch (IOException e) {
			throw new BizException(e);
		}
		return bytes;
	}

	/**
	 * 把二进制发序列化为对象
	 *  @param bytes 二进制数据
	 *  @return 对象
	 * */
	public static Object getObject(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		Object object = null;
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
		){
			object = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw new BizException(e);
		}
		return object;
	}
	
}