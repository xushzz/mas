package com.sirap.geek.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XmlUtil;
import com.sirap.common.framework.SimpleKonfig;

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
		items.add("Maven Home = " + getMavenHome());
		items.add("Maven Repo = " + getMavenRepo());
		
		return items;
	}
	
	private String getMavenHome() {
		String path = System.getProperty("java.library.path");
		List<String> items = StrUtil.split(path, ';');
		for(String item : items) {
			if(StrUtil.contains(item, "maven")) {
				String temp = item.replaceAll("\\\\bin", "");
				return temp;
			}
		}
		
		
		return null;
	}
	
	private String getMavenRepo() {
		String home = getMavenHome();
		if(home == null) {
			return null;
		}
		
		String fileName = home + File.separatorChar + "conf\\settings.xml";
		File file = FileUtil.getIfNormalFile(fileName);
				
		if(file == null) {
			return null;
		}
		
		String repo = XmlUtil.readValue(fileName, "localRepository");

		return repo;
	}
	
	public List<String> getDependencies(String pomFilepath) {
		String repo = SimpleKonfig.g().getUserValueOf("maven.repo");
		if(EmptyUtil.isNullOrEmpty(repo)) {
			repo = getMavenRepo();
		}
		
		String cmd = "mvn dependency:list -f" + pomFilepath;
		String newCommand = "cmd /c " + cmd;
		C.pl2("building... " + cmd);
		
		List<String> buildResult = PanaceaBox.executeAndRead(newCommand);
		List<String> deps = validateAndParseDeps(repo, buildResult);
		
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
		List<String> deps = new ArrayList<String>();
		for(String item : items) {
			String dep = parseDetailAndConstructPath(repoWithoutSeparator, item);
			if(dep != null) {
				deps.add(dep);
			}
		}
		
		return deps;
	}
	
	/***
	 * com.itextpdf:itext-asian:jar:5.2.0:compile
	 * net.sf.json-lib:json-lib:jar:jdk15:2.2.2:compile
	 * @param repo
	 * @param source
	 * @return
	 */
	public static String parseDetailAndConstructPath(String repoWithoutSeparator, String source) {
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
		
		String guard = File.separator;
		String lady = "-";
		
		StringBuffer sb = new StringBuffer(repoWithoutSeparator);
		sb.append(guard).append(groupId.replace(".", guard));
		sb.append(guard).append(artifactId);
		sb.append(guard).append(version);
		sb.append(guard).append(artifactId).append(lady).append(version);
		if(hasClassifier) {
			sb.append(lady).append(classifier);
		}
		sb.append(".").append(type);
		
		return sb.toString();
	}
}
