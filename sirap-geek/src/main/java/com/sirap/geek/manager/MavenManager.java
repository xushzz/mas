package com.sirap.geek.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.MavenUtils;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;

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
		items.add("Maven home : " + MavenUtils.getHome());
		items.add("Maven repo : " + MavenUtils.getRepo());
		items.add("Settings   : " + MavenUtils.getSettings());
		items.add("Super pom  : " + MavenUtils.getSuperPom());
		
		return items;
	}
	
	public List<String> getDependencies(String mvnCommand) {
		String newCommand = "cmd /c " + mvnCommand;
		C.pl2("building... " + mvnCommand);
		
		List<String> buildResult = PanaceaBox.executeAndRead(newCommand);
		String repo = MavenUtils.getRepo();
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
	
	/***
	 * #1 org.springframework.spring-beans.3.0.5.RELEASE
	 *    org/springframework/spring-beans.3.0.5.RELEASE
	 * #2 org.springframework.spring-beans/3.0.5.RELEASE
	 *    org/springframework/spring-beans/3.0.5.RELEASE
	 * @return
	 */
	public static String toNormalPath(String origin) {
		int len = origin.length();
		char[] dest = new char[len];
		for(int k = 0; k < len; k++) {
			char current = origin.charAt(k);
			int left = k - 1, right = k + 1;
			if(current != '.' || left >= 0 && StrUtil.isDigit(origin.charAt(left)) || right <= len - 1 && StrUtil.isDigit(origin.charAt(right))) {
				dest[k] = current;
				continue;
			}
			
			dest[k] = '/';
		}
		
		return new String(dest);
	}
}
