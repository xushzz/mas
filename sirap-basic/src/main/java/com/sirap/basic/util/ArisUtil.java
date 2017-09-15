package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.exception.MexException;

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
}
