package com.sirap.basic.util;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.MexedList;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.D;

public class MexUtil {

	public static final String PREFIX_POUND = "#";
	public static final String PREFIX_TYPE = "Type: ";
	
	public static String createClassNameHeader(String className) {
		String header = PREFIX_TYPE + className;
		return header;
	}
	
	public static String parseMexClassName(String record) {
		String regex = PREFIX_POUND + PREFIX_TYPE + "\\s*([a-z|\\.]+)";
		String param = StrUtil.parseParam(regex, record);
		
		return param;
	}
	
	public static boolean isIgnorable(String record) {
		if(EmptyUtil.isNullOrEmpty(record)) {
			return true;
		}
		
		String temp = record.trim();
		if(temp.startsWith(PREFIX_POUND)) {
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean saveAsNew(List objList, String fullFileName) {
		XXXUtil.nullCheck(objList, "List objList");
		
		List<String> header = new ArrayList<String>();
		header.add(PREFIX_POUND + "Saved: " + new Date() + ", KY");
		if(objList.size() > 0) {
			String className = objList.get(0).getClass().getName();
			header.add(PREFIX_POUND + createClassNameHeader(className));
		}
		
		List<String> footer = new ArrayList<String>();
		footer.add(PREFIX_POUND + "Records:" + objList.size());
		
		if(FileUtil.isMexFile(fullFileName)) {
			return saveAsMex(objList, fullFileName, header, footer);
		} else {
			return IOUtil.saveAsTxt(objList, fullFileName, header, footer);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean saveAsMex(List objList, String fullFileName) {
		return saveAsMex(objList, fullFileName, null, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean saveAsMex(List objList, String fullFileName, List<String> header, List<String> footer) {
		List<MexItem> mexItems = new ArrayList<MexItem>();
		for(Object obj:objList) {
			if(obj instanceof MexItem) {
				mexItems.add((MexItem)obj);
			} else {
				mexItems.add(new MexObject(obj));
			}
		}

		MexedList genius = new MexedList(mexItems);
		genius.setHeader(header);
		genius.setFooter(footer);
		
		return IOUtil.saveObject(genius, fullFileName);
	}

	public static <E extends MexItem> List<E> readMexItemsViaUnderlyingClassName(String fileName) {
		String underlyingClassName = getUnderlyingClassNameByFile(fileName);
		boolean flag = ObjectUtil.isInherit(underlyingClassName, MexItem.class);
		if(!flag) {
			XXXUtil.alert("underlying class [" + underlyingClassName + "] doesn't inherit " + MexItem.class.getName());
		}
		
		return readMexItemsViaClassName(fileName, underlyingClassName);
	}

	public static String getUnderlyingClassNameByFile(String fileName) {
		String className = null;
		
		BufferedReader kevin = null;
		try {
			File file = FileUtil.getIfNormalFile(fileName);
			if(file == null) return null;
			
			kevin = new BufferedReader(new FileReader(file));
			String record = null;
			while((record = kevin.readLine()) != null) {
				String temp = record.trim();
				if(temp.length() == 0) {
					continue;
				}
				
				className = parseMexClassName(temp);
				if(className != null) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(kevin != null) kevin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return className;
	}
	
	public static <E extends MexItem> List<E> readMexItemsViaExplicitClass(String fileName, Class<?> clazz) {
		return readMexItemsViaClassName(fileName, clazz.getName());
	}
	
	public static <E extends MexItem> List<E> readMexItemsViaClassName(String fileName, String className) {
		String charset = IOUtil.charsetOfTextFile(fileName);
		return readMexItemsViaClassName(fileName, className, charset);
	}
	 
	@SuppressWarnings("unchecked")
	public static <E extends MexItem> List<E> readMexItemsViaClassName(String fileName, String className, String charset) {
		List<E> list = new ArrayList<E>();
		
		if(EmptyUtil.isNullOrEmpty(className)) {
			return list;
		}
		
		try {
			InputStreamReader isr = null;
			if(charset != null) {
				isr = new InputStreamReader(new FileInputStream(fileName), charset);
			} else {
				isr = new InputStreamReader(new FileInputStream(fileName));
			}
			BufferedReader kevin = new BufferedReader(isr);
			String record = null;
			while((record = kevin.readLine()) != null) {
				String temp = record.trim();
				
				if(isIgnorable(temp)) {
					continue;
				}
				
				MexItem coin = parseMexItem(className, temp);
				if(coin != null) {
					list.add((E)coin);
				}
			}
		} catch (Exception e) {
			D.params(fileName, className);
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static List<MexItem> readMexItems(String fileName) {
		return readMexItems(fileName, FileUtil.isMexFile(fileName));
	}
	
	@SuppressWarnings("unchecked")
	public static List<MexItem> readMexItems(String fileName, boolean isMexFile) {
		if(isMexFile) {
			Object obj = IOUtil.readObject(fileName);
			if(obj instanceof MexedList) {
				MexedList genius = (MexedList)obj;
				return genius.getItems();
			}
			
			return Collections.EMPTY_LIST;
		} else {
			return readMexItemsViaUnderlyingClassName(fileName);
		}
	}
	
	public static MexedList readMexedList(String fileName) {
		Object obj = IOUtil.readObject(fileName);
		if(obj instanceof MexedList) {
			MexedList genius = (MexedList)obj;
			return genius;
		}
		
		return null;
	}

	public static MexItem parseMexItem(String className, String record) throws Exception {
		Object instance = Class.forName(className).newInstance();
		if(instance instanceof MexItem) {
			MexItem c = (MexItem)instance;
			if(c.parse(record)) {
				return c;
			}
		}
		
		return null;
	}
	
	public static List<String> getNamesOfFiles(String directory) {
		return getNamesOfFiles(null, directory);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getNamesOfFiles(final String prefix, String directory) {
		File file = new File(directory);
		String[] names = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String temp = prefix != null ? prefix : ".*?";
				Matcher m = Pattern.compile(temp + "_.*?\\.(txt|data)", Pattern.CASE_INSENSITIVE).matcher(name);
				return m.matches();
			}
		});
		
		if(names == null) return Collections.EMPTY_LIST;
		
		return Arrays.asList(names);
	}
	
	public static String readStatusByFullName(String fullName, boolean returnNullIfNonExist) {
		StringBuffer sb = new StringBuffer();
		BufferedReader kevin = null;
		try {
			File file = new File(fullName);
			if(!file.exists()) {
				if(returnNullIfNonExist) {
					return null;
				} else {
					String status = "doesn't exist.";
					return status;
				}
			}
			
			if(FileUtil.isMexFile(fullName)) {
				String status = "Size(B):" + file.length();
				return status;
			} else {
				kevin = new BufferedReader(new FileReader(file));
				String record = null;
				while((record = kevin.readLine()) != null) {
					String temp = record.trim();
					if(temp.startsWith("#")) {
						sb.append(temp).append(" ");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(kevin != null) kevin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	public static String askForInput(String prompt) {
		return askForInput(prompt, false, true);
	}
	
	public static String askForHiddenInput(String prompt) {
		return askForInput(prompt, true, true);
	}
	
	public static String askForInput(String prompt, boolean isHidden, boolean toCollect) {
		String input = null;
		if(isHidden && !System.getProperty("user.dir").contains("workspace")) {
			input = PanaceaBox.askForHiddenInput(prompt);
		} else {
			input = PanaceaBox.askForInput(prompt);
		}
		
		return input;
	}
	
	public static boolean isAllTrue(boolean[] flags) {
		for(boolean flag : flags) {
			if(!flag) {
				return false;
			}
		}
		
		return true;
	}
	
	public static List<String> allFonts() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = e.getAvailableFontFamilyNames();
		return Arrays.asList(fontNames);
	}
}
