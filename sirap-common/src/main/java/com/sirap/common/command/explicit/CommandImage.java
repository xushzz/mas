package com.sirap.common.command.explicit;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.PaymentItem;
import com.sirap.basic.thirdparty.image.qrcode.QRCodeHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PaymentUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetEmail;
import com.sirap.common.framework.command.target.TargetFolder;

public class CommandImage extends CommandBase {
	private static final String KEY_CAPTURE_CURRENT_WINDOW = "c";
	private static final String KEY_CAPTURE_ENTIRE_SCREEN = "s";
	private static final String KEY_LAST_MODIFIED = "lm";
	private static final String KEY_CAPTURE_SOUND_ONOFF_SWITCH = "sx";
	private static final String KEY_SWAP = "swap";
	private static final String KEY_QRCODE_ENCODE = "qrc";
	private static final String KEY_QRCODE_DECODE = "qrx";	
	private static final String KEY_KICK_PRINT = "kp";
	private static final String KEY_FIX_IMAGE = "fix";
	private static final String KEY_DONATION_CHINESE = XCodeUtil.urlDecodeUTF8("%E6%89%93%E8%B5%8F");
	private static final String KEY_DONATION = "do,don,donation,dashang," + KEY_DONATION_CHINESE;
	
	public boolean handle() {

		params = parseParams(StrUtil.occupy("({0}|{1})(|\\d{1,2})(|\\s.*?)", KEY_CAPTURE_ENTIRE_SCREEN, KEY_CAPTURE_CURRENT_WINDOW));
		if(params != null) {
			String type = params[0];
			String strDelay = params[1];
			String nameInfo = params[2];
			int delay = MathUtil.toInteger(strDelay, 0);
			if(delay == 0) {
				String defDelayStr = SimpleKonfig.g().getUserValueOf("capture.delay");
				Integer defDelay = MathUtil.toInteger(defDelayStr);
				if(defDelay != null && defDelay > 0) {
					int maxDelay = 99;
					if(defDelay > maxDelay) {
						delay = maxDelay;
					} else {
						delay = defDelay;
					}
				}
			}
			
			String[] filenameAndFormat = generateImageFilenamePrefixAndFormat(nameInfo, Konstants.IMG_BMP);
			String filename = filenameAndFormat[0];
			String format = filenameAndFormat[1];
			
			String filePath = ImageUtil.takePhoto(filename, getCaptureSound(), format, delay, KEY_CAPTURE_ENTIRE_SCREEN.equals(type));
			if(filePath != null) {
				String info = "";
				if(OptionUtil.readBooleanPRI(options, "d", true)) {
					info += " " + FileUtil.formatSize(filePath);
					info += " " + ImageUtil.readImageWidthHeight(filePath, "*");
				}

				C.pl(" => " + filePath + info);
				tryToOpenGeneratedImage(filePath);
			}
			
			if(target instanceof TargetEmail) {
				export(FileUtil.getIfNormalFile(filePath));
			}
			
			return true;
		}
		
		params = parseParams(KEY_LAST_MODIFIED + "(|\\s+(.*?))");
		if(params != null) {
			String llPath = g().getUserValueOf("ll.folder");
			String folderPath = llPath != null ? llPath : screenShotPath();
			String key = params[1];
			String filePath = lastModified(folderPath, key);
			if(filePath != null) {
				FileOpener.open(filePath);
			}
		}
		
		params = parseParams(StrUtil.occupy("({0}|{1})(|\\d{1,2})\\+(\\d{1,4})(|\\s.*?)", KEY_CAPTURE_ENTIRE_SCREEN, KEY_CAPTURE_CURRENT_WINDOW));
		if(params != null) {
			String type = params[0];
			String strDelay = params[1];
			int count = Integer.valueOf(params[2]);
			if(count > 0) {
				String nameInfo = params[3];
				int delay = MathUtil.toInteger(strDelay, 0);
				if(delay == 0) {
					String defDelayStr = SimpleKonfig.g().getUserValueOf("capture.delay");
					Integer defDelay = MathUtil.toInteger(defDelayStr);
					if(defDelay != null && defDelay > 0) {
						int maxDelay = 99;
						if(defDelay > maxDelay) {
							delay = maxDelay;
						} else {
							delay = defDelay;
						}
					}
				}
				
				String[] filenameAndFormat = generateImageFilenamePrefixAndFormat(nameInfo, Konstants.IMG_BMP);
				String filename = filenameAndFormat[0];
				String format = filenameAndFormat[1];

				String filePath = ImageUtil.takeConsecutivePhotos(filename, getCaptureSound(), format, delay, count, KEY_CAPTURE_ENTIRE_SCREEN.equals(type));
				if(filePath != null) {
					String info = "";
					if(OptionUtil.readBooleanPRI(options, "d", false)) {
						info += " " + FileUtil.formatSize(filePath);
						info += " " + ImageUtil.readImageWidthHeight(filePath, "x");
					}
					C.pl("detail:" + info);
					tryToOpenGeneratedImage(filePath);
				}
				
				if(target instanceof TargetEmail) {
					export(FileUtil.getIfNormalFile(filePath));
				}
				
				return true;
			}
		}
		
		if(is(KEY_CAPTURE_SOUND_ONOFF_SWITCH)) {
			boolean flag = !g().isCaptureSoundOn();
			g().setCaptureSoundOn(flag);
			String value = flag ? "on" : "off";
			C.pl2("Capture sound turned " + value + ".");
			
			return true;
		}
		
		if(is(KEY_SWAP)) {
			String[] codeAndImage = ImageUtil.generateCaptcha(4, screenShotPath());
			String code = null;
			String filePath = null;
			if(codeAndImage != null) {
				code = codeAndImage[0];
				filePath = codeAndImage[1];
			}
			
			if(filePath != null) {
				String info = "";
				if(OptionUtil.readBooleanPRI(options, "d", false)) {
					info += " " + FileUtil.formatSize(filePath);
					info += " " + ImageUtil.readImageWidthHeight(filePath, "x");
				}
				C.pl(code + ", " + filePath + info);
				tryToOpenGeneratedImage(filePath);
			}
			
			if(target instanceof TargetConsole) {
				return true;
			}
			
			export(FileUtil.getIfNormalFile(filePath));
			
			return true;
		}
		
		params = parseParams(KEY_QRCODE_ENCODE + "\\s(.*?)(|///(.*?))");
		if(params != null) {
			String nameInfo = params[2];
			if(nameInfo == null) {
				nameInfo = "";
			}

			String content = params[0];
			File file = parseFile(content);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					content = IOUtil.readStringWithLineSeparator(filePath);
					C.pl("Encode text file.");
					if(EmptyUtil.isNullOrEmpty(nameInfo)) {
						nameInfo = FileUtil.extractFilenameWithoutExtension(filePath);
					}
				}
			}

