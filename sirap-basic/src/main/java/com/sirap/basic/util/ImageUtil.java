package com.sirap.basic.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.thirdparty.image.ImageFixer;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.CaptchaGenerator;
import com.sirap.basic.tool.ScreenCaptor;

public class ImageUtil {
	
	public static void countDown(int seconds) {
		for(int i = 0; i < seconds; i++) {
			int left = seconds - i;
			String display = left + (left > 1 ? " " : "");
			C.pr(display);
			
			ThreadUtil.sleepInSeconds(1);
		}
	}
	
	public static String takeConsecutivePhotos(String fileNamePrefix, String soundSource, String format, int delay, int count, boolean isEntireScreen) {
		countDown(delay);
		if(delay > 0) {
			C.pl();
		}
		String lastOne = null;
		String temp = fileNamePrefix + "_{1}.{2}";
		for(int i = 0; i < count; i++) {
			if(i != 0) {
				ThreadUtil.sleepInSeconds(1);
			}
			int index = i + 1;
			String indexStr = StrUtil.padLeft(index + "", (count + "").length(), "0");
			String imagePath = StrUtil.occupy(temp, DateUtil.timestamp(), indexStr, format);
			ScreenCaptor fang = new ScreenCaptor();
			RenderedImage image = isEntireScreen ? fang.captureEntireScreen() : fang.captureCurrentWindow();
	    	if(EmptyUtil.isNullOrEmptyOrBlank(soundSource)) {
	        	IOUtil.playSound(soundSource);
	    	}
			String filePath = ImageUtil.saveImage(image, imagePath);
			C.pl(index + "/" + count +" => " + filePath);
			if(i == count - 1) {
				lastOne = filePath;
			}
		}
		
		return lastOne;
	}

	public static String takePhoto(String fileNamePrefix, String soundSource, String format, int delay, boolean isEntireScreen) {
		countDown(delay);
		String imagePath = StrUtil.occupy(fileNamePrefix, DateUtil.timestamp()) + "." + format;
		ScreenCaptor fang = new ScreenCaptor();
		RenderedImage image = isEntireScreen ? fang.captureEntireScreen() : fang.captureCurrentWindow();
    	if(!EmptyUtil.isNullOrEmptyOrBlank(soundSource)) {
        	IOUtil.playSound(soundSource);
    	}
    	
    	String filePath = ImageUtil.saveImage(image, imagePath);
		
		return filePath;
	}

	public static String[] generateCaptcha(int numberOfChars, String storage) {
		String text = RandomUtil.letters(4);
    	String filePath = storage + DateUtil.timestamp() + "_captcha.jpeg";
		CaptchaGenerator james = new CaptchaGenerator(text);
		boolean flag = james.writeImageTo(filePath);
		if(flag) {
			return new String[]{text, filePath};
		} else {
			return null;
		}
	}
	
	/***
	 * 	What feeble means? My feeble attempt on Graphics.
	 * 	Fetching... http://www.dictionary.com/browse/feeble
		adjective, feebler, feeblest.
		1. physically weak, as from age or sickness; frail.
		2. weak intellectually or morally: a feeble mind.
		3. lacking in volume, loudness, brightness, distinctness, etc.: a feeble voice; feeble light.
		4. lacking in force, strength, or effectiveness: feeble resistance; feeble arguments.
	 * @param filepath
	 * @param text
	 * @param width
	 * @param height
	 */
	public static void createImage(String filepath, String text, int width, int height) {
		XXXUtil.checkRange(width, 80, 1800);
        int imageWidth = width;
        int imageHeight = height;
    
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);    
        Graphics mike = image.getGraphics();    
        mike.setColor(Color.WHITE);    
        mike.fillRect(0, 0, imageWidth, imageHeight);    
        mike.setColor(Color.RED);    
        mike.setFont(new Font("Times New Roman", Font.BOLD, 40));    

        int xPoint = 50;
        int yPoint = 50;
        mike.drawString(text, xPoint, yPoint);

