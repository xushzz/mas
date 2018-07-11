package com.sirap.basic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.thread.Master;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.thread.business.InternetFileFetcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.tool.MexedAudioPlayer;

@SuppressWarnings({"rawtypes"})
public class IOUtil {
	
	public static String charset() {
		return Charset.defaultCharset().name();
	}
	/***
	 * 1) regular file such as > D:/Gitpro/OSChina/todos/high/celine.html
	 * 	  regular file such as > D:\Gitpro\OSChina\todos\high\celine.html
	 *    regular file such as > \\LAPTOP-RDK8AQNO\OSChina\stamina\README.md
	 *    regular file such as > file:///D:/Gitpro/OSChina/todos/high/celine.html
	 * 2) resource file such as > /application.properties in windows
	 * 3) remote file such as https://gitee.com/thewire/todos/raw/master/high/celine.js
	 * 
	 * @param dynamicLocation
	 * @return
	 */
	public static List<String> readLines(String dynamicLocation) {
		return readLines(dynamicLocation, charset());
	}
	
	public static InputStream streamByClassLoader(String path) {
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		
		return ins;
	}
	
	public static List<String> readLinesFromStream(InputStream ins, String charset) {
		List<String> lines = Lists.newArrayList();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ins, charset));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return lines;
	}
	
	public static List<String> readLines(String dynamicLocation, String charset) {
		if(MiscUtil.isHttp(dynamicLocation)) {
			WebReader xiu = new WebReader(dynamicLocation, charset);
			return xiu.readIntoList();
		}

		List<String> lines = Lists.newArrayList();
		
		try {
			InputStream ins = null;
			String asresource = StrUtil.parseParam("#(.+?)", dynamicLocation);

			if(asresource != null) {
				ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(asresource);
			}
			
			if(ins == null) {
				String temp = dynamicLocation.replaceAll("^file:///", "");
				ins = new FileInputStream(temp);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(ins, charset));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}

			br.close();
		} catch (Exception ex) {
			D.pl("Exception while dealing with file: " + (new File(dynamicLocation).getAbsolutePath()));
			ex.printStackTrace();
			throw new MexException(ex);
		}

		return lines;
	}

	public static String readStringWithLineSeparator(String dynamicLocation) {
		return readString(dynamicLocation, charset(), Konstants.NEWLINE);
	}
	
	public static String readStringWithLineSeparator(String fileName, String lineSeperator) {
		return readString(fileName, charset(), lineSeperator);
	}
	
	public static String readString(String dynamicLocation) {
		return readString(dynamicLocation, charset(), "");
	}
	
	public static String readString(String dynamicLocation, String charset) {
		return readString(dynamicLocation, charset, "");
	}
	
	public static String readString(String dynamicLocation, String charset, String lineSeperator) {
		if(MiscUtil.isHttp(dynamicLocation)) {
			WebReader xiu = new WebReader(dynamicLocation, charset);
			return xiu.readIntoString();
		}

		StringBuffer sb = StrUtil.sb();
		
		try {
			InputStream ins = null;
			if(PanaceaBox.isWindows()) {
				if(dynamicLocation.startsWith("/")) {
					ins = InputStream.class.getResourceAsStream(dynamicLocation);
				}
			}
			
			if(ins == null) {
				String temp = dynamicLocation.replaceAll("^file:///", "");
				ins = new FileInputStream(temp);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(ins, charset));
			String line;
			boolean theFirstOne = true;
			while ((line = br.readLine()) != null) {
				if(!theFirstOne) {
					sb.append(lineSeperator);
				}
				sb.append(line);
			}

			br.close();
		} catch (Exception ex) {
			D.pl("Exception while dealing with file: " + (new File(dynamicLocation).getAbsolutePath()));
			throw new MexException(ex);
		}

		return sb.toString();
	}
	
	public static boolean isSourceExist(String filePath) {
		return InputStream.class.getResourceAsStream(filePath) != null;
	}
	
	@Deprecated
	public static List<String> readFileIntoList(String fileName) {
		String cat = charsetOfTextFile(fileName);
		return readLines(fileName, cat);
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
			String explain = XXXUtil.explainResponseException(ex.getMessage());
			String template = "{0}\n\turl => {1}\n\tlocation => {2}.downloadNormalFile";
			if(explain != null) {
				template += "\n\tstatus code => {3}";
			}
			C.pl(StrUtil.occupy(template, ex, address, IOUtil.class.getName(), explain));
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
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
			return ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException("{0}, {1}, {2}.", IOUtil.class, "readObject", fileName);
		}
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
	
	public static boolean copyFile(File sourceFile, File targetFile)  {
    	XXXUtil.nullOrEmptyCheck(sourceFile, "sourceFile");
    	XXXUtil.nullOrEmptyCheck(targetFile, "targetPath");
    	
    	if(sourceFile.getParentFile().equals(targetFile)) {
    		XXXUtil.info("File [{0}] is already out there [{1}].", sourceFile, targetFile);
    		return false;
    	}

    	BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

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
		
		MasterItemOriented<String, String> master = new MasterItemOriented<String, String>(links, dinesh){
			@Override
			protected int countOfThread() {
				int temp = threads <= 0 ? super.countOfThread() : threads;
				return temp;
			}
		};

		return master.getValidStringResults();
	}
	
	@SuppressWarnings("resource")
	public static Class loadClassFromJarFile(String jarLocation, String className) {
		String regex = "^[\\w]{2,}:";
		String tempLocation = FileUtil.unixSeparator(jarLocation);
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
		String tempLocation = FileUtil.unixSeparator(fileLocation);
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
	
	public static String charsetOfTextFile(String filepath) {
		try(InputStream lucy = new FileInputStream(filepath)) {
		    return charsetOfStream(lucy);
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	/***
	 * 0xefbb utf8
	 * 0xfffe unicode -1,-2
	 * 
	 * @param stream
	 * @return
	 */
	public static String charsetOfStream(InputStream stream) {
	    Integer key0xefbb = 61371, key0xfeff = 65279, key0xfffe = 65534;
		Map<Integer, String> headAndType = Maps.newConcurrentMap();
		headAndType.put(key0xefbb, "UTF-8");
		headAndType.put(key0xfeff, "UTF-16BE");
		headAndType.put(key0xfffe, "Unicode");

		try(BufferedInputStream lucy = new BufferedInputStream(stream)) {
			String code = "GBK";
		    int head = (lucy.read() << 8) + lucy.read();
		    String temp = headAndType.get(head);
		    if(temp != null) {
		    	code = temp;
		    }
//		    D.pl(head, code);
			return code;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	public static void save(byte[] file, String filePath) { 
        try(FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(file);
            out.flush();
            out.close();
        } catch (Exception ex) {
        	throw new MexException(ex);
        }
    }
}
