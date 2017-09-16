package com.sirap.basic.util;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.ArisDetail;

public class ArisUtil {
	
	public static String belongsToWhichRuntimeJar(String targetEntryName) {
		String jreLibrary = getRuntimeLibraryLocation();
		List<String> names = FileUtil.getAllXXXFiles(jreLibrary, ".jar");
		for(String jarPath : names) {
			boolean flag = existEntry(jarPath, targetEntryName);
			if(flag) {
				return jarPath;
			}
		}
		
		return null;
	}
	
	public static boolean existEntry(String filepath, String targetEntryName){
		try(ZipFile zipFile = new ZipFile(filepath)) {
			Enumeration what = zipFile.entries();
			while (what.hasMoreElements()) {
		    	Object obj = what.nextElement();
		    	if(obj instanceof ZipEntry) {
		    		ZipEntry entry = (ZipEntry)obj;
		    		if(StrUtil.equals(entry.getName(), targetEntryName)) {
		    			return true;
		    		}
		    	}
			}
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
		
		return false;
	}
	
	public static List<String> siblingClasses(String filepath, String brotherName) {
		if((new File(filepath).isFile())) {
			return siblingClassesByJar(filepath, brotherName);
		} else {
			return siblingClassesByFolder(filepath, brotherName);
		}
	}
	
	/**
	 * 
	 * @param filepath
	 * @param brotherName such as: com/sirap/basic/component/CleverFolder.class
	 * @return
	 */
	private static List<String> siblingClassesByJar(String filepath, String brotherName){
		String regexClassName = "/[^/\\.]+\\.class";
		String lead = brotherName.replaceAll(regexClassName, "{0}");
		String regex = StrUtil.occupy(lead, regexClassName);
		List<String> items = new ArrayList<>();
		try(ZipFile zipFile = new ZipFile(filepath)) {
			Enumeration what = zipFile.entries();
			while (what.hasMoreElements()) {
		    	Object obj = what.nextElement();
		    	if(obj instanceof ZipEntry) {
		    		ZipEntry entry = (ZipEntry)obj;
		    		boolean flag = StrUtil.isRegexMatched(regex, entry.getName());
		    		if(flag) {
		    			String temp = entry.getName();
		    			items.add(temp.replaceAll("[/\\\\]", ".").replaceAll("\\.class$", ""));
		    		}
		    	}
			}
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
		
		return items;
	}

	private static List<String> siblingClassesByFolder(String filepath, String brotherName){
		String regexClassName = "/[^/\\.]+\\.class";
		String lead = brotherName.replaceAll(regexClassName, "");
		String folder = StrUtil.useSeparator(filepath, lead);
		File file = new File(folder);
		File[] subFiles = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File subFile) {
				return StrUtil.endsWith(subFile.getName(), ".class");
			}
		});
		List<String> items = new ArrayList<>();
		if(!EmptyUtil.isNullOrEmpty(subFiles)) {
			for(File subFile : subFiles) {
				String fullpath = subFile.getAbsolutePath();
				String parent = (new File(filepath)).getAbsolutePath() + File.separator;
				String temp = fullpath.replace(parent, "");
				items.add(temp.replaceAll("[/\\\\]", ".").replaceAll("\\.class$", ""));
			}
		}
		
		return items;
	}

	public static List<MexZipEntry> parseZipEntries(String filepath) {
		try(ZipFile jarFile = new ZipFile(filepath)) {
			List<MexZipEntry> items = new ArrayList<>();
		    Enumeration what = jarFile.entries();
		    String jarName = jarFile.getName();
		    while (what.hasMoreElements()) {
		    	Object obj = what.nextElement();
		    	if(obj instanceof ZipEntry) {
			    	ZipEntry entry = (ZipEntry)obj;
			    	MexZipEntry xiu = new MexZipEntry(entry);
			    	xiu.setJarName(jarName);
			    	items.add(xiu);
		    	}
		    }
		    
		    return items;
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}
	
	public static String getRuntimeLibraryLocation() {
		String path = System.getenv("PATH");
		boolean isMacOrLinuxOrUnix = PanaceaBox.isMacOrLinuxOrUnix();
		String what = PanaceaBox.isMacOrLinuxOrUnix() ? ":" : ";";
		List<String> items = StrUtil.split(path, what);
		for(String item : items) {
			String regex = "jre.*(/|\\\\)bin$";
			if(StrUtil.isRegexFound(regex, item)) {
				String libPath = StrUtil.useSeparator(item.replaceAll("bin$", ""), "lib");
				if(FileUtil.exists(libPath)) {
					if(isMacOrLinuxOrUnix) {
						libPath = libPath.replace("\\", "/");
					} else {
						libPath = libPath.replace("/", "\\");
					}
					return libPath;
				}
			}
		}
		
		throw new MexException("JRE not found, uncanny.");
	}
	
	public static List<String> getClassDetail(Class glass, String sourceLocation) {
		ArisDetail xiu = new ArisDetail(glass, sourceLocation);
		return xiu.getAllParts();
	}
	
	public static List<String> getClassDetail(Class glass) {
		ArisDetail xiu = new ArisDetail(glass);
		return xiu.getAllParts();
	}
	
	public static String sourceLocation(Class glass) {
		CodeSource source = glass.getProtectionDomain().getCodeSource();
		if(source != null && source.getLocation() != null) {
			String temp = source.getLocation().toString();
			temp = temp.replaceAll("^file:/", "").replaceAll("/$", "");
			return temp;
		} else {
			String entryName = glass.getName().replace('.', '/') + ".class";
			String someRuntimeJar = ArisUtil.belongsToWhichRuntimeJar(entryName);
			if(someRuntimeJar != null) {
				return someRuntimeJar;
			} else {
				return StrUtil.occupy("Uncanny, not found {0} in {1}", entryName, ArisUtil.getRuntimeLibraryLocation());
			}
		}
	}
	
	public static List<String> readZipEntry(String url, String entryName) {
		try(ZipFile file = new ZipFile(url)) {
		    ZipEntry entry = file.getEntry(entryName);
		    if(entry == null) {
		    	String msg = "The entry '{0}' doesn't exist.";
		    	throw new MexException(StrUtil.occupy(msg, entryName));
		    }
		    boolean dealWithDirectory = false;
		    if(entry.isDirectory()) {
		    	dealWithDirectory = true;
		    } else {
		    	if(entry.getSize() == 0) {
		    		ZipEntry likeEntry = file.getEntry(entryName + "/");
		    		if(likeEntry != null && likeEntry.isDirectory()) {
		    			dealWithDirectory = true;
		    		}
		    	}
		    }
		    
		    if(dealWithDirectory) {
		    	List<String> items = new ArrayList<>();
		    	Enumeration entries = file.entries();
		        while (entries.hasMoreElements()) {  
		        	ZipEntry zack = (ZipEntry)entries.nextElement();
		        	if(StrUtil.startsWith(zack.getName(), entryName)) {
		        		items.add(url + "!" + zack.getName());
		        	}
		        }
		        
		        return items;
		    }
		    
		    if(StrUtil.endsWith(entryName, Konstants.SUFFIX_CLASS)) {
		    	return ArisUtil.getClassDetail(IOUtil.loadClassFromJarFile(url, entryName));
		    } else {
			    return IOUtil.readStreamIntoList(file.getInputStream(entry));
		    }
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}

}
