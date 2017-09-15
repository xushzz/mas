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
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class ArisExecutor {
	
	public static final String FINAL_CLASS_NAME = "ARIS";
	private static final String AUTO_INCLUDE_IMPORTS = "java.io,java.math,java.text,java.util,java.net";

	public static ArisExecutor g = new ArisExecutor();

	private List<String> sourceCode;
	private String finalJavaFileFullPath;
	private String whereToGenerate;
	private List<String> consoleOutput;
	private List<String> manualJavacode;
	private String configClasspath;
	
	private void init() {
		finalJavaFileFullPath = null;
		whereToGenerate = null;
	}
	
	public List<String> execute(List<String> manualJavacode, String configClasspath, boolean keepGeneratedFiles) {
		init();

		this.manualJavacode = manualJavacode;
		this.configClasspath = configClasspath;
		
		generateSourceCode();		
		saveSourceCode();
		compileAndRun();
		if(!keepGeneratedFiles) {
			cleanGeneratedFiles();
		}
		
		return consoleOutput;
	}
	
	private void generateSourceCode() {
		sourceCode = new ArrayList<>();
		sourceCode.add("");
		
		//add runtimeImports;
		sourceCode.addAll(genearteRuntimeImportsFromJRELibrary(AUTO_INCLUDE_IMPORTS));
		sourceCode.add("");
		
		//add configImports;
		sourceCode.addAll(generateConfigImportsFromClassPath());
		//add manualImports;
		sourceCode.add("");
		
		sourceCode.add("public class " + FINAL_CLASS_NAME + " {");
		sourceCode.add("");
		sourceCode.add("\tpublic static void main(String[] args) {");
		//add manual java code;
		for(String item : manualJavacode) {
			sourceCode.add("\t\t" + item + (item.endsWith(";") ? "":";"));
		}
		sourceCode.add("\t}");
		sourceCode.add("}");
	}
	
	private List<String> genearteRuntimeImportsFromJRELibrary(String desiredPackages) {
		String runtimeJarPath = PanaceaBox.runtimeJarLocation();
		return generateImportsByJarFile(runtimeJarPath, StrUtil.split(desiredPackages));
	}
	
	private List<String> generateConfigImportsFromClassPath() {
		List<String> imports = new ArrayList<>();
		
		if(EmptyUtil.isNullOrEmpty(configClasspath)) {
			return imports;
		}
		
		List<String> items = StrUtil.split(configClasspath, ';');
		for(String item : items) {
			if(EmptyUtil.isNullOrEmptyOrBlank(item)) {
				continue;
			}
			
			if(StrUtil.endsWith(item, ".jar")) {
				imports.addAll(generateImportsByJarFile(item));
			} else {
				imports.addAll(generateImportsByFolder(item));
			}
		}
		
		return imports;
	}
	
	private void cleanGeneratedFiles() {
		FileUtil.removeEntireFolder(whereToGenerate);
	}
	
	private void compileAndRun() {
		consoleOutput = new ArrayList<>();
		String javacCommand = "javac -cp \"{0}\" {1}";
		javacCommand = StrUtil.occupy(javacCommand, configClasspath, finalJavaFileFullPath);
		List<String> errors = PanaceaBox.executeAndRead(javacCommand);
		consoleOutput.addAll(errors);
		if(!EmptyUtil.isNullOrEmpty(errors)) {
			C.pl("Compile error with command: " + javacCommand);
			return;
		}
		
		String javaCommand = "java -cp \"{0}\" {1}";
		String classpath = StrUtil.useDelimiter(";", whereToGenerate, configClasspath);
		javaCommand = StrUtil.occupy(javaCommand, classpath, FINAL_CLASS_NAME);
		consoleOutput.addAll(PanaceaBox.executeAndRead(javaCommand));
	}
	
	private void saveSourceCode() {
		String folderName = DateUtil.timestamp() + "_" + RandomUtil.letters(1, true) + RandomUtil.digits(2);
		whereToGenerate = StrUtil.useSeparator(System.getProperty("user.home"), "aris", folderName);
		File target = new File(whereToGenerate);
		target.mkdirs();
		
		finalJavaFileFullPath = StrUtil.useSeparator(whereToGenerate, FINAL_CLASS_NAME + ".java");
		
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
	    				String fullPackageName = ma.group(1).replace('/', '.');
	    				if(!EmptyUtil.isNullOrEmpty(desiredPkgNames) && !StrUtil.startsWith(fullPackageName, desiredPkgNames)) {
	    					continue;
	    				}
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