			String[] filenameAndFormat = generateQRCodeImageFilenameAndFormat(nameInfo, content, "png");
			String filepath = filenameAndFormat[0];
			String format = filenameAndFormat[1];
			
			String filePath = QRCodeHelper.createImage(content, filepath, format, 200, 200);
			if (filePath != null) {
				String info = "";
				if(OptionUtil.readBooleanPRI(options, "d", false)) {
					info += " " + FileUtil.formatSize(filePath);
					info += " " + ImageUtil.readImageWidthHeight(filePath, "x");
				}
				C.pl(filePath + info);
				tryToOpenGeneratedImage(filePath);
			}
			
			return true;
		}

		String param = parseParam(KEY_QRCODE_DECODE + "\\s+(.+?)");
		if(param != null) {
			File file = parseFile(param);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				String content = QRCodeHelper.decodeImage(filePath);
				export(content);
				
				return true;
			}
		}
		
		solo = parseParam(KEY_KICK_PRINT + "(|\\s+.+)");
		if(solo != null) {
			if(!solo.isEmpty()) {
				File image = parseFile(solo);
				if(image != null) {
					FileOpener.open(image.getAbsolutePath(), "+p");
					return true;
				}
			}

			String location = getExportLocation();
			String temp = g().getUserValueOf("ko.location");
			if(!EmptyUtil.isNullOrEmpty(temp)) {
				File folder = FileUtil.getIfNormalFolder(temp);
				if(folder != null) {
					location = folder.getAbsolutePath();
				} else {
					XXXUtil.info("Non-existing location: {0}, use default location: {1}", temp, location);
				}
			}
			
			String text = solo.isEmpty() ? "https://en.wikipedia.org/wiki/Live_Free_or_Die" : solo;
			String filename = DateUtil.timestamp() + "_KP.jpg";
			String filepath = StrUtil.useSeparator(location, filename);
			ImageUtil.createImage(filepath, text, 1200, 400);
			FileOpener.open(filepath, "+p");
			
			return true;
		}
		
		solo = parseParam(KEY_FIX_IMAGE + "\\s+(.+)");
		if(solo != null) {
			String fileSizeWithUnit = OptionUtil.readString(options, "size");
			XXXUtil.nullCheck(fileSizeWithUnit, ":You must specify the file size you want to compress to.");
			if(FileOpener.isImageFile(solo)) {
				fixSingleImage(solo, fileSizeWithUnit);
				return true;
			}
			
			File folder = parseFolder(solo);
			if(folder != null) {
				//handle all those image files in this folder
				folder.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						String filePath = StrUtil.useSeparator(dir.getAbsolutePath(), name);
						if(FileOpener.isImageFile(filePath)) {
							fixSingleImage(filePath, fileSizeWithUnit);
						}
						return false;
					}
				});
				
				return true;
			}
		}
		
		if(isIn(KEY_DONATION)) {
			boolean isAlipay = OptionUtil.readBooleanPRI(options, "ali", false);
			String type = isAlipay ? PaymentItem.TYPE_ALIPAY : PaymentItem.TYPE_WEIXIN;
			PaymentItem payinfo = PaymentUtil.getActive(type);
			if(payinfo == null) {
				C.pl2("No active & valid payment info from " + PaymentUtil.URL_DONATION);
			} else {
				String[] filenameAndFormat = generateQRCodeImageFilenameAndFormat(type + payinfo.getRemark(), RandomUtil.letters(3), "png");
				String filepath = filenameAndFormat[0];
				String format = filenameAndFormat[1];
				
				String filePath = QRCodeHelper.createImage(payinfo.getUrl(), filepath, format, 400, 400);
				if (filePath != null) {
					String info = "";
					if(OptionUtil.readBooleanPRI(options, "d", false)) {
						info += " " + FileUtil.formatSize(filePath);
						info += " " + ImageUtil.readImageWidthHeight(filePath, "x");
					}
					C.pl(filePath + info);
					tryToOpenGeneratedImage(filePath);
				}
				XXXUtil.info("Please use {0} to scan the qrcode to make donation {1}.", type, payinfo.getRemark());
				C.pl2("Your donation will make this project greater. !THANK YOU!");
			}
		}
		
		return false;
	}
	
	private void fixSingleImage(String filePath, String fileSizeWithUnit) {
		String whereToSave = null;
		boolean toSameFolder = OptionUtil.readBooleanPRI(options, "same", false);
		if(!toSameFolder) {
			if(target instanceof TargetFolder) {
				whereToSave = ((TargetFolder)target).getPath();
			} else {
				whereToSave = getExportLocation();
			}
		}
		boolean withTimestamp = g().isExportWithTimestampEnabled(options);
		String finalFilename = null;
		//handle single image file.
		if(toSameFolder) {
			finalFilename = ImageUtil.compressImageToSameFolder(filePath, fileSizeWithUnit, withTimestamp);
		} else {
			finalFilename = ImageUtil.compressImageToTargetFolder(filePath, whereToSave, fileSizeWithUnit, withTimestamp);
		}
		
		if(finalFilename != null) {
			String info = "";
			if(OptionUtil.readBooleanPRI(options, "d", true)) {
				info += " " + FileUtil.formatSize(finalFilename);
				info += " " + ImageUtil.readImageWidthHeight(finalFilename, "*");
			}

			C.pl("Saved => " + finalFilename + info);
			tryToOpenGeneratedImage(finalFilename);
			
			if(target instanceof TargetEmail) {
				export(FileUtil.getIfNormalFile(finalFilename));
			}
		}
	}
	
	private String[] generateImageFilenamePrefixAndFormat(String nameInfo, String defFormat) {

		String suffix = "";
		String format = "";
		if(EmptyUtil.isNullOrEmpty(nameInfo)) {
			format = getConfigedImageFormat(defFormat);
		} else {
			String[] suffixAndFormt = parseImageFormat(nameInfo);
			if(suffixAndFormt != null) {
				suffix = suffixAndFormt[0];
				format = suffixAndFormt[1];
			} else {
				suffix = nameInfo;
				format = getConfigedImageFormat(defFormat);
			}
		}
		
		if(!EmptyUtil.isNullOrEmpty(suffix)) {
			suffix = "_" + suffix;
		}
		
		String dir = getImageLocation();
		
		String filenamePrefix = dir + "{0}" + FileUtil.generateLegalFileName(suffix);
		
		return new String[] {filenamePrefix, format};
	}
	
	private String getCaptureSound() {
		if(!g().isCaptureSoundOn()) {
			return null;
		}
		
		String key = "capture.sound";
		String sound = g().getUserValueOf(key);
		if(EmptyUtil.isNullOrEmpty(sound)) {
			C.pl("[silent] can't find sound with key " + key);
			return null;
		}
		
		File file = parseFile(sound);
		if(file == null) {
			C.pl("[silent] can't find sound at " + sound);
			return null;
		}
		return file.getAbsolutePath();
	}
	
	public String lastModified(String folderPath, final String criteria) {
		TreeMap<String, String> mapDateStr = new TreeMap<String, String>();
		TreeMap<Long, MexObject> mapLastModified = new TreeMap<Long, MexObject>();
		File folder = FileUtil.getIfNormalFolder(folderPath);
		if(folder == null) {
			String msg = "Invalid path [" + folderPath + "].";
			C.pl2(msg);
			return null;
		}
		
		List<MexFile> list = new ArrayList<MexFile>();
		folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				if(!file.isFile()) {
					return false;
				}
				
				list.add(new MexFile(file));
				
				mapLastModified.put(file.lastModified(), new MexObject(file.getAbsolutePath()));
								
				return true;
			}
		});

		String filePath = null;
		if(criteria == null) {
			String regex = "^\\d{8}_\\d{6}";
			Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			
			for(MexFile file : list) {
				String fileName = file.getName();
				String fileAbsPath = file.getPath();
				Matcher m = ptn.matcher(fileName);
				if(m.find()) {
					String dateStr = m.group(0);
					mapDateStr.put(dateStr, fileAbsPath);
				}
			}
			
			Map.Entry<String, String> entry = mapDateStr.lastEntry();
			if(entry != null) {
				filePath = entry.getValue();
				C.pl(filePath);
			} else {
				Map.Entry<Long,  MexObject> entry2 = mapLastModified.lastEntry();
				if(entry2 != null) {
					filePath = entry2.getValue().getString();
					C.pl(filePath);
				} else {
					exportEmptyMsg();
				}
			}
		} else {
			List<MexObject> filePaths = CollUtil.filter(new ArrayList<MexObject>(mapLastModified.values()), criteria, isCaseSensitive(), isStayCriteria());
			
			int size = filePaths.size();
			if(size == 1) {
				filePath = filePaths.get(0).getString();
				C.pl(filePath);
			} else {
				if(!filePaths.isEmpty()) {
					filePath = filePaths.get(size - 1).getString();
				} 
				export(filePaths);
			}
		}
		
		return filePath;
	}
}
