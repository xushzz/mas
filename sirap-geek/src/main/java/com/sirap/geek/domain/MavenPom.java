package com.sirap.geek.domain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MistUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.geek.manager.MavenBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper=false)
@Data
@NoArgsConstructor
public class MavenPom extends MavenItem {
	private MavenPom parent;
	
	private Map<String, Object> origin;
	private Map<String, String> props = new LinkedHashMap<>();
	private List<MavenDep> managements = Lists.newArrayList();
	
	private String groupId;
	private String artifactId;
	private String version;
	
	private List<MavenDep> dependencies = Lists.newArrayList();
	
	private String repoPath;
	private String pomPath;
	private int depth;
	
	public void self(int depth) {
		String dent = StrUtil.spaces(depth * 4);
		D.pl(dent + groupId + " # " + artifactId + " " + version);
		D.pl(props.get("basedir"));
		D.pjsp(props, MavenDepManagement.class, MavenDep.class, MavenPom.class, MavenDeps.class);
	}
	
	public void process() {
//		origin = XmlUtil.xmlOfFile(pomPath);
		origin = (Map)MistUtil.ofXmlFile(pomPath);
		
		props.put("basedir", FileUtil.folderpathAndFilenameOf(pomPath)[0]);
		parseBasis();
		parseProps();
		parseManagement();
		parseDeps();
	}
	
	private List<MavenDep> applyExclusions(List<MavenDep> deps, List<MavenExclusion> clus) {
		List<MavenDep> newlist = Lists.newArrayList();
		for(MavenDep dep : deps) {
			String gid = dep.getGroupId();
			String aid = dep.getArtifactId();
			boolean fullMatched = false;
			for(MavenExclusion item : clus) {
				String cgid = item.getGroupId();
				String caid = item.getArtifactId();
				boolean flagA = StrUtil.equals("*", cgid);
				if(!flagA) {
					flagA = StrUtil.equals(gid, cgid);
				}
				boolean flagB = StrUtil.equals("*", caid);
				if(!flagB) {
					flagB = StrUtil.equals(aid, caid);
				}
				if(flagA && flagB) {
					fullMatched = true;
					break;
				}
			}
			if(!fullMatched) {
				newlist.add(dep);
			}
		}
		
		return newlist;
	}
	
	private void parseDeps() {
		Object moon = objOf("project.dependencies", origin);
//		D.pjsp(moon);
		if(moon == null) {
			return;
		}
		
		MavenDeps deps = new MavenDeps();
		deps.read(moon);
		dependencies.addAll(deps.getDependencies());
		applyExpression(dependencies, this);
	}
	
	public List<MavenDep> getParentsSelfKidsManagements() {
		List<MavenDep> mars = Lists.newArrayList();
		MavenPom pom = this;
		do {
			mars.addAll(pom.getManagements());
			pom = pom.getParent();
		} while (pom != null);
		
		mars.addAll(getKidsManagement());
		
		return mars;
	}
	
	public List<MavenDep> getParentsSelfDeps() {
		List<MavenDep> mars = Lists.newArrayList();
		MavenPom pom = this;
		do {
			List<MavenDep> deps = pom.getDependencies();
//			D.list(deps);
//			D.pjsp(depsToMap(deps).size());
			mars.addAll(deps);
			pom = pom.getParent();
		} while (pom != null);

//		mars.addAll(getKidsDeps());
		
		return mars;
	}
	
	public List<MavenDep> getKidsManagement() {
		int count = 1;
		List<MavenDep> mars = Lists.newArrayList();
		for(MavenDep dep : managements) {
			dep.explodeImport(mars, repoPath, "Sub.Mgrs#", count);
		}
		
		return mars;
	}

	public List<MavenDep> getKidsDeps() {
		List<MavenDep> aks = Lists.newArrayList();
		for(MavenDep dep : dependencies) {
			String depPath = dep.path(repoPath);
			depPath = depPath.replaceAll("\\.jar$", ".pom");
//			D.pl();
//			D.sink(depPath);
//			D.pl(dep.getExclusions());
//			D.pl();
			MavenPom pom = MavenBuilder.parsePom(depPath, repoPath, "KKK", 4);
			aks.addAll(pom.dependencies);
//			aks.addAll(applyExclusions(mars.getDependencies(), dep.getExclusions()));
		}
		
		return aks;
	}
	
	private void parseManagement() {
		Object moon = objOf("project.dependencyManagement.dependencies", origin);
		if(moon == null) {
			return;
		}
		MavenDeps deps = new MavenDeps();
		deps.read(moon);
		managements.addAll(deps.getDependencies());
		applyExpression(managements, this);
	}
	
	private void parseProps() {
		Object moon = objOf("project.properties", origin);
		if(moon == null) {
			return;
		}
		
		List items = Lists.newArrayList();
		if(List.class.isInstance(moon)) {
			items.addAll((List)moon);
		} else if(Map.class.isInstance(moon)) {
			items.add(moon);
		}
		
		for(Object item : items) {
			Map kid = (Map)item;
			Iterator it = kid.keySet().iterator();
			Object key = it.next();
			String facevalue = kid.get(key) + "";
			String value = useDollars(facevalue, this);
			props.put(key + "", value);
		}
	}
	
	public void parseBasis() {
		groupId = valueOf("project.groupId", origin);
		artifactId = valueOf("project.artifactId", origin);
		version = valueOf("project.version", origin);
		
//		D.pla(groupId, artifactId, version);
		
		Object moon = objOf("project.parent", origin);
		if(moon != null) {
			String parentGroupId = valueOf("groupId", moon);
			String parentArtifactId = valueOf("artifactId", moon);
			String parentVersion = valueOf("version", moon);

//			D.pla(parentGroupId, parentArtifactId, parentVersion);
			String pomPath = pathOfPom(repoPath, parentGroupId, parentArtifactId, parentVersion);
			parent = MavenBuilder.parsePom(pomPath, repoPath, "PPP", depth + 1);
			
			if(groupId == null) {
				groupId = parentGroupId;
			}

			if(artifactId == null) {
				artifactId = parentArtifactId;
			}

			if(version == null) {
				version = parentVersion;
			}
		}

//		D.pla(groupId, artifactId, version);
		
		XXXUtil.shouldBeNotnull(groupId);
		XXXUtil.shouldBeNotnull(artifactId);
		XXXUtil.shouldBeNotnull(version);

		props.put("project.groupId", groupId);
		props.put("project.artifactId", artifactId);
		props.put("project.version", version);
	}
	
	public String toJson() {
		return D.jsp(this, this.getClass());
	}
}
