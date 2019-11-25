package com.xanglong.frame.io;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

import javax.swing.ImageIcon;

import com.xanglong.frame.exception.BizException;

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
			throw new BizException(e);
		} catch (IOException e) {
			throw new BizException(e);
		}
		return bytes;
	}
	
	/**
	 * 写文本到文件
	 * @param file 文件对象
	 * @param text 文本
	 * @param append 是否追加
	 * */
	private static void write(File file, String text, boolean append) {
		String filePath = file.getPath();
		File folder = new File(filePath.substring(0, filePath.lastIndexOf("\\")));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			FileChannel fileChannel = randomAccessFile.getChannel();
			FileLock fileLock = fileChannel.tryLock();
		){
			if (append) {//追加写入
				randomAccessFile.seek(randomAccessFile.length());
			} else {//覆盖写入
				randomAccessFile.setLength(0);
			}
			fileChannel.write(ByteBuffer.wrap(text.getBytes()));
			fileLock.release();
		} catch (FileNotFoundException e) {
			throw new BizException(e);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 写文本到文件：覆盖
	 * @param file 文件对象
	 * @param text 文本
	 * */
	public static void writeCover(File file, String text) {
		write(file, text, false);
	}
	
	/**
	 * 写文本到文件：追加
	 * @param file 文件对象
	 * @param text 文本
	 * */
	public static void writeAppend(File file, String text) {
		write(file, text, true);
	}
	
	/**
	 * 读取为缓冲图片对象
	 * @param bytes 二进制数据
	 * @return 图片缓冲对象
	 * */
	public static BufferedImage getBufferedImage(byte[] bytes) {
		Image image = Toolkit.getDefaultToolkit().createImage(bytes);
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		image = new ImageIcon(image).getImage();
		BufferedImage bufferedImage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice graphicsDevice = ge.getDefaultScreenDevice();
			GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
			bufferedImage = graphicsConfiguration.createCompatibleImage(image.getWidth(null),
			image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			throw new BizException(e);
		}
		if (bufferedImage == null) {
			bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		}
		Graphics graphics = bufferedImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return bufferedImage;
	}

}