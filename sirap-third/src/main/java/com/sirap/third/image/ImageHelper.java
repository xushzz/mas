package com.sirap.third.image;

import java.io.File;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class ImageHelper {


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
