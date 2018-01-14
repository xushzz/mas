package com.sirap.basic.util;

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
		long targetSize = FileUtil.parseFileSize(fileSizeWithUnit);
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
		long targetSize = FileUtil.parseFileSize(fileSizeWithUnit);
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