package com.sirap.basic.util;

import java.io.File;
import java.io.FileFilter;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.ArisDetail;

public class ArisUtil {
	
	public static final List<String> SIRAP_PROJECTS = StrUtil.split("basic,common,db,executor,extractor,geek,ldap,titus");
	public static final String TEMP_REPO_SIRAP = "https://raw.githubusercontent.com/acesfullmike/mas/master/sirap-{0}/src/main/java/{1}.java";
	public static final String TEMP_REPO_THEWIRE = "https://gitee.com/thewire/jdk8/raw/master/src/{0}.java";

	/**
	 * https://raw.githubusercontent.com/acesfullmike/mas/master/sirap-basic/src/main/java/com/sirap/basic/util/OptionUtil.java
	 * @param className com/sirap/basic/algo/SM3.java
	 * @return
	 */
	public static String sourceCodeURL(String className) {
		String regexSirap = "^com/sirap/([^/]+)";
		String solo = StrUtil.findFirstMatchedItem(regexSirap, className);
		if(solo != null && SIRAP_PROJECTS.contains(solo)) {
			String url = StrUtil.occupy(TEMP_REPO_SIRAP, solo, className);
			return url;
		} else {
			return StrUtil.occupy(TEMP_REPO_THEWIRE, className);
		}
	}
	
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
		List<String> items = StrUtil.split(path, File.pathSeparator);
		for(String item : items) {
			String regex = "jre.*(/|\\\\)bin$";
			if(StrUtil.isRegexFound(regex, item)) {
				String libPath = StrUtil.useSeparator(item.replaceAll("bin$", ""), "lib");
				if(FileUtil.exists(libPath)) {
					if(isMacOrLinuxOrUnix) {
						libPath = FileUtil.unixSeparator(libPath);
					} else {
						libPath = FileUtil.windowsSeparator(libPath);
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
	
	public static List<String> getClassDetail(Class glass, String sourceLocation, boolean printException) {
		ArisDetail xiu = new ArisDetail(glass, sourceLocation);
		xiu.setPrintException(printException);
		return xiu.getAllParts();
	}
	
	public static List<String> getClassDetail(Class glass, boolean printException) {
		ArisDetail xiu = new ArisDetail(glass);
		xiu.setPrintException(printException);
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
		        		MexZipEntry xiu = new MexZipEntry(zack);
				    	xiu.setJarName(url);
		        		items.add(xiu.toPrint());
		        	}
		        }
		        
		        return items;
		    }
		    
		    if(StrUtil.endsWith(entryName, Konstants.DOT_CLASS)) {
		    	return ArisUtil.getClassDetail(IOUtil.loadClassFromJarFile(url, entryName));
		    } else {
			    return PanaceaBox.readStreamIntoList(file.getInputStream(entry), false);
		    }
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	public static List<String> generateImportsByJarFile(String jarFilepath) {
		return generateImportsByJarFile(jarFilepath, null);
	}
	
	public static List<String> generateImportsByJarFile(String jarFilepath, List<String> desiredPkgNames) {
		Set<String> checker = new HashSet<>();
		List<String> imports = new ArrayList<>();
		Pattern pa = Pattern.compile("(.+)/.+\\.class");
		try(JarFile jarFile = new JarFile(jarFilepath)) {
		    Enumeration<JarEntry> what = jarFile.entries();
		    while (what.hasMoreElements()) {
		    	JarEntry entry = what.nextElement();
	    		String name = entry.getName();
	    		Matcher ma = pa.matcher(name);
	    		if(ma.find()) {
	    			String path = ma.group(1);
	    			if(!checker.contains(path)) {
	    				checker.add(path);
	    				String fullPackageName = path.replace('/', '.');
	    				if(EmptyUtil.isNullOrEmpty(fullPackageName)) {
	    					continue;
	    				}
	    				if(!EmptyUtil.isNullOrEmpty(desiredPkgNames) && !StrUtil.startsWith(fullPackageName, desiredPkgNames)) {
	    					continue;
	    				}
		    			String item = "import " + fullPackageName + ".*;";
		    			imports.add(item);
	    			}	    			
	    		}
		    }
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
		
		return imports;
	}
	
	public static List<String> generateImportsByFolder(String folder) {
		List<String> items = new ArrayList<>();
		String root = folder;
		readRecursively(items, root, folder);
		
		return items;
	}
	
	private static void readRecursively(List<String> items, String root, String folder) {
		File file = new File(folder);
		if(file.isFile()) {
			return;
		}
		
		String[] subs = file.list();
		if(subs == null) {
			return;
		}
	
		Set<String> checker = new HashSet<>();
		for(String shortPath : subs) {
			if(shortPath.endsWith(".class")) {
				if(!checker.contains(folder)) {
					checker.add(folder);
					String packageName = folder.replace(root, "").replaceAll("[/\\\\]", ".").replaceAll("^\\.", "");
					if(EmptyUtil.isNullOrEmpty(packageName)) {
    					continue;
    				}
	    			String item = "import " + packageName + ".*;";
	    			items.add(item);
				}
			}
			readRecursively(items, root, StrUtil.useSeparator(folder, shortPath));
		}
	}
}
