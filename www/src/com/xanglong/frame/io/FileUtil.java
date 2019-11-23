package com.xanglong.frame.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.xanglong.frame.log.Logger;

/**文件处理工具类*/
public class FileUtil {
	
	/**
	 * 读取文件内容为字符串
	 * @param file 文件对象
	 * @return 文本内容
	 * */
	public static String read(File file) {
		byte[] bytes = readByte(file);
        return bytes == null ? "" : new String(bytes, Charset.forName("UTF-8"));
	}
	
	/**
	 * 读二进制文件
	 * @param file 文件对象
	 * @return 二进制数据
	 * */
	public static byte[] readByte(File file) {
		if (!file.exists()) {
			return null;
		}
		byte[] bytes = new byte[0];
		try (FileInputStream fileInputStream = new FileInputStream(file);
			FileChannel fileChannel = fileInputStream.getChannel();
		) {
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
			while (fileChannel.read(byteBuffer) > 0) { }
			bytes = byteBuffer.array();
		} catch (FileNotFoundException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return bytes;
	}

}