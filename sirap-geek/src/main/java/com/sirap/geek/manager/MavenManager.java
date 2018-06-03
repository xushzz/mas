package com.sirap.geek.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.basic.util.XmlUtil;

public class MavenManager {

	private static MavenManager instance;
	
	public static final String BUILD_SUCCESS = "BUILD SUCCESS";
	public static final String BUILD_INFO = "[INFO]";
	
	public static MavenManager g() {
		
		if(instance == null) {
			instance = new MavenManager();
		}
		
		return instance;
	}
	
	public List<String> getMavenInfo() {
		List<String> items = new ArrayList<>();
		String home = getMavenHome();
		items.add("Maven home : " + home);
		items.add("Maven repo : " + getMavenRepo(home));
		items.add("Super pom  : " + getSuperPom(home));
		
		return items;
	}
	
	private String getMavenHome() {
		String mavenBin = SatoUtil.kidOfJavaLibPath("maven");
		XXXUtil.nullCheck(mavenBin, "mavenBin");
		String temp = mavenBin.replaceAll("\\\\bin", "");
		
		return temp;
	}
	
	public String getSuperPom(String home) {
		String regex = "(\\d\\.\\d\\.\\d{1,2})";
		String version = StrUtil.findFirstMatchedItem(regex, home);
		String jarName = StrUtil.occupy("maven-model-builder-{0}.jar", version);
		String jarPath = StrUtil.useDelimiter(File.separator, home, "lib", jarName);
		String pomPath = jarPath.replace('\\', '/') + "!/org/apache/maven/model/pom-4.0.0.xml";
		//C:\apache-maven-3.3.9\lib\maven-model-builder-3.3.9.jar
		//C:/apache-maven-3.3.9/lib/maven-model-builder-3.3.9.jar!/org/apache/maven/model/pom-4.0.0.xml
		
		return pomPath;
	}
	
	private String getMavenRepo(String home) {
		if(home == null) {
			return null;
		}
		
		File satoshi = new File(home, "conf\\settings.xml");
				
		if(!satoshi.exists()) {
			XXXUtil.alert("File non exists: " + satoshi);
		}
		
		String repo = XmlUtil.readValueFromFile(satoshi.getAbsolutePath(), "localRepository");

		return repo;
	}
	
	public List<String> getDependencies(String mvnCommand) {
		
		String newCommand = "cmd /c " + mvnCommand;
		C.pl2("building... " + mvnCommand);
		
		List<String> buildResult = PanaceaBox.executeAndRead(newCommand);
		List<String> deps = validateAndParseDeps(getMavenRepo(getMavenHome()), buildResult);
		
		return deps;
	}
	
	/****
	 * 
	 * @param items result of "mvn dependency:list -fD:\Github\SIRAP\mas\sirap-common\pom.xml"
	 * @return
	 */
	public static List<String> validateAndParseDeps(String repoWithoutSeparator, List<String> buildResult) {
		String oneline = StrUtil.connect(buildResult);
		
		String regex = "The following files have been resolved:(.+?)" + BUILD_SUCCESS;
		String temp = StrUtil.findFirstMatchedItem(regex, oneline);
		if(temp == null) {
			throw new MexException("not a valid build result for parsing dependpencies.");
		}
		
		List<String> items = StrUtil.split(temp, BUILD_INFO);
		Set<String> deps = new TreeSet<String>();
		for(String item : items) {
			String dep = parseDetailAndConstructPath(repoWithoutSeparator, item);
			if(dep != null) {
				deps.add(dep);
			}
		}
		
		return Lists.newArrayList(deps);
	}
	
	/***
	 * com.itextpdf:itext-asian:jar:5.2.0:compile
	 * net.sf.json-lib:json-lib:jar:jdk15:2.2.2:compile
	 * @param repo
	 * @param source
	 * @return
	 */
	public static String parseDetailAndConstructPath(String repo, String source) {
		String[] params = source.split(":");
		int size = params.length;
		if(size < 5) {
			return null;
		}
		
		String groupId = params[0];
		String artifactId = params[1];
		String type = params[2];
		boolean hasClassifier = size > 5;
		String version = hasClassifier ? params[4]:params[3];
		String classifier = hasClassifier ? params[3]: null;
		
		String guard = "/";
		String lady = "-";
		
		StringBuffer sb = new StringBuffer();
		sb.append(groupId.replace(".", guard));
		sb.append(guard).append(artifactId);
		sb.append(guard).append(version);
		sb.append(guard).append(artifactId).append(lady).append(version);
		if(hasClassifier) {
			sb.append(lady).append(classifier);
		}
		sb.append(".").append(type);
		
		return StrUtil.useSeparator(repo, sb);
	}
}