        yPoint += 100;
        int len = RandomUtil.number(10, 20);
        text = StrUtil.occupy("RandomUtil.alphanumeric({0})={1}", len, RandomUtil.alphanumeric(len));
        mike.setColor(Color.GREEN);
        mike.drawString(text, xPoint, yPoint);
        
        yPoint += 100;
        text = StrUtil.occupy("My feeble attempt on Graphics at {0}", DateUtil.displayNow(DateUtil.HOUR_Min_Sec_AM_DATE));
        mike.setColor(Color.BLUE);
        mike.drawString(text, xPoint, yPoint);
        
        saveImage(image, filepath);
	}
	
	public static String saveImage(RenderedImage image, String filepath) {
		File file = new File(filepath);
		String format = StrUtil.findFirstMatchedItem("\\.([a-z]+$)", filepath);
		XXXUtil.nullCheck(format, "The filename indicates no format: " + filepath);
		
		try {
			ImageIO.write(image, format, file);
			return file.getAbsolutePath();
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}

	public static String getRealFormat(String filepath) {
		File file = new File(filepath);

		try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				String msg = "No readers found for image [" + filepath + "]";
				throw new MexException(msg);
			}

			ImageReader reader = iter.next();
			String value = reader.getFormatName();

			return value;
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}

	public static String readImageWidthHeight(String filepath, String connector) {
		File file = new File(filepath);

		try {
			BufferedImage image = ImageIO.read(file);
			if(image == null) {
				String msg = "Not valid image [" + filepath + "]";
				throw new MexException(msg);
			}
			String value = image.getWidth() + connector + image.getHeight();

			return value;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	public static int[] widthAndHeight(String filepath) {
		try {
			BufferedImage image = ImageIO.read(new File(filepath));
			if(image == null) {
				String msg = "Not valid image [" + filepath + "]";
				throw new MexException(msg);
			}

			return new int[]{image.getWidth(), image.getHeight()};
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}

	//D:/Github/Wiki/mas.wiki/maps/china.jpg
	//D:/Github/Wiki/mas.wiki/maps/20171214_001010_china_500k.jpg
	//D:/Github/Wiki/mas.wiki/maps/china_500k.jpg
	public static String compressImageToSameFolder(String sourcePath, String fileSizeWithUnit, boolean withTimestamp) {
		String regex = "(.+[/\\\\])([^\\.\\\\/]+\\.[a-z]+$)";
		String[] folderAndName = StrUtil.parseParams(regex, sourcePath);
		String folder = folderAndName[0];
		String name = folderAndName[1];
		String temp = name;
		if(withTimestamp) {
			temp = DateUtil.timestamp() + "_" + name;
		}
		String newName = temp.replace(".", "_" + fileSizeWithUnit + ".");
		String targetPath = folder + newName;
		long targetSize = FileUtil.parseSize(fileSizeWithUnit);
		return compressImage(sourcePath, targetPath, targetSize);
	}
	
	public static String compressImageToTargetFolder(String sourcePath, String targetFolder, String fileSizeWithUnit, boolean withTimestamp) {
		if(FileUtil.getIfNormalFolder(targetFolder) == null) {
			XXXUtil.alert("Not a valid folder: " + targetFolder);
		}
		String regex = "(.+[/\\\\])([^\\.\\\\/]+\\.[a-z]+$)";
		String[] folderAndName = StrUtil.parseParams(regex, sourcePath);
		String name = folderAndName[1];
		String temp = name;
		if(withTimestamp) {
			temp = DateUtil.timestamp() + "_" + name;
		}
		String newName = temp.replace(".", "_" + fileSizeWithUnit + ".");
		String targetPath = StrUtil.useSeparator(targetFolder, newName);
		long targetSize = FileUtil.parseSize(fileSizeWithUnit);
		return compressImage(sourcePath, targetPath, targetSize);
	}
	
	public static String compressImage(String source, String target, long targetSize) {
		File nick = new File(source);  
        if (nick.length() <= targetSize) {
        	String msg = "Target size {0} is larger than file size {1}, ignored {2}";
        	XXXUtil.info(msg, targetSize, nick.length(), source);
            return source;
        }
        
		ImageFixer jack = new ImageFixer();
		jack.commpress(source, target, targetSize);
		return target;
	}	
}