package com.sirap.geek.domain;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.geek.manager.MavenBuilder;

import lombok.Data;

public class MavenDep extends MavenItem {
	private String groupId;
	private String artifactId;
	private String version;
	private String type = "jar";
	private String classifier;
	private String scope;
	private List<MavenExclusion> exclusions;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public List<MavenExclusion> getExclusions() {
		return exclusions;
	}

	public void setExclusions(List<MavenExclusion> exclusions) {
		this.exclusions = exclusions;
	}

	public String keyOf() {
		String key = getGroupId() + " && " + getArtifactId();
		return key;
	}
	
	public String gav() {
		String key = getGroupId() + " && " + getArtifactId() + " => " + getVersion();
		return key;
	}
	
	public void read(Object mars) {
//		D.pl("XXXXXXXXX");
//		D.pjsp(mars);
//		D.pl("YYYYY");
		groupId = valueOf("groupId", mars);
		artifactId = valueOf("artifactId", mars);
		version = valueOf("version", mars);
		String temp = valueOf("type", mars);
		if(temp != null) {
			type = temp;
		}
		scope = valueOf("scope", mars);
		Object moon = objOf("exclusions", mars);
//		D.pjsp(moon);
		if(moon != null) {
			exclusions = Lists.newArrayList();
			List items = Lists.newArrayList();
			if(List.class.isInstance(moon)) {
				items.addAll((List)moon);
			} else if(Map.class.isInstance(moon)) {
				items.add(moon);
			}
			
			for(Object item : items) {
				MavenExclusion dep = new MavenExclusion();
				dep.read(objOf("exclusion", item));
				exclusions.add(dep);
			}
		}
	}

	public boolean isImport() {
		return StrUtil.equals(scope, "import");
	}
	
	/***
	 * "scope": "import"
	 * #
	 * #1
	 * @return
	 */
	public void explodeImport(List<MavenDep> holder, String repoPath, String levelInfo, int orderNumber) {
		if(!isImport()) {
			return;
		}
		
		String pomPath = path(repoPath);
		MavenPom pom = MavenBuilder.parsePom(pomPath, repoPath, "KKK", 2);
		List<MavenDep> kids = pom.getManagements();
		holder.addAll(kids);
		
		String temp = levelInfo + orderNumber;
		String info = StrUtil.occupy("{0}{1} has {2} {3}:", levelInfo, orderNumber, kids.size(), "kids");
		C.pl(pomPath);
		C.pl(info);
//		D.pjsp(kids);
		int count = 1;
		for(MavenDep kid : kids) {
			kid.explodeImport(holder, repoPath, temp + ".", count++);
		}
	}
	
	public void extendDeps(String repoPath, Map<String, String> props) {
		if(!isImport()) {
			props.put(keyOf(), version);
			return;
		}
		
		String pomPath = path(repoPath);
		D.pl("MMMM: " + pomPath);
		MavenPom mp = new MavenPom();
		mp.setRepoPath(repoPath);
//		mp.process(repoPath, pomPath, props);
		
		List<MavenDep> kids = mp.getDependencies();
		for(MavenDep kid : kids) {
			kid.extendDeps(repoPath, props);
		}
	}
	
	public String path(String repoPath) {
		String fullpath = StrUtil.useSeparator(repoPath, path());
		return fullpath;
	}
	
	public String path() {
		String guard = "/";
		String lady = "-";
		
		StringBuffer sb = new StringBuffer();
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
	
	public void applyExpression(MavenPom pom) {
		Field[] arr = getClass().getDeclaredFields();
		for(Field fd : arr) {
			if(!String.class.equals(fd.getType())) {
				continue;
			}
			fd.setAccessible(true);
			try {
				String facevalue = fd.get(this) + "";
				String value = useDollars(facevalue, pom);
//				D.pla("XXXX", fd.getName(), facevalue, value);
				if(!StrUtil.equals(facevalue, value)) {
					fd.set(this, value);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String toJson() {
		return D.jsp(this, this.getClass());
	}
	
	@Override
	public List toList() {
		List item = Lists.newArrayList();
		item.add(groupId);
		item.add(artifactId);
		item.add(version);
		
		return item;
	}

	@Override
	public String toPrint() {
		return gav();
	}
}