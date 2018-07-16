package com.sirap.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.MexedList;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.thirdparty.TrumpHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;

public class FileOpener {
	
	public static final String KEY_IMAGE = "format.iamge";
	public static final String KEY_AUDIO = "format.audio";
	public static final String KEY_VIDEO = "format.video";
	public static final String KEY_TEXT = "format.text";
	public static final String KEY_EXECUTABLE = "format.exe";
	public static final String KEY_OTHERS = "format.others";
	public static final String KEY_TXT_ULTRA_EDITOR = "ue";
	public static final String KEY_TXT_NOTEPAD = "np";
	
	public static void open(String filePath) {
		open(filePath, null);
	}
	
	public static void open(String filePath, String options) {
		filePath = FileUtil.windowsSeparator(filePath);
		
		if(OptionUtil.readBooleanPRI(options, KEY_TXT_ULTRA_EDITOR, false)) {
			C.pl2("Open by ultraedit: " + filePath);
			playThing(filePath, "txt.ue");
		} else if(OptionUtil.readBooleanPRI(options, KEY_TXT_NOTEPAD, false)) {
			C.pl2("Open by notepad: " + filePath);
			playThingByAppName(filePath, "notepad");
		} else if(OptionUtil.readBooleanPRI(options, "p", false)) {
			C.pl2("Open by mspaint: " + filePath);
			playThingByAppName(filePath, "mspaint");
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_AUDIO, KEY_AUDIO)) {
			C.pl2("Play audio");
			playThing(filePath, "audio.player");
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_VIDEO, KEY_VIDEO)) {
			C.pl2("Play video");
			playThing(filePath, "video.player");
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_EXECUTABLE, KEY_EXECUTABLE)) {
			C.pl2("Run application");
			PanaceaBox.execute(filePath);
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_IMAGE, KEY_IMAGE)) {
			C.pl2("View photo");
			playThing(filePath, "image.viewer");
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_PDF)) {
			C.pl2("View pdf");
			playThing(filePath, "pdf.viewer");
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_WORD)) {
			C.pl2("View word document");
			playThing(filePath, "word.viewer");
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
			C.pl2("View excel document");
			playThing(filePath, "excel.viewer");
		} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_HTML)) {
			C.pl2("View html document");
			playThing(filePath, "page.viewer");
		} else if(isAcceptableFormat(filePath, FileUtil.SUFFIXES_TEXT, KEY_TEXT)) {
			if(FileUtil.isAnyTypeOf(filePath, "bat")) {
				C.pl2("Open by notepad: " + filePath);
				playThingByAppName(filePath, "notepad");	
			} else {
				C.pl2("View text file.");
				PanaceaBox.openFile(filePath);
			}
		} else {
			PanaceaBox.openFile(filePath);
			C.pl2("Might not be able to deal with [" + filePath + "].");
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
	
	public static boolean isTextFile(String filename) {
		String extension = FileUtil.extensionOf(filename);
		//boolean flag = isAcceptableFormat(fileName, FileUtil.SUFFIXES_TEXT, KEY_TEXT);
		boolean flag = FileUtil.EXTENSIONS_TEXT.contains(extension);
		if(flag) {
			return true;
		}
		
//		flag = FileUtil.isAnyTypeOf(fileName, FileUtil.SUFFIX_MEX);
		flag = FileUtil.EXTENSIONS_HTML.contains(extension);
		if(flag) {
			return true;
		}
		
//		flag = FileUtil.isAnyTypeOf(fileName, FileUtil.SUFFIX_SIRAP);
		flag = FileUtil.EXTENSIONS_SIRAP.contains(extension);
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
		String extension = FileUtil.extensionOf(filePath);
		List<String> records = new ArrayList<String>();
		if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIX_MEX)) {
			MexedList mlist = MexUtil.readMexedList(filePath);
			if(mlist != null) {
				List<String> temp = mlist.getAllRecords();
				if(temp != null) {
					records.addAll(temp);
				}
			}
		} else if(StrUtil.isIn(extension, FileUtil.EXTENSIONS_SIRAP)) {
			String temp = IOUtil.readString(filePath);
			String passcode = SimpleKonfig.g().getSecurityPasscode();
			String content = TrumpHelper.decodeBySIRAP(temp, passcode);
			if(content != null) {
				records.add(content);
			}
		} else if(readAsTextAnyway || isAcceptableFormat(filePath, FileUtil.SUFFIXES_TEXT, KEY_TEXT)) {
			List<String> temp = IOUtil.readLines(filePath, charset);
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
		if(StrUtil.equals(playerKey, "page.viewer")) {
			String value = SimpleKonfig.g().getUserValueOf(playerKey);
			if(FileUtil.exists(value)) {
				C.pl("Use => " + value);
				C.pl("Site => " + filePath);
				playThingByAppName(filePath, value);
				
				return true;
			}
			
			Map<String, String> explorers = SatoUtil.allExplorers();
			if(!explorers.isEmpty()) {
				String explorerpath = null;
				if(value != null) {
					String keyword = StrUtil.parseParam("\\$(.+?)", value);
					if(keyword != null) {
						explorerpath = explorers.get(keyword);
						if(explorerpath == null) {
							C.pl("Not a valid key : " + keyword);
							C.pl("Available explorers:\n" + JsonUtil.toPrettyJson(explorers));
						}
					}
				}
				
				if(explorerpath == null) {
					explorerpath = Amaps.getFirst(explorers);
				}
				C.pl("Select => " + explorerpath);
				C.pl("Site => " + filePath);
				playThingByAppName(filePath, explorerpath);
				return true;
			}
		}
		
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
		if(SimpleKonfig.g().isFromWeb()) {
			XXXUtil.alerto("Forbidden to open [{0}] with [{1}].", filePath, appDir);
			return false;
		}
		
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
