package com.xanglong.frame.io;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xanglong.frame.exception.BizException;
import com.xanglong.frame.net.ImageType;

import net.coobird.thumbnailator.Thumbnails;

public class ImageUtil {

	/**
	 * 读取为缓冲图片对象
	 * @param bytes 二进制数据
	 * @return 图片缓冲对象
	 * */
	public static BufferedImage getBufferedImage(byte[] bytes) {
		BufferedImage bufferedImage = null;
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);) {
			bufferedImage = ImageIO.read(bais);
		} catch (IOException e) {
			throw new BizException(e);
		}
		return bufferedImage; 
	}
	
	/**
	 * 压缩图片：带图片格式
	 * @param bytes 图片二进制数据
	 * @param height 图片高度
	 * @param width 图片宽度
	 * @param imageType 图片类型
	 * @return 二进制数据
	 * */
	public static byte[] getImageBySize(byte[] bytes, int height, int width, ImageType imageType) {
		byte[] minBytes = null;
		try {
			BufferedImage bufferedImage = ImageUtil.getBufferedImage(bytes);
			try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
				bufferedImage = Thumbnails.of(bufferedImage).size(height, width).asBufferedImage();
				ImageIO.write(bufferedImage, imageType.getCode(), byteArrayOutputStream);
				minBytes = byteArrayOutputStream.toByteArray();
            }
		} catch (IOException e) {
			throw new BizException(e);
		}
		return minBytes;
	}
	
	/**
	 * 获取颜色的灰度值
	 * @param rgb RGB颜色
	 * @return 灰度值
	 * */
	private static int getGray(int rgb){
		Color color = new Color(rgb);
		return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
	}
	
	/**
	 * 自己加周围8个灰度值再除以9，算出其相对灰度值
	 * @param gray 灰度值
	 * @param x 宽度坐标
	 * @param y 高度坐标
	 * @param w 宽度
	 * @param h 高度
	 * @return 相对灰度值
	 */
	private static int  getAverageColor(int[][] gray, int x, int y, int w, int h) {
        int rs = gray[x][y]
        	+ (x == 0 ? 255 : gray[x - 1][y])
        	+ (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
        	+ (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
        	+ (y == 0 ? 255 : gray[x][y - 1])
        	+ (y == h - 1 ? 255 : gray[x][y + 1])
        	+ (x == w - 1 ? 255 : gray[x + 1][ y])
        	+ (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
        	+ (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }
	
	/**
	 * 图片灰度算法
	 * @param bufferedImage 图片对象
	 * @param imageType 图片类型
	 * @param averageGray 灰度均值，平均128
	 * @return 图片二进制数据
	 * */
	public static byte[] getGrayBytes(BufferedImage bufferedImage, ImageType imageType, int averageGray) {
		int h = bufferedImage.getHeight();
		int w = bufferedImage.getWidth();
		int[][] gray = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				gray[x][y] = getGray(bufferedImage.getRGB(x, y));
			}
		}
		BufferedImage emptyBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (getAverageColor(gray, x, y, w, h) > averageGray) {
					int max=new Color(255, 255, 255).getRGB();
					emptyBufferedImage.setRGB(x, y, max);
				} else {
					int min = new Color(0, 0, 0).getRGB();
					emptyBufferedImage.setRGB(x, y, min);
				}
			}
		}
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			ImageIO.write(emptyBufferedImage, imageType.getCode(), baos);
			baos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
