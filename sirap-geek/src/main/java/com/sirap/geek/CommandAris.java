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

public class CommandAris extends CommandBase {
	private static final String KEY_EXECUTE_JAVACODE = "ar";
	private static final String KEY_JRE_LOCATION = "jre";
	private static final String KEY_PRINT_CLASS = "(cl|class)";

	public boolean handle() {
		
		singleParam = parseParam(KEY_EXECUTE_JAVACODE + "\\s+(.+)");
		if(singleParam != null) {
			boolean keepGeneratedFiles = g().isYes("aris.keep");
			Object toKeep = OptionUtil.readObject(options, "k");
			if(toKeep instanceof Boolean) {
				keepGeneratedFiles = (Boolean)toKeep;
			}
			List<String> classPaths = g().getUserValuesByKeyword("aris.path.");
			String classpath = StrUtil.connect(classPaths, File.pathSeparator);
			List<String> autoPackages = getAutoIncludedPackageNames();
			String arisPlace = g().getUserValueOf("aris.place", System.getProperty("user.home"));
			boolean toPrintCommand = OptionUtil.readBoolean(options, "p", false);
			ArisExecutor instance = ArisExecutor.g().setToPrintCommand(toPrintCommand).setArisPlace(arisPlace);
			if(!EmptyUtil.isNullOrEmpty(autoPackages)) {
				instance.setAutoIncludedPackageNames(autoPackages);
			}
			File file = parseFile(singleParam);
			if(file != null ) {
				List<String> javacodes = IOUtil.readFileIntoList(file.getAbsolutePath(), g().getCharsetInUse());
				if(StrUtil.endsWith(singleParam, Konstants.SUFFIX_JAVA)) {
					export(instance.executeJavaFileStyle(javacodes, classpath, keepGeneratedFiles));
				} else {
					export(instance.executeTextFileStyle(javacodes, classpath, keepGeneratedFiles));
				}
			} else {
				export(instance.executeOnelineStyle(singleParam, classpath, keepGeneratedFiles));
			}
			
			return true;
		}
		
		if(is(KEY_JRE_LOCATION)) {
			export(ArisUtil.getRuntimeLibraryLocation());
			
			return true;
		}

		regex = KEY_PRINT_CLASS + "\\s+([a-zA-Z\\d_\\.\\$/\\\\]+)(|\\*)";
		params = parseParams(regex);
		if(params != null) {
			String name = params[1].replace('/', '.').replace('\\', '.').replaceAll("\\.class$", "");
			boolean showSameClassesInSamePackage = !EmptyUtil.isNullOrEmpty(params[2]);
			Class glass = ObjectUtil.forName(name);
			String sourceLocation = ArisUtil.sourceLocation(glass);
			if(showSameClassesInSamePackage) {
				String jarEntryName = name.replace('.', '/') + ".class";
				export(ArisUtil.siblingClasses(sourceLocation, jarEntryName));
			} else {
				List<String> items = ArisUtil.getClassDetail(glass);
				export(items);
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
