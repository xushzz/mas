package com.sirap.basic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedMap;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.thirdparty.excel.ExcelHelper;
import com.sirap.basic.thirdparty.pdf.PdfHelper;
import com.sirap.basic.thread.Master;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.thread.business.InternetFileFetcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.tool.MexedAudioPlayer;

@SuppressWarnings({"rawtypes","unchecked"})
public class IOUtil {
	
	public static String readURL(String address) {
		return readURL(address, Konstants.CODE_UTF8);
	}
	
	public static String readURL(String address, String charset) {
		return readURL(address, charset, true);
	}

	public static String readURL(String address, String charset, boolean printException) {
		WebReader xiu = new WebReader(address, charset);
		return xiu.readIntoString();
	}

	public static List<String> readURLIntoList(String address, String charset, boolean printException) {
		WebReader xiu = new WebReader(address, charset);
		return xiu.readIntoList();
	}
	
	public static List<String> readResourceIntoList(String filePath) {
		InputStream inputStream = InputStream.class.getResourceAsStream(filePath);
		if(inputStream == null) {
			return null;
		}
		
		return readStreamIntoList(inputStream);
	}
	
	public static boolean isSourceExist(String filePath) {
		InputStream inputStream = InputStream.class.getResourceAsStream(filePath);
		if(inputStream == null) {
			return false;
		}
		
		return true;
	}
	
	public static List<String> readStreamIntoList(InputStream inputStream) {
		return readStreamIntoList(inputStream, false, "");
	}
	
	public static List<String> readStreamIntoList(InputStream inputStream, boolean printAlong) {
		return readStreamIntoList(inputStream, printAlong, "");
	}

