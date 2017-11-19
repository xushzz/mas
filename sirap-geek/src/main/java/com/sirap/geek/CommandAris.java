package com.sirap.geek;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.geek.util.GeekExtractors;

public class CommandAris extends CommandBase {
	private static final String KEY_EXECUTE_JAVACODE = "ar";
	private static final String KEY_JRE_LOCATION = "jre";
	private static final String KEY_PRINT_CLASS = "(cl|class)";

	@SuppressWarnings("rawtypes")
	public boolean handle() {
		
		solo = parseSoloParam(KEY_EXECUTE_JAVACODE + "\\s+(.+)");
		if(solo != null) {
			boolean keepGeneratedFiles = OptionUtil.readBooleanPRI(options, "k", g().isYes("aris.keep"));
			List<String> classPaths = g().getUserValuesByKeyword("aris.path.");
			String classpath = StrUtil.connect(classPaths, File.pathSeparator);
			List<String> autoPackages = getAutoIncludedPackageNames();
			String arisPlace = g().getUserValueOf("aris.place", System.getProperty("user.home"));
			boolean toPrintCommand = OptionUtil.readBooleanPRI(options, "p", false);
			ArisExecutor instance = ArisExecutor.g().setToPrintCommand(toPrintCommand).setArisPlace(arisPlace).setToKeepGeneratedFiles(keepGeneratedFiles);
			if(!EmptyUtil.isNullOrEmpty(autoPackages)) {
				instance.setAutoIncludedPackageNames(autoPackages);
			}
			File file = parseFile(solo);
			if(file != null ) {
				List<String> javacodes = IOUtil.readFileIntoList(file.getAbsolutePath());
				if(StrUtil.endsWith(solo, Konstants.SUFFIX_JAVA)) {
					export(instance.executeJavaFileStyle(javacodes, classpath));
				} else {
					export(instance.executeTextFileStyle(javacodes, classpath));
				}
			} else {
				export(instance.executeOnelineStyle(solo, classpath));
			}
			
			return true;
		}
		
		if(is(KEY_JRE_LOCATION)) {
			export(ArisUtil.getRuntimeLibraryLocation());
			
			return true;
		}

		regex = KEY_PRINT_CLASS + "\\s+([a-zA-Z\\d_\\.\\$/\\\\]+)(|\\*)(|\\s\\S+)";
		params = parseParams(regex);
		if(params != null) {
			String name = params[1].replace('/', '.').replace('\\', '.').replaceAll("\\.class$", "");
			boolean showSameClassesInSamePackage = !EmptyUtil.isNullOrEmpty(params[2]);
			String mexCriteria = params[3];
			Class glass = ObjectUtil.forName(name);
			String sourceLocation = ArisUtil.sourceLocation(glass);
			List<String> items = null;
			if(showSameClassesInSamePackage) {
				String jarEntryName = name.replace('.', '/') + ".class";
				items = ArisUtil.siblingClasses(sourceLocation, jarEntryName);
				export(items);
			} else {
				String method = OptionUtil.readString(options, "m");
				if(!EmptyUtil.isNullOrEmpty(method)) {
					items = GeekExtractors.fetchJDK7Api(name.replace('.', '/'), method);
					export(items);
				} else {
					items = ArisUtil.getClassDetail(glass, isDebug());
					export2(items, mexCriteria);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public List<String> getAutoIncludedPackageNames() {
		List<String> desired = Lists.newArrayList();
		List<String> items = g().getUserValuesByKeyword("aris.auto.");
		for(String item : items) {
			List<String> parentAndChildren = StrUtil.split(item);
			if(parentAndChildren.isEmpty()) {
				continue;
			}
			String parent = parentAndChildren.get(0);
			if(parentAndChildren.size() == 1) {
				desired.add(parent);
			} else {
				for(int i = 1 ; i < parentAndChildren.size(); i++) {
					desired.add(parent + "." + parentAndChildren.get(i));			
				}
			}
		}
		
		return desired;
	}
}
