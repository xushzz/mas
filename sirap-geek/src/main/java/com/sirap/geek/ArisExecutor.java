package com.sirap.geek;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class ArisExecutor {

	public static final String REGEX_IMPORT = StrUtil.occupy("import\\s+(static\\s+|){0}(\\s*\\.\\s*{0})*\\.(\\*|{0})\\s*;", Konstants.REGEX_JAVA_IDENTIFIER);
	public static final String REGEX_PACKAGE = StrUtil.occupy("package\\s+(static\\s+|){0}(\\s*\\.\\s*{0})*;", Konstants.REGEX_JAVA_IDENTIFIER);
	public static final String FINAL_CLASS_NAME = "Aris";

	private List<String> autoIncludedPackageNames = StrUtil.split("java.util,java.math,java.io");
	private String arisPlace = System.getProperty("user.home");

	private String whereToGenerate;
	private List<String> manualJavacode;
	private List<String> arispathItems;
	private String publicClassName;
	private boolean isJavaFileStyle;
	private boolean toPrintCommand;
	private boolean toKeepGeneratedFiles;
	
	public static ArisExecutor g() {
		return new ArisExecutor();
	}
	
	public ArisExecutor setToPrintCommand(boolean toPrintCommand) {
		this.toPrintCommand = toPrintCommand;
		return this;
	}

	public ArisExecutor setAutoIncludedPackageNames(List<String> names) {
		Set<String> items = Sets.newConcurrentHashSet();
		items.addAll(names);
		items.addAll(autoIncludedPackageNames);
		this.autoIncludedPackageNames = Lists.newArrayList(items);
		return this;
	}
	
	public ArisExecutor setToKeepGeneratedFiles(boolean toKeepGeneratedFiles) {
		this.toKeepGeneratedFiles = toKeepGeneratedFiles;
		return this;
	}
	
	public ArisExecutor setArisPlace(String arisPlace) {
		this.arisPlace = arisPlace;
		return this;
	}
	
	public void setJavaFileStyle(boolean isJavaFileStyle) {
		this.isJavaFileStyle = isJavaFileStyle;
	}

	public List<String> executeOnelineStyle(String lineJavacode, List<String> arispathItems) {
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
		String regex = "([a-zA-Z\\d_\\.\\$]+\\.|)([a-zA-Z_$][\\da-zA-Z_$]*)(\\.{3,}|\\.class)";
		String[] params = StrUtil.parseParams(regex, remain);
		if(params != null) {
			String glass = params[0] + params[1] + Konstants.DOT_CLASS;
			items.add(StrUtil.occupy("C.list(ArisUtil.getClassDetail({0}, true));", glass));
		} else {
			String[] arr = StrUtil.parseParams("^(={1,3})(.+)", remain);
			if(arr != null) {
				int len = arr[0].length();
				String template;
				if(len == 3) {
					template = "D.pla({0});";
				} else if(len == 2) {
					template = "D.list({0});";
				} else {
					template = "D.pl({0});";
				}
				String expression = arr[1].replaceAll("[,\\.;]+$", "");
				items.add(StrUtil.occupy(template, expression));
			} else {
				items.add(remain + (remain.endsWith(";") ? "" : ";"));
			}
		}
		
		this.manualJavacode = items;
		this.arispathItems = arispathItems;
		return process();
	}
	
	public List<String> executeTextFileStyle(List<String> textJavacode, List<String> arispathItems) {
		this.manualJavacode = textJavacode;
		this.arispathItems = arispathItems;
		return process();
	}
	
	private List<String> process() {
		String finalJavaFileFullPath = saveSourceCode(generateSourceCode());
		List<String> consoleOutput = compileAndRun(finalJavaFileFullPath);
		if(!toKeepGeneratedFiles) {
			cleanGeneratedFiles();
		}
		
		return consoleOutput;
	}
	
	private String connectWithoutComments(List<String> lines) {
		StringBuffer sb = StrUtil.sb();
		String lineCommentRegex = "//.+";
		for(String line : lines) {
			sb.append(line.replaceAll(lineCommentRegex, ""));
		}
		
		String temp = sb.toString();
		String blockCommentRegex = "/\\*.*?\\*/";
		temp = temp.replaceAll(blockCommentRegex, "");
		
		return temp;
	}
	
	private List<String> generateSourceCode() {
		String allInOneLine = connectWithoutComments(manualJavacode);
		String regexClassName = "public\\s+class\\s+(" + Konstants.REGEX_JAVA_IDENTIFIER + ")";
		String existingClassName = StrUtil.findFirstMatchedItem(regexClassName, allInOneLine);

		if(existingClassName != null) {
			publicClassName = existingClassName;
		}
		
		List<String> imports = new ArrayList<>();
		
		//add runtimeImports;
		imports.addAll(genearteRuntimeImportsFromJRELibrary());
		imports.add("");
		
		//add configImports;
		imports.addAll(generateConfigImportsFromClassPath());
		imports.add("");

		List<String> sourceCode = new ArrayList<>();
		sourceCode.add("");
		if(isJavaFileStyle) {
			sourceCode.addAll(imports);
			for(String item : manualJavacode) {
				if(StrUtil.isRegexMatched(REGEX_PACKAGE, item.trim())) {
					continue;
				}
				
				sourceCode.add(item);
			}
			
			return sourceCode;
		}
		
		List<String> sentences = new ArrayList<>();
		if(existingClassName == null) {
			publicClassName = FINAL_CLASS_NAME;
			sentences.add("public class " + publicClassName + " {");
			sentences.add("");
		}
		
		String regexMain = "(public\\s+static|static\\s+public)\\s+void\\s+main\\s*\\(\\s*String[^\\(\\)]+\\)";
		boolean hasMainMethodAlready = StrUtil.isRegexFound(regexMain, allInOneLine);
		if(!hasMainMethodAlready) {
			sentences.add("\tpublic static void main(String[] args) throws Throwable {");
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
		if(existingClassName == null) {
			sentences.add("}");
		}

		sourceCode.addAll(imports);
		sourceCode.addAll(sentences);
		
		return sourceCode;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> genearteRuntimeImportsFromJRELibrary() {
		if(EmptyUtil.isNullOrEmpty(autoIncludedPackageNames)) {
			return Collections.EMPTY_LIST;
		}
		String runtimeJarPath = FileUtil.bySeparator(ArisUtil.getRuntimeLibraryLocation(), "rt.jar");
		return ArisUtil.generateImportsByJarFile(runtimeJarPath, autoIncludedPackageNames);
	}
	
	private List<String> generateConfigImportsFromClassPath() {
		List<String> imports = new ArrayList<>();
		
		if(EmptyUtil.isNullOrEmpty(arispathItems)) {
			return imports;
		}
		for(String item : arispathItems) {
			if(!FileUtil.exists(item)) {
				XXXUtil.alert("Arispath item not found: {0}", item);
			}
			
			if(StrUtil.endsWith(item, ".jar")) {
				imports.addAll(ArisUtil.generateImportsByJarFile(item));
			} else {
				imports.addAll(ArisUtil.generateImportsByFolder(item));
			}
		}
		
		return imports;
	}
	
	private void cleanGeneratedFiles() {
		FileUtil.remove(whereToGenerate);
	}
	
	private List<String> compileAndRun(String finalJavaFileFullPath) {
		List<String> consoleOutput = new ArrayList<>();
		String javacCommand = "javac -Xlint:none -cp \"{0}\" {1}";
		String arispathString = StrUtil.connect(arispathItems, File.pathSeparator);
		javacCommand = StrUtil.occupy(javacCommand, arispathString, finalJavaFileFullPath);
		if(toPrintCommand) {
			consoleOutput.add("command for javac: \n" + javacCommand);
			consoleOutput.add("");
		}
		List<String> result = PanaceaBox.executeAndRead(javacCommand);
		if(!EmptyUtil.isNullOrEmpty(result)) {
			toKeepGeneratedFiles = true;
			consoleOutput.add("result for javac:");
			consoleOutput.addAll(result);
		}
		
		String classFilepath = finalJavaFileFullPath.replaceAll("\\.java$", Konstants.DOT_CLASS);
		if(!FileUtil.exists(classFilepath)) {
			return consoleOutput;
		}
		
		String javaCommand = "java -cp \"{0}\" {1}";
		String classpath = StrUtil.useDelimiter(File.pathSeparator, whereToGenerate, arispathString);
		
		javaCommand = StrUtil.occupy(javaCommand, classpath, publicClassName);
		if(toPrintCommand) {
			consoleOutput.add("command for java: \n" + javaCommand);
			consoleOutput.add("");
		}
		consoleOutput.addAll(PanaceaBox.executeAndRead(javaCommand));
		
		return consoleOutput;
	}
	
	private String saveSourceCode(List<String> sourceCode) {
		String folderName = DateUtil.timestamp() + "_" + RandomUtil.LETTERS(1) + RandomUtil.digits(2);
		whereToGenerate = FileUtil.bySeparator(arisPlace, "aris", folderName);
		File target = new File(whereToGenerate);
		target.mkdirs();
		String finalJavaFileFullPath = FileUtil.bySeparator(whereToGenerate, publicClassName + ".java");
		
		try(BufferedWriter thomas = new BufferedWriter(new FileWriter(finalJavaFileFullPath))) {
			for(String item : sourceCode) {
				thomas.write(item);
				thomas.newLine();
			}
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		
		return finalJavaFileFullPath;
	}
}
