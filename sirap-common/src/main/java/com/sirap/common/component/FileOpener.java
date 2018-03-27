package com.sirap.common.component;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.MexedList;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.common.framework.SimpleKonfig;

public class FileOpener {
	
	public static final String KEY_IMAGE = "format.iamge";
	public static final String KEY_AUDIO = "format.audio";
	public static final String KEY_VIDEO = "format.video";
	public static final String KEY_TEXT = "format.text";
	public static final String KEY_EXECUTABLE = "format.exe";
	public static final String KEY_OTHERS = "format.others";
	
	public static boolean open(String filePath) {
		return open(filePath, null);
	}
	
	public static boolean open(String filePath, String options) {
		filePath = filePath.replace('/', '\\');
		
		if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_AUDIO, KEY_AUDIO)) {
			C.pl2("Play audio");
			playThing(filePath, "audio.player");
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_VIDEO, KEY_VIDEO)) {
			C.pl2("Play video");
			playThing(filePath, "video.player");
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_EXECUTABLE, KEY_EXECUTABLE)) {
			C.pl2("Run application");
			PanaceaBox.execute(filePath);
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_IMAGE, KEY_IMAGE)) {
			if(OptionUtil.readBooleanPRI(options, "p", false)) {
				C.pl2("Open by mspaint: " + filePath);
				playThingByAppName(filePath, "mspaint");
			} else {
				C.pl2("View photo");
				playThing(filePath, "image.viewer");
			}
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_PDF)) {
			C.pl2("View pdf");
			playThing(filePath, "pdf.viewer");
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_WORD)) {
			C.pl2("View word document");
			playThing(filePath, "word.viewer");
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
			C.pl2("View excel document");
			playThing(filePath, "excel.viewer");
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_HTML)) {
			C.pl2("View html document");
			playThing(filePath, "page.viewer");
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_TEXT, KEY_TEXT)) {
			if(OptionUtil.readBooleanPRI(options, "n", false)) {
				C.pl2("Open by notepad: " + filePath);
				playThingByAppName(filePath, "notepad");
			} else {
				C.pl2("View text file.");
				PanaceaBox.openFile(filePath);
			}
			
			return true;
		} else {
			PanaceaBox.openFile(filePath);
			C.pl2("Might not be able to deal with [" + filePath + "].");
			return false;
		}
	}
	
	public static boolean isPossibleNormalFile(String url) {
		int idxOfAsk = url.indexOf('?');
		String filePath = idxOfAsk > 0 ? url.substring(0, idxOfAsk) : url;
		if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_AUDIO, KEY_AUDIO)) {
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_VIDEO, KEY_VIDEO)) {
			return true;
		} else if(isImageFile(filePath)) {
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_EXECUTABLE, KEY_EXECUTABLE)) {
			return true;
		} else if(isTextFile(filePath)) {
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_PDF)) {
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_WORD)) {
			return true;
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
			return true;
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_OTHERS, KEY_OTHERS)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isImageFile(String filePath) {
		boolean flag = isAcceptableFormat(filePath, FileUtil.SUFFIXES_IMAGE, KEY_IMAGE);
		
		return flag;
	}

	public static boolean isZipFile(String fileName) {
		boolean flag = FileUtil.isAnyTypeOf(fileName, FileUtil.SUFFIXES_ZIP);
		
		return flag;
	}
	
	public static boolean isTextFile(String fileName) {
		boolean flag = isAcceptableFormat(fileName, FileUtil.SUFFIXES_TEXT, KEY_TEXT);
		if(flag) {
			return true;
		}
		
		flag = FileUtil.isAnyTypeOf(fileName, FileUtil.SUFFIX_MEX);
		if(flag) {
			return true;
		}
		
		flag = FileUtil.isAnyTypeOf(fileName, FileUtil.SUFFIX_SIRAP);
		if(flag) {
			return true;
		}
		
		return false;
	}
	
	public static List<String> readTextContent(String filePath) {
		return readTextContent(filePath, false);
	}
	
	public static List<String> readTextContent(String filePath, boolean readAsTextAnyway) {
		String charset = IOUtil.charsetOfTextFile(filePath);
		return readTextContent(filePath, readAsTextAnyway, charset);
	}
	
	public static List<String> readTextContent(String filePath, boolean readAsTextAnyway, String charset) {
		
		List<String> records = new ArrayList<String>();
		if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIX_MEX)) {
			MexedList mlist = MexUtil.readMexedList(filePath);
			if(mlist != null) {
				List<String> temp = mlist.getAllRecords();
				if(temp != null) {
					records.addAll(temp);
				}
			}
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIX_SIRAP)) {
			String temp = IOUtil.readFileWithoutLineSeparator(filePath);
			String passcode = SimpleKonfig.g().getSecurityPasscode();
			String content = TrumpUtil.decodeBySIRAP(temp, passcode);
			if(content != null) {
				records.add(content);
			}
		} else if(readAsTextAnyway || isAcceptableFormat(filePath, FileUtil.SUFFIXES_TEXT, KEY_TEXT)) {
			List<String> temp = IOUtil.readFileIntoList(filePath, charset);
			if(temp != null) {
				records.addAll(temp);
			}
		} else {
			throw new MexException("Not an acceptable text file: " +filePath);
		}
		
		return records;
	}
	
	public static boolean isAcceptableFormat(String fileName, String regularSuffixes, String keyOfExtraSuffixes) {
		boolean flag = FileUtil.isAnyTypeOf(fileName, regularSuffixes);
		if(flag) {
			return true;
		}
		
		String extraSuffixes = SimpleKonfig.g().getValueOf(keyOfExtraSuffixes);
		if(EmptyUtil.isNullOrEmpty(extraSuffixes)) {
			return false;
		}
		
		flag = FileUtil.isAnyTypeOf(fileName, extraSuffixes);
		
		return flag;
	}

	public static void playThing(String filePath, String playerKey) {
		playThing(filePath, playerKey, false);
	}
	
	public static boolean playThing(String filePath, String playerKey, boolean mandatory) {
		String appDir = SimpleKonfig.g().getUserValueOf(playerKey);
		if(mandatory) {
			if(EmptyUtil.isNullOrEmpty(appDir)) {
				C.pl("Can't find player with key: " + playerKey);
			} else {
				if(!FileUtil.exists(appDir)) {
					C.pl("Can't find player with path: " + appDir);
				}
			}
		}
		playThingByAppName(filePath, appDir);
		
		return true;
	}
	
	public static boolean playThingByAppName(String filePath, String appDir) {
		if(PanaceaBox.isMac()) {
			return PanaceaBox.openFile(filePath);
		}
		
		if(EmptyUtil.isNullOrEmpty(appDir) || appDir.length() < 5) {
			PanaceaBox.openFile(filePath);
		} else {
			PanaceaBox.openFile(appDir, filePath);
		}
		
		return true;
	}
}
