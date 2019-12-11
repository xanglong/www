package com.xanglong.frame.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.xanglong.frame.exception.BizException;
import com.xanglong.i18n.zh_cn.FrameException;

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
	 * 写二进制文件
	 * @param destFile 目标文件
	 * @param bytes 二进制数据
	 * */
	public static void writeByte(File destFile, byte[] bytes) {
		if (!destFile.exists()) {
			createFile(destFile);
		}
		try (FileOutputStream fileOutputStream = new FileOutputStream(destFile);
			DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
		){
		    dataOutputStream.write(bytes);
		    dataOutputStream.flush();
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	/**
	 * 创建文件
	 * @param file 文件对象
	 * */
	public static void createFile(File file) {
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
	    	    parentFile.mkdirs();
	    	}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new BizException(e);
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param file 文件对象
	 * */
	public static void deleteFile(File file) {
		if (!file.delete()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						if (f.isDirectory()) {
							deleteFile(f);
						}
			        }
				}
            } else {
            	file.delete();
            }
		}
	}
	
	/**
	 * 复制文件
	 * @param srcFile 原始文件
	 * @param destFile 目标文件
	 * */
	public static void copyFile(File srcFile, File destFile) {
        if (destFile.isDirectory()) {
        	throw new BizException(FrameException.FRAME_COPY_FILE_EXIST, destFile.getName());
        }
        if (!destFile.exists()) {
        	createFile(destFile);
        }
        try (	FileInputStream fis = new FileInputStream(srcFile);
        		FileOutputStream fos = new FileOutputStream(destFile);
        		FileChannel input = fis.getChannel();
        		FileChannel output = fos.getChannel();
        ) {
            long size = input.size(), pos = 0, count = 0;
            while (pos < size) {//默认30M大小
                count = size - pos > 31457280 ? 31457280 : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } catch (IOException e) {
        	throw new BizException(e);
		}
        if (srcFile.length() != destFile.length()) {
        	throw new BizException(FrameException.FRAME_COPY_FILE_FAIL, srcFile.getName(), destFile.getName());
        }
    }
	
	/**
	 * 移动文件
	 * @param srcFile 原始文件
	 * @param destFile 目标文件
	 * */
	public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile);
            deleteFile(srcFile);
        }
    }
	
	/**
	 * 列举文件夹下的文件
	 * @param directory 文件夹
	 * @param extensions 相对目录表达式
	 * @param recursive 是否递归列举
	 * */
	public static List<File> listFiles(File directory, String[] extensions, boolean recursive) {
		List<File> files = new ArrayList<>();
		if (!directory.isDirectory()) {
            return files;
        }
		if (extensions != null ) {
			String[] suffixes = new String[extensions.length];
	        for (int i = 0; i < extensions.length; i++) {
	        	String extension = extensions[i];
	        	if (!extension.startsWith(".")) {
	        		suffixes[i] = "." + extensions[i];
	        	}
	        }
		}
        innerListFiles(files, directory, extensions, recursive);
		return files;
	}

	/**
	 * 递归遍历文件
	 * @param files 文件列表
	 * @param directory 文件夹
	 * @param extensions 相对目录表达式
	 * @param recursive 是否递归列举
	 * */
	private static void innerListFiles(List<File> files, File directory, String[] extensions, boolean recursive) {
        File[] found = directory.listFiles();
        if (found != null) {
            for (int i = 0; i < found.length; i++) {
            	File file = found[i];
                if (file.isDirectory() && recursive) {
                    innerListFiles(files, found[i], extensions, recursive);
                } else {
                	if (extensions != null) {
                		for (String extension : extensions ) {
                    		if (file.getName().endsWith(extension)) {
                    			files.add(file);
                    			break;
                        	}
                    	}
                	} else {
                		files.add(file);
                	}
                }
            }
        }
    }
	
	/**
	 * 获取16进制字符串
	 * @param bytes 二进制数据
	 * @return 16进制字符串
	 * */
	public static String getHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();  
		String stmp = "";  
        for (int i = 0; i < bytes.length; i++) {  
            stmp = (Integer.toHexString(bytes[i] & 0XFF));  
            if (stmp.length() == 1) {
            	hexString.append("0").append(stmp);  
            } else {
            	hexString.append(stmp);  
            }
        }
        return hexString.toString().toUpperCase();
	}
	
}