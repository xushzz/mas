package com.sirap.geek;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class ArisExecutor {
	
	public static final String FINAL_JAVA_FILE_SHORT_NAME = "ARIS";
	public static final String TEMPLATE_FILEPATH = "/" + FINAL_JAVA_FILE_SHORT_NAME + ".java.txt";
	public static final String FLAG_AUTO_IMPORTS = "//${auto.imports}";
	public static final String FLAG_MANUAL_IMPORTS = "//${manual.imports}";
	public static final String FLAG_MANUAL_JAVACODE = "//${manual.javacode}";

	public static ArisExecutor g = new ArisExecutor();

	private List<String> sourceCode;
	private String finalJavaFileFullPath;
	private String whereToGenerate;
	private List<String> consoleOutput = new ArrayList<>();
	private List<String> autoImports;
	private List<String> manualJavacode;
	private String configClasspath;
	
	private void init() {
		sourceCode = IOUtil.readResourceIntoList(TEMPLATE_FILEPATH);
		finalJavaFileFullPath = null;
		whereToGenerate = null;
		consoleOutput = new ArrayList<>();
		autoImports = new ArrayList<>();
	}
	
	public List<String> execute(List<String> manualJavacode, String configClasspath, boolean keepGeneratedFiles) {
		init();

		this.manualJavacode = manualJavacode;
		this.configClasspath = configClasspath;
		
		generateConfigImportsFromClassPath();
		injectImportsAndJavacode();
		saveSourceCode();
		compileAndRun();
		if(!keepGeneratedFiles) {
			cleanGeneratedFiles();
		}
		
		return consoleOutput;
	}
	
	private void generateConfigImportsFromClassPath() {
		if(EmptyUtil.isNullOrEmpty(configClasspath)) {
			return;
		}
		
		List<String> items = StrUtil.split(configClasspath, ';');
		for(String item : items) {
			if(EmptyUtil.isNullOrEmptyOrBlank(item)) {
				continue;
			}
			
			if(StrUtil.endsWith(item, ".jar")) {
				autoImports.addAll(generateImportsByJarFile(item));
			} else {
				autoImports.addAll(generateImportsByFolder(item));
			}
		}
	}
	
	private void cleanGeneratedFiles() {
		FileUtil.removeEntireFolder(whereToGenerate);
	}
	
	private void compileAndRun() {
		String javacCommand = "javac -cp \"{0}\" {1}";
		javacCommand = StrUtil.occupy(javacCommand, configClasspath, finalJavaFileFullPath);
//		C.pl(javacCommand);
		List<String> errors = PanaceaBox.executeAndRead(javacCommand);
		consoleOutput.addAll(errors);
		if(!EmptyUtil.isNullOrEmpty(errors)) {
			C.pl("Compile error with command: " + javacCommand);
			return;
		}
		
		String javaCommand = "java -cp \"{0}\" {1}";
		String classpath = StrUtil.useDelimiter(";", whereToGenerate, configClasspath);
		javaCommand = StrUtil.occupy(javaCommand, classpath, FINAL_JAVA_FILE_SHORT_NAME);
//		C.pl(javaCommand);
		consoleOutput.addAll(PanaceaBox.executeAndRead(javaCommand));
	}
	
	private void injectImportsAndJavacode() {
		sourceCode = new ArrayList<>();
		List<String> template = IOUtil.readResourceIntoList(TEMPLATE_FILEPATH);
		for(String line : template) {
			if(line.contains(FLAG_AUTO_IMPORTS) && !EmptyUtil.isNullOrEmpty(autoImports)) {
				for(String item : autoImports) {
					sourceCode.add(item);
				}
				sourceCode.add("");
				continue;
			}
			
			if(line.contains(FLAG_MANUAL_JAVACODE) && !EmptyUtil.isNullOrEmpty(manualJavacode)) {
				for(String item : manualJavacode) {
					sourceCode.add(item + (item.endsWith(";") ? "":";"));
				}
				sourceCode.add("");

				continue;
			}
			
			sourceCode.add(line);
		}
	}
	
	private void saveSourceCode() {
		String folderName = DateUtil.timestamp() + "_" + RandomUtil.letters(1, true) + RandomUtil.digits(2);
		whereToGenerate = StrUtil.useSeparator(System.getProperty("user.home"), "aris", folderName);
		File target = new File(whereToGenerate);
		target.mkdirs();
		
		finalJavaFileFullPath = StrUtil.useSeparator(whereToGenerate, FINAL_JAVA_FILE_SHORT_NAME + ".java");
		
		try(BufferedWriter thomas = new BufferedWriter(new FileWriter(finalJavaFileFullPath))) {
			for(String item : sourceCode) {
				thomas.write(item);
				thomas.newLine();
			}
		} catch (Exception ex) {
			throw new MexException(ex);
		}
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
					String packageName = folder.replace(root, "").replace(File.separatorChar, '.').replaceAll("^\\.", "");
	    			String item = "import " + packageName + ".*;";
	    			items.add(item);
				}
			}
			readRecursively(items, root, StrUtil.useSeparator(folder, shortPath));
		}
	}
	
	public static List<String> generateImportsByJarFile(String jarFilepath) {
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
		    			String item = "import " + ma.group(1).replace('/', '.') + ".*;";
		    			imports.add(item);
	    			}	    			
	    		}
		    }
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
		
		return imports;
	}
}
