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

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class ArisExecutor {

	public static final String REGEX_IMPORT = StrUtil.occupy("import\\s+(static\\s+|){0}(\\s*\\.\\s*{0})*\\.(\\*|{0})\\s*;", Konstants.REGEX_JAVA_IDENTIFIER);
	public static final String REGEX_PACKAGE = StrUtil.occupy("package\\s+(static\\s+|){0}(\\s*\\.\\s*{0})*;", Konstants.REGEX_JAVA_IDENTIFIER);
	public static final String FINAL_CLASS_NAME = "ARIS";
	private static final String AUTO_INCLUDE_IMPORTS = "java.io,java.math,java.text,java.util,java.net,java.security";

	public static ArisExecutor g = new ArisExecutor();

	private List<String> sourceCode;
	private String finalJavaFileFullPath;
	private String whereToGenerate;
	private List<String> consoleOutput;
	private List<String> manualJavacode;
	private String configClasspath;
	private String publicClassName;
	private boolean isJavaFileStyle;
	private boolean toPrintCommand;
	
	private void init() {
		finalJavaFileFullPath = null;
		whereToGenerate = null;
		publicClassName = null;
	}
	
	public ArisExecutor setToPrintCommand(boolean toPrintCommand) {
		this.toPrintCommand = toPrintCommand;
		return this;
	}

	public List<String> executeOnelineStyle(String lineJavacode, String configClasspath, boolean keepGeneratedFiles) {
		Matcher ma = StrUtil.createMatcher(REGEX_IMPORT, lineJavacode);
		StringBuffer sb = new StringBuffer();
		List<String> items = new ArrayList<>();
		while(ma.find()) {
			String what = ma.group();
			items.add(what);
			ma.appendReplacement(sb, "");
		}
		
		ma.appendTail(sb);
		String remain = sb.toString().trim();
		String expression = StrUtil.findFirstMatchedItem("^=(.+)", remain);
		String line = null;
		if(expression != null) {
			expression = expression.replaceAll("[,\\.;]+$", "");
			line = StrUtil.occupy("System.out.println({0});", expression);
		} else {
			line = remain + (remain.endsWith(";") ? "" : ";");
		}
		
		items.add(line);
		
		this.manualJavacode = items;
		this.configClasspath = configClasspath;
		return process(keepGeneratedFiles);
	}
	
	public List<String> executeTextFileStyle(List<String> textJavacode, String configClasspath, boolean keepGeneratedFiles) {
		this.manualJavacode = textJavacode;
		this.configClasspath = configClasspath;
		return process(keepGeneratedFiles);
	}
	
	public List<String> executeJavaFileStyle(List<String> textJavacode, String configClasspath, boolean keepGeneratedFiles) {
		this.manualJavacode = textJavacode;
		this.configClasspath = configClasspath;
		this.isJavaFileStyle = true;
		return process(keepGeneratedFiles);
	}
	
	private List<String> process(boolean keepGeneratedFiles) {
		init();
		generateSourceCode();		
		saveSourceCode();
		compileAndRun();
		if(!keepGeneratedFiles) {
			cleanGeneratedFiles();
		}
		
		return consoleOutput;
	}
	
	private void generateSourceCode() {
		String allInOneLine = StrUtil.connect(manualJavacode);
		String regex = "public\\s+class\\s+(" + Konstants.REGEX_JAVA_IDENTIFIER + ")";
		String tempClassName = StrUtil.findFirstMatchedItem(regex, allInOneLine);

		if(tempClassName != null) {
			publicClassName = tempClassName;
		}

		sourceCode = new ArrayList<>();
		if(isJavaFileStyle) {
			for(String item : manualJavacode) {
				if(StrUtil.isRegexMatched(REGEX_PACKAGE, item.trim())) {
					continue;
				}
				
				sourceCode.add(item);
			}
			
			return;
		}
		
		List<String> imports = new ArrayList<>();
		
		//add runtimeImports;
		imports.addAll(genearteRuntimeImportsFromJRELibrary(AUTO_INCLUDE_IMPORTS));
		imports.add("");
		
		//add configImports;
		imports.addAll(generateConfigImportsFromClassPath());
		imports.add("");
		
		List<String> sentences = new ArrayList<>();
		if(tempClassName == null) {
			publicClassName = FINAL_CLASS_NAME;
			sentences.add("public class " + publicClassName + " {");
			sentences.add("");
		}
		
		String regexMain = "(public\\s+static|static\\s+public)\\s+void\\s+main\\s*\\(\\s*String[^\\(\\)]+\\)";
		boolean hasMainMethodAlready = StrUtil.isRegexFound(regexMain, allInOneLine);
		if(!hasMainMethodAlready) {
			sentences.add("\tpublic static void main(String[] args) {");
		}
		
		//add manual java code;
		for(String item : manualJavacode) {
			String temp = item.trim();
			if(StrUtil.isRegexMatched(REGEX_IMPORT, temp)) {
				imports.add(temp);
			} else if(StrUtil.isRegexMatched(REGEX_PACKAGE, temp)) {
				//do nothing
			} else {
				sentences.add("\t\t" + item);
			}
		}
		imports.add("");

		if(!hasMainMethodAlready) {
			sentences.add("\t}");
		}
		if(tempClassName == null) {
			sentences.add("}");
		}

		sourceCode = new ArrayList<>();
		sourceCode.add("");
		sourceCode.addAll(imports);
		sourceCode.addAll(sentences);
	}
	
	private List<String> genearteRuntimeImportsFromJRELibrary(String desiredPackages) {
		String runtimeJarPath = StrUtil.useSeparator(ArisUtil.getRuntimeLibraryLocation(), "rt.jar");
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
		String javacCommand = "javac -Xlint:none -cp \"{0}\" {1}";
		javacCommand = StrUtil.occupy(javacCommand, configClasspath, finalJavaFileFullPath);
		if(toPrintCommand) {
			consoleOutput.add("command for javac: \n" + javacCommand);
			consoleOutput.add("");
		}
		List<String> result = PanaceaBox.executeAndRead(javacCommand);
		if(!EmptyUtil.isNullOrEmpty(result)) {
			consoleOutput.add("result for javac: \n" + result);
			consoleOutput.add("");
		}
		
		String classFilepath = finalJavaFileFullPath.replaceAll("\\.java$", Konstants.SUFFIX_CLASS);
		if(!FileUtil.exists(classFilepath)) {
			return;
		}
		
		String javaCommand = "java -cp \"{0}\" {1}";
		String classpath = StrUtil.useDelimiter(File.pathSeparator, whereToGenerate, configClasspath);
		javaCommand = StrUtil.occupy(javaCommand, classpath, publicClassName);
		if(toPrintCommand) {
			consoleOutput.add("command for java: \n" + javaCommand);
			consoleOutput.add("");
		}
		consoleOutput.addAll(PanaceaBox.executeAndRead(javaCommand));
	}
	
	private void saveSourceCode() {
		String folderName = DateUtil.timestamp() + "_" + RandomUtil.letters(1, true) + RandomUtil.digits(2);
		whereToGenerate = StrUtil.useSeparator(System.getProperty("user.home"), "aris", folderName);
		File target = new File(whereToGenerate);
		target.mkdirs();
		finalJavaFileFullPath = StrUtil.useSeparator(whereToGenerate, publicClassName + ".java");
		
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
