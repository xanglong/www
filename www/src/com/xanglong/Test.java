package com.xanglong;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.io.ImageUtil;
import com.xanglong.frame.net.ImageType;

public class Test {

	private static int count = 1;
	
	private static Set<String> md5Set = new HashSet<>();
	
	public static void main(String[] args) {
		File[] files = new File("D:/icon").listFiles();
		for (File file : files) {
			FileUtil.copyFile(file, new File("D:/icon2/" + file.getName().replace("_", "-").toLowerCase()));
		}
	}
	
	private static String md5(byte[] bytes) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(bytes);
		byte s[] = m.digest();
		String result = "";
		for (int i = 0; i < s.length; i++) {
			result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
		}
		return result;
	}
	
	private static void findImage(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				findImage(f);
			}
		} else {
			String fileName = file.getName();
			for (ImageType imageType : ImageType.values()) {
				if (fileName.endsWith("." + imageType.getCode())) {
					byte[] bytes = FileUtil.readByte(file);
					BufferedImage bufferedImage = ImageUtil.getBufferedImage(bytes);
					if (bufferedImage != null && bufferedImage.getHeight() == 16 && bufferedImage.getWidth() == 16) {
						fileName = fileName.replace("." + imageType.getCode(), "");
						FileUtil.copyFile(file, new File("D:/image/" + fileName + "__" + count + "." + imageType.getCode()));
						count++;
						break;
					}
				}
			}
		}
	}
	
}