	public static List<String> readStreamIntoList(InputStream inputStream, boolean printAlong, String prefix) {
		List<String> list = new ArrayList<String>();
		
		try {
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String record;
			while ((record = br.readLine()) != null) {
				if(printAlong) {
					C.pl(record);
				}
				list.add(prefix + record);
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
	}

	public static String readFileWithoutLineSeparator(String fileName) {
		return readFileWithLineSeparator(fileName, "", null);
	}

	public static String readFileWithRegularLineSeparator(String fileName) {
		return readFileWithLineSeparator(fileName, Konstants.NEWLINE, null);
	}
	
	public static String readFileWithLineSeparator(String fileName, String lineSeperator) {
		return readFileWithLineSeparator(fileName, lineSeperator, null, new String[0]);
	}

	public static String readFileWithoutLineSeparator(String fileName, String charset) {
		return readFileWithLineSeparator(fileName, "", charset);
	}

	public static String readFileWithRegularLineSeparator(String fileName, String charset) {
		return readFileWithLineSeparator(fileName, Konstants.NEWLINE, charset);
	}
	
	public static String readFileWithLineSeparator(String fileName, String lineSeperator, String charset) {
		return readFileWithLineSeparator(fileName, lineSeperator, charset, new String[0]);
	}

	/***
	 * 
	 * @param fileName
	 * @param lineSeperator
	 * @param prefixesToIgnoreOneline
	 * @return
	 */
	public static String readFileWithLineSeparator(String fileName, String lineSeperator, String charset, String... prefixesToIgnoreOneline) {
		StringBuffer sb = new StringBuffer();

		try {
			InputStreamReader isr = null;
			if(charset != null) {
				isr = new InputStreamReader(new FileInputStream(fileName), charset);
			} else {
				isr = new InputStreamReader(new FileInputStream(fileName));
			}
			BufferedReader br = new BufferedReader(isr);
			String record = br.readLine();
			boolean theFirstOne = true;
			while (record != null) {
				if(!theFirstOne) {
					sb.append(lineSeperator);
				}
				
				boolean toIgnore = StrUtil.startsWith(record.trim(), prefixesToIgnoreOneline);
				if(!toIgnore) {
					sb.append(record);
					theFirstOne = false;
				}
				
				record = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			C.pl(e);
		}

		return sb.toString();
	}
	
	public static List<String> readFileIntoList(String fileName) {
		return readFileIntoList(fileName, null);
	}
	
	public static List<String> readFileIntoList(String fileName, String charset) {
		List<String> list = new ArrayList<String>();
		try {
			InputStreamReader isr = null;
			if(charset != null) {
				isr = new InputStreamReader(new FileInputStream(fileName), charset);
			} else {
				isr = new InputStreamReader(new FileInputStream(fileName));
			}
			BufferedReader br = new BufferedReader(isr);
			String record = br.readLine();
			while (record != null) {
				list.add(record);
				record = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public static int[] countOfLinesChars(String fileName) {
		int[] countArr = new int[2];
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String record = br.readLine();
			while (record != null) {
				countArr[0]++;
				countArr[1] += record.length();
				record = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return countArr;
	}
	
	public static MexedMap createMexedMapByRegularFile(String filePath) {
		try {
			InputStream inputStream = new FileInputStream(filePath);
			return readKeyValuesIntoMexedMap(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static MexedMap createMexedMapByResourceFile(String filePath) {
		InputStream inputStream = InputStream.class.getResourceAsStream(filePath);
		
		return readKeyValuesIntoMexedMap(inputStream);
	}
	
	public static MexedMap createMexedMapByProperties(Properties props) {
		XXXUtil.nullCheck(props, "Properties props");
		
		MexedMap map = new MexedMap();
		Enumeration<?> en = props.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = props.getProperty(key).trim();
			map.put(key.toLowerCase(), value);
		}

		return map;
	}
	
	public static Properties readProperties(String filePath) {
		Properties props = new Properties();
		
		try {
			InputStream inputStream = new FileInputStream(filePath);
			props = readProperties(inputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return props;
	}
	
	public static Properties readProperties(InputStream inputStream) {
		Properties props = new Properties();
		
		try {
			props.load(inputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return props;
	}
	
	public static MexedMap readKeyValuesIntoMexedMap(InputStream stream) {
		XXXUtil.nullCheck(stream, "InputStream stream");
		MexedMap map = new MexedMap();
		try {
			BufferedReader reader =new BufferedReader(new InputStreamReader(stream, "gbk"));
			String record;
			while ((record = reader.readLine()) != null) {
				String temp = record.trim();
				
				boolean toIgnore = StrUtil.startsWith(temp, "#");
				if(toIgnore) {
					continue;
				}
				
				String regex = "([^=]+)=(.+)";
				String[] params = StrUtil.parseParams(regex, temp);
				if(params == null) {
					continue;
				}
				
				String key = params[0].trim();
				String value = params[1].trim();
				
				if(key.isEmpty() || value.isEmpty()) {
					continue;
				}
				
				map.put(key, value);
			}
			
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public static MexedMap readPropertiesIntoMexedMap(InputStream stream) {
		XXXUtil.nullCheck(stream, "InputStream stream");
		
		try {
			BufferedReader reader =new BufferedReader(new InputStreamReader(stream, "gbk"));
			Properties props = new Properties();
			props.load(reader);
			return createMexedMapByProperties(props);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void playSound(String soundSource) {
		MexedAudioPlayer andy = new MexedAudioPlayer(soundSource);
    	andy.play();
	}
	
	private static void setUserAgent(URLConnection urlConn) {
		urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1WOW64rv:25.0) Gecko/20100101 Firefox/25.0");
	}
	
	public static boolean downloadNormalFile(String address, String filePath) {
		return downloadNormalFile(address, filePath, false);
	}
	
	public static boolean downloadNormalFile(String address, String filePath, boolean checkSize) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		
		long fileSizeInBytes = -1;
		boolean flag = false;
		try {
			URLConnection conn = new URL(address).openConnection();
			setUserAgent(conn);
			conn.setConnectTimeout(9000);
			conn.setReadTimeout(9000);
			fileSizeInBytes = conn.getContentLengthLong();
			inBuff = new BufferedInputStream(conn.getInputStream());
			outBuff = new BufferedOutputStream(new FileOutputStream(filePath));
			int t;
			while ((t = inBuff.read()) != -1) {
				outBuff.write(t);
			}
			flag = true;
		} catch (Exception ex) {
			C.pl(ex + "\n\turl=>" + address + "\n\tLocation=>" + IOUtil.class.getName() + ".downloadNormalFile");
			File invalidFile = new File(filePath);
			if(invalidFile.exists()) {
				invalidFile.delete();
			}
		} finally {
        	try {
                if (inBuff != null)
                	inBuff.close();
                if (outBuff != null)
                    outBuff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		if(checkSize) {
			if(fileSizeInBytes < 0) {
				return false;
			}
			boolean isComplete = false;
			File temp = new File(filePath);
			if(temp.exists()) {
				double diffPerc = 0.01;
				long min = (long) (fileSizeInBytes * (1 - diffPerc));
				isComplete = temp.length() > min;
			}
			
			if(!isComplete) {
				C.pl("Incomplete, going to delete file: " + filePath);
				temp.delete();
			}
			
			flag = isComplete;
		}
		
		return flag;
	}
	
	public static boolean append(Object content, String fullFileName) {
		List<Object> objList = new ArrayList<>();
		objList.add(content);
		
		return append(objList, fullFileName);
	}
	
	public static boolean append(List<Object> objList, String fullFileName) {
		BufferedWriter thomas = null;
		try {
			thomas = new BufferedWriter(new FileWriter(fullFileName, true));
			
			for(Object member: objList) {
				thomas.write(member.toString());
				thomas.newLine();
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(thomas != null) thomas.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean saveAsTxtWithHeaderAndTotal(List objList, String fullFileName) {
		return saveAsTxt(objList, fullFileName, true, true);
	}
	
	public static boolean saveAsTxtWithHeaderOnly(List objList, String fullFileName) {
		return saveAsTxt(objList, fullFileName, true, false);
	}
	
	public static boolean saveAsTxt(List objList, String fullFileName) {
		return saveAsTxt(objList, fullFileName, false, false);
	}
	
	public static boolean saveAsTxt(List objList, String fullFileName, boolean printSimpleHeader, boolean printTotal) {
		if(objList == null) {
			objList = Collections.EMPTY_LIST;
		}
		
		List<String> header = new ArrayList<String>();
		if(printSimpleHeader) {
			header.add("#Date: " + new Date() + ", KY");
		}
		
		List<String> footer = new ArrayList<String>();
		if(printTotal) {
			footer.add("#Records:" + objList.size());
		}
		
		return saveAsTxt(objList, fullFileName, header, footer);
	}
	
	public static boolean saveAsTxt(List objList, String fullFileName, List<String> header, List<String> footer) {
		XXXUtil.nullCheck(objList, "List objList");
		
		BufferedWriter thomas = null;
		try {

			thomas = new BufferedWriter(new FileWriter(fullFileName));
			if(!EmptyUtil.isNullOrEmpty(header)) {
				for(String record:header) {
					thomas.write(record);
					thomas.newLine();
				}
			}
			
			for(Object member: objList) {
				thomas.write(MexUtil.print(member));
				thomas.newLine();
			}
			
			if(!EmptyUtil.isNullOrEmpty(footer)) {
				for(String record:footer) {
					thomas.write(record);
					thomas.newLine();
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(thomas != null) thomas.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean saveAsTxtWithCharset(List objList, String fullFileName, String charset) {
		XXXUtil.nullCheck(objList, "List objList");
		
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fullFileName), charset)) {
			for(Object member: objList) {
				out.write(MexUtil.print(member));
				out.write("\r\n");
			}
			
			return true;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}

	public static Object readObject(String fileName) {
		Object obj = null;
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch (Exception e) {
			D.debug(IOUtil.class, "readObject", fileName);
			e.printStackTrace();
		} finally {
			try {
				if(fis != null) {
					fis.close();
				}
				
				if(ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return obj;
	}

	public static boolean saveObject(Serializable obj, String fileName) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(obj);
			oos.flush();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * @See MexUtil.copyFiles(List<File>, String), which yields better performance 
	 * @param sourceFiles
	 * @param targetFolder
	 * @return
	 */
	public static int[] copyFilesSequentially(List<File> sourceFiles, String targetFolder)  {
		int[] results = new int[2];
		for(File file:sourceFiles) {
			boolean flag = copyFileToFolder(file, targetFolder);
			if(flag) {
				results[0]++;
			} else {
				results[1]++;
			}
		}
		
		return results;
	}
	
	public static boolean copyFileToFolder(String sourceFilePath, String targetFolder)  {
		return copyFileToFolder(new File(sourceFilePath), targetFolder);
	}
	
	public static boolean isInTheFolder(File file, String folder) {
		boolean flag = file.getParentFile().equals(new File(folder));
		
		return flag;
	}
	
	public static boolean copyFileToFolder(File sourceFile, String targetFolder)  {
    	XXXUtil.nullOrEmptyCheck(sourceFile, "sourceFile");
    	XXXUtil.nullOrEmptyCheck(targetFolder, "targetFolder");
    	
    	if(sourceFile.getParentFile().equals(new File(targetFolder))) {
    		XXXUtil.info("File [{0}] is already out there [{1}].", sourceFile, targetFolder);
    		return false;
    	}

    	BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
        	String targetPath = StrUtil.useSeparator(targetFolder, sourceFile.getName()); 
        	FileUtil.makeDirectoriesIfNonExist(targetFolder);
            outBuff = new BufferedOutputStream(new FileOutputStream(targetPath));

            byte[] b = new byte[Konstants.FILE_SIZE_STEP * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
            
            return true;
        } catch (Exception ex) {
        	ex.printStackTrace();        	
        } finally {
        	try {
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return false;
    }
	
	public static void copyFiles(List<File> sourcefiles, final String targetFolder) {
		Master<File> george = new Master<File>(sourcefiles, new Worker<File>() {
			public void process(File normalFile) {
				String sourceFilepath = normalFile.getPath();
				String targetFilepath = StrUtil.useSeparator(targetFolder, normalFile.getName());
				int count = countOfTasks - queue.size();
				status(STATUS_FILE_COPY, count, countOfTasks, "Copying...", sourceFilepath, targetFolder);
				IOUtil.copyFileToFolder(sourceFilepath, targetFolder);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Copied", targetFilepath);
			}
		});
		george.sitAndWait();
	}
	
	public static List<String> downloadFiles(String storage, List<String> links, String suffixWhenObscure, final int threads) {
		return downloadFiles(storage, links, suffixWhenObscure, threads, false);
	}
	
	public static List<String> downloadFiles(String storage, List<String> links, String suffixWhenObscure, final int threads, boolean useUniqueFilename) {
		FileUtil.makeDirectoriesIfNonExist(storage);
		
		InternetFileFetcher dinesh = new InternetFileFetcher(storage, suffixWhenObscure);
		dinesh.setUseUniqueFilename(useUniqueFilename);
		
		MasterItemOriented<String> master = new MasterItemOriented<String>(links, dinesh){
			@Override
			protected int countOfThread() {
				int temp = threads <= 0 ? super.countOfThread() : threads;
				return temp;
			}
		};

		return master.getValidStringResults();
	}
	
	public static boolean saveAsPDF(List objList, String fullFileName, PDFParams params) {
		return PdfHelper.export(objList, fullFileName, params);
	}
	
	public static boolean saveAsPDF(List objList, String fullFileName) {
		return PdfHelper.export(objList, fullFileName, new PDFParams());
	}
	
	public static boolean saveAsExcel(List objList, String fullFileName, ExcelParams params) {
		return ExcelHelper.export(objList, fullFileName, params);
	}
	
	public static List<String> echoPath() {
		String path = System.getProperty("java.library.path");
		char delimiter = ';';
		if(PanaceaBox.isMacOrLinuxOrUnix()) {
			delimiter = ':';
		}
		List<String> items = StrUtil.split(path, delimiter);

		return items;
	}
	
	@SuppressWarnings("resource")
	public static Class loadClassFromJarFile(String jarLocation, String className) {
		String regex = "^[\\w]{2,}:";
		String tempLocation = jarLocation.replace('\\', '/');
		if(!StrUtil.isRegexFound(regex, tempLocation)) {
			tempLocation = "file:" + tempLocation;
		}
		
		try {
			URL url = new URL(tempLocation);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url}, loader);
		    String tempClassName = className.replaceAll(".class$", "");
		    tempClassName = tempClassName.replaceAll("/", ".");

		    Class glass = myClassLoader.loadClass(tempClassName);
		    
		    return glass;
		} catch (Throwable ex) {
			throw new MexException(ex);
		}
	}
	
	public static Class loadClassFile(final String fileLocation) {
		String regex = "^[\\w]{2,}:";
		String tempLocation = fileLocation.replace('\\', '/');
		if(!StrUtil.isRegexFound(regex, tempLocation)) {
			tempLocation = "file:///" + tempLocation;
		}
		ClassLoader wood = new ClassLoader() {
			@SuppressWarnings("deprecation")
			protected Class<?> findClass(String uri) throws ClassNotFoundException {
				byte[] bytes = null;
				try {
					Path path = Paths.get(new URI(uri));
					bytes = Files.readAllBytes(path);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				Class tang = defineClass(bytes, 0, bytes.length);  
				 return tang;
			}
		};
		
		try {
			return wood.loadClass(tempLocation);
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
}
