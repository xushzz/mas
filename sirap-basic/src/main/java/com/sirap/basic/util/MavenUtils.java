package com.sirap.basic.util;

import java.io.File;

import com.sirap.basic.json.MistUtil;

public class MavenUtils {

	public static String getHome() {
		String mavenBin = SatoUtil.kidOfJavaLibPath("maven");
		XXXUtil.nullCheck(mavenBin, "mavenBin");
		
		return mavenBin.replaceAll("\\\\bin", "");
	}
	
	public static String getSettings() {
		String home = getHome();
		File kid = new File(home, "conf\\settings.xml");
	
		return kid.getAbsolutePath();
	}
	
	public static String getRepo() {
		String settings = getSettings();
//		String repo = XmlUtil.readValueFromFile(settings, ".localRepository") + "";
		String repo = MistUtil.ofXmlFile(settings).valueOf(".localRepository") + "";
		
		return repo;
	}
	
	public static String getSuperPom() {
		String home = getHome();
		String regex = "(\\d\\.\\d\\.\\d{1,2})";
		String version = StrUtil.findFirstMatchedItem(regex, home);
		String jarName = StrUtil.occupy("maven-model-builder-{0}.jar", version);
		String jarPath = StrUtil.useDelimiter(File.separator, home, "lib", jarName);
		String pomPath = jarPath.replace('\\', '/') + "!/org/apache/maven/model/pom-4.0.0.xml";
		
		return pomPath;
	}
}
