package com.sirap.geek.domain;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.json.MistUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MavenItem extends MexItem {
	
	public void read(Object source) {
		return;
	}
	
	public void applyExpression(List<MavenDep> deps, MavenPom pom) {
		for(MavenDep dep : deps) {
			dep.applyExpression(pom);
		}
	}

	public String valueOf(String key, Object mapOrList) {
//		Object obj = XmlUtil.valueOf(mapOrList, key);
		Object obj = MistUtil.ofMapOrList(mapOrList).valueOf(key);
		if(obj == null) {
			return null;
		}
		
		return obj.toString();
	}
	
	public Object objOf(String key, Object mapOrList) {
//		return XmlUtil.valueOf(mapOrList, key);
		return MistUtil.ofMapOrList(mapOrList).valueOf(key);
	}
	
	public List listOf(String key, Object mapOrList) {
//		Object mix = XmlUtil.valueOf(mapOrList, key);
		Object mix = MistUtil.ofMapOrList(mapOrList).valueOf(key);
		if(mix == null) {
			return null;
		}
		
		if(mix instanceof List) {
			return (List)mix;
		}
		
		if(Map.class.isInstance(mix)) {
			List list = Lists.newArrayList(mix);
			return list;
		}
		
		XXXUtil.alert();
		return null;
	}
	//comeback
	public List mapOf(String key, Map<String, Object> mars) {
//		Object mix = XmlUtil.valueOf(mars, key);
		Object mix = MistUtil.ofMapOrList(mars).valueOf(key);
		if(mix instanceof List) {
			return (List)mix;
		}
		
		if(Map.class.isInstance(mix)) {
			List list = Lists.newArrayList(mix);
			return list;
		}
		
		XXXUtil.alert();
		return null;
	}
	
	public String removeCommentBuildProfiles(String source) {
		String cake = source;
		cake = HtmlUtil.removeComment(cake);
		cake = cake.replaceAll("<build>.*?</build>", "");
		cake = cake.replaceAll("<profiles>.*?</profiles>", "");
		cake = cake.replaceAll("<reporting>.*?</reporting>", "");
		return cake;
	}
	
	public String removeSection(String source, String name) {
		String temp = source;
		String regex = "<{0}>.*?</{0}>";
		temp = temp.replaceAll(StrUtil.occupy(regex, name), "");
		
		return temp;
	}
	
	public List<String[]> readElements(String source) {
		String regex = "<\\s*(.+?)\\s*>(.*?)</\\s*(.+?)\\s*>";

		List<String[]> kvs = Lists.newArrayList();
		Matcher ma = StrUtil.createMatcher(regex, source);
		while(ma.find()) {
			String key = ma.group(1);
			String value = ma.group(2);
			
			kvs.add(new String[]{key, value});
		}
		
		return kvs;
	}
	
	public String[] readElement(String source, String elementName) {
		String template = "<\\s*{0}\\s*>(.*?)</\\s*{0}\\s*>";
		String regex = StrUtil.occupy(template, elementName);
		StringBuffer sb = sb();

		Matcher ma = StrUtil.createMatcher(regex, source);
		String value = null;
		if(ma.find()) {
			value = ma.group(1).trim();
			ma.appendReplacement(sb, "");
		}
		ma.appendTail(sb);
		if(!EmptyUtil.isNull(value)) {
			return new String[]{sb.toString(), value};
		}
		
		template = "<\\s*{0}\\s*/>";
		regex = StrUtil.occupy(template, elementName);
		sb.setLength(0);

		ma = StrUtil.createMatcher(regex, source);
		value = null;
		if(ma.find()) {
			value = ma.group(1).trim();
			ma.appendReplacement(sb, "");
		}
		ma.appendTail(sb);
		if(!EmptyUtil.isNull(value)) {
			return new String[]{sb.toString(), value};
		}
		
		return null;
	}
	
	public String readElementValue(String source, String elementName) {
		String template = "<\\s*{0}\\s*>(.*?)</\\s*{0}\\s*>";
		String regex = StrUtil.occupy(template, elementName);

		Matcher ma = StrUtil.createMatcher(regex, source);
		if(ma.find()) {
			String value = ma.group(1);
			
			return value.trim();
		}
		
		return null;
	}
	
	public List<String> readElementValues(String source, String elementName) {
		String template = "<\\s*{0}\\s*>(.*?)</\\s*{0}\\s*>";
		String regex = StrUtil.occupy(template, elementName);
		List<String> vs = Lists.newArrayList();
		Matcher ma = StrUtil.createMatcher(regex, source);
		while(ma.find()) {
			String value = ma.group(1);
			vs.add(value.trim());
		}
		
		return vs;
	}
	
	public String keyOf(String dollarExpression) {
		String regex = "\\$\\{(.+?)\\}";
		String param = StrUtil.findFirstMatchedItem(regex, dollarExpression);
		return param;
	}
	

	
	/***
	 * https://blog.csdn.net/shmilxu/article/details/54646458
	 * ${project.version}
	 */
	public String useDollars(String facevalue, MavenPom pom) {
		String regex = "\\$\\{(.+?)\\}";
		Matcher ma = StrUtil.createMatcher(regex, facevalue);
		StringBuffer sb = sb();
//		D.sink("VVVV " + pomPath);
		while(ma.find()) {
			String key = ma.group(1);
			String dollar = dollarOf(key, pom);
			if(dollar != null) {
				ma.appendReplacement(sb, dollar);
			} else {
				D.pl("=======" + key);
				ma.appendReplacement(sb, "##" + key);
			}
		}
		
		ma.appendTail(sb);
		
		return sb.toString();
	}

	public String dollarByProp(String key, MavenPom mp) {
		String temp = mp.getProps().get(key);
		if(temp != null) {
			return temp;
		} 
		
		if(mp.getParent() != null) {
			return dollarByProp(key, mp.getParent());
		} else {
			return null;
		}
	}
	
	public String dollarByPom(String key, MavenPom pom) {
		String[] params = StrUtil.parseParams("(parent|project\\.parent)(.+?)", key);
		if(params != null) {
			String newkey = "project" + params[1];
//			D.pla("KKK", params[1],pom.getParent());
			String value = valueOf(newkey, pom.getParent().getOrigin());
			return value;
		}
		
		String newkey = key;
		if(!key.startsWith("project")) {
			newkey = "project." + key;
		}
		
//		D.pla(newkey, key, "project", StrUtil.startsWith(key, "project"));
		String value = valueOf(newkey, pom.getOrigin());
//		D.pla(key, newkey, value);
		
		return value;
//		
//		if(StrUtil.isRegexMatched("parent|project.parent", key)) {
//			Object instance = matter;
//			if(StrUtil.equals("parent", top)) {
//				//instance = ((MavenProject)matter).getParent();
//			}
//			List<String> subs = Lists.newArrayList(parts);
//			subs.remove(0);
//			return dollarByPom(key, instance);
//		} else {
//			String dollar = ObjectUtil.readFieldValue(matter, top) + "";
//			return dollar;
//		}
	}
	
	public String dollarOf(String key, MavenPom mp) {
		String temp = dollarByProp(key, mp);
		if(temp != null) {
			return temp;
		}

		String dollar = dollarByPom(key, mp);
		if(dollar != null) {
			mp.getProps().put(key, dollar);
		}

		return dollar;
	}
	
	public String pathOfPom(String repoPath, String groupId, String artifactId, String version) {
		return pathOfArtifact(repoPath, groupId, artifactId, version, null, "pom");
	}
	
	public String pathOfJar(String repoPath, String groupId, String artifactId, String version) {
		return pathOfArtifact(repoPath, groupId, artifactId, version, null, "jar");
	}
	
	public String pathOfArtifact(String repoPath, String groupId, String artifactId, String version, String type) {
		return pathOfArtifact(repoPath, groupId, artifactId, version, null, type);
	}
	
	public String pathOfArtifact(String repoPath, String groupId, String artifactId, String version, String classifier, String type) {
		String guard = "/";
		String lady = "-";
		
		StringBuffer sb = new StringBuffer();
		sb.append(repoPath).append(guard);
		sb.append(groupId.replace(".", guard));
		sb.append(guard).append(artifactId);
		sb.append(guard).append(version);
		sb.append(guard).append(artifactId).append(lady).append(version);
		if(!EmptyUtil.isNullOrEmpty(classifier)) {
			sb.append(lady).append(classifier);
		}
		sb.append(".").append(type);

		return sb.toString();
	}
	
	public String modifySection(String section, MavenPom mp) {
		String regex = "\\$\\{(.+?)\\}";
		Matcher ma = StrUtil.createMatcher(regex, section);
		StringBuffer sb = sb();
		while(ma.find()) {
			String key = ma.group(1);
			String dollar = dollarOf(key, mp);
			if(dollar != null) {
				ma.appendReplacement(sb, dollar);
			} else {
				D.pl("=======" + key);
				ma.appendReplacement(sb, "##" + key);
			}
		}
		
		ma.appendTail(sb);
		section = sb.toString();
		return section;
	}
}
