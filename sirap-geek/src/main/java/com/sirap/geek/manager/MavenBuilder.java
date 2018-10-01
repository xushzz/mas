package com.sirap.geek.manager;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.geek.domain.MavenDep;
import com.sirap.geek.domain.MavenPom;

public class MavenBuilder {
	
	private static class Holder {
		private static ThreadLocal<Map<String, MavenPom>> allPoms = new ThreadLocal<Map<String, MavenPom>>(){
			protected Map<String, MavenPom> initialValue() {
				return new LinkedHashMap<>();
			};
		};
	}

	public static void lark() {
		String va = "D:/Gitpro/FARM/gs-spring-boot/sirap-springboot/pom.xml";
		va = "D:/m2repo/org/springframework/cloud/spring-cloud-dependencies/Dalston.SR3/spring-cloud-dependencies-Dalston.SR3.pom";
		va = "D:\\m2repo\\org\\springframework\\cloud\\spring-cloud-dependencies\\Dalston.SR3\\spring-cloud-dependencies-Dalston.SR3.pom";
//		va = "D:/m2repo/org/springframework/cloud/spring-cloud-commons-dependencies/1.2.3.RELEASE/spring-cloud-commons-dependencies-1.2.3.RELEASE.pom";
//		va = "D:/M2REPO/org/springframework/cloud/spring-cloud-commons-dependencies/2.0.1.RELEASE/spring-cloud-commons-dependencies-2.0.1.RELEASE.pom";
//		va = "D:/M2REPO/org/springframework/cloud/spring-cloud-dependencies-parent/2.0.3.RELEASE/spring-cloud-dependencies-parent-2.0.3.RELEASE.pom";
//		va = "D:/m2repo/org/springframework/cloud/spring-cloud-commons-dependencies/2.0.1.RELEASE/spring-cloud-commons-dependencies-2.0.1.RELEASE.pom";
//		va = "D:/m2repo/org/springframework/boot/spring-boot-starter-parent/2.0.5.RELEASE/spring-boot-starter-parent-2.0.5.RELEASE.pom";
//		va = "D:/m2repo/org/springframework/cloud/spring-cloud-dependencies/Dalston.SR3/spring-cloud-dependencies-Dalston.SR3.pom";
//		va = "D:/m2repo/org/springframework/cloud/spring-cloud-aws-dependencies/1.2.1.RELEASE/spring-cloud-aws-dependencies-1.2.1.RELEASE.pom";
//		va = "D:/m2repo/com/amazonaws/aws-java-sdk-bom/1.11.125/aws-java-sdk-bom-1.11.125.pom";
//		va = "D:/m2repo/com/amazonaws/aws-java-sdk-pom/1.11.125/aws-java-sdk-pom-1.11.125.pom";
//		va = "D:/M2REPO/org/apache/httpcomponents/httpcomponents-parent/10/httpcomponents-parent-10.pom";
//		va = "D:/Gitpro/FARM/gs-spring-boot/sirap-springboot/pom.xml";
		va = "D:/Gitpro/SIRAP/masroo/sirap-common/pom.xml";
//		va = "D:/Gitpro/SIRAP/masroo/sirap-basic/pom.xml";
//		va = "E:/KDB/tasks/0927_MavenPom/sirap-pom/pom.xml";
//		va = "D:/Gitpro/SIRAP/masroo/sirap-third/pom.xml";
//		va = "D:/Gitpro/OSChina/todos/springxxx/demo/pom.xml";
//		va = "E:/KDB/tasks/0927_MavenPom/sirap-pyke/pom.xml";
//		va = "E:/KDB/tasks/0927_MavenPom/ANGEL.txt";
		MavenPom me = MavenBuilder.parsePom(va, "D:/m2repo", "XXX", 0);
//		D.pjsp(me.getProps());
//		temp.dollarTest();
//		me.applyExpression();
		D.list(getParentsSelfKidsDeps(me));
//		showParentsManagements(me);
//		D.pl();
//		showParentsDeps(me);
//		D.list(me.getKidsDeps());
//		applyManagements(me);
//		showParentsDeps(me);
//		me.getKidsManagement();
//		D.pjsp(me.getProps());
//		D.pjsp(me.getOrigin(), MavenDepManagement.class, MavenDep.class, MavenPom.class, MavenDeps.class);
	}
	
	public static List<MavenDep> getParentsSelfKidsDeps(MavenPom pom) {
		applyManagements(pom);
		List<MavenDep> allDeps = Lists.newArrayList();
		allDeps.addAll(pom.getParentsSelfDeps());
		allDeps.addAll(pom.getKidsDeps());
		
		return allDeps;
	}
	
	private static void applyManagements(MavenPom pom) {
		D.pl(pom.getPomPath());
		Map<String, MavenDep> mars = depsToMap(pom.getParentsSelfKidsManagements());
		Map<String, MavenDep> deps = depsToMap(pom.getParentsSelfDeps());
//		C.listSome(Lists.newArrayList(mars.keySet()), 10);
//		D.list(Lists.newArrayList(deps.keySet()));
		
		Iterator<String> it = deps.keySet().iterator();
		int count = 0;
		while(it.hasNext()) {
			String key = it.next();
			MavenDep dep = deps.get(key);
			if(dep.getVersion() != null) {
				continue;
			}
			
			count++;
			MavenDep goodDep = mars.get(key);
			String version = goodDep.getVersion();
			dep.setVersion(version);
			dep.setExclusions(goodDep.getExclusions());
			XXXUtil.shouldBeNotnull(version);
			D.pl(key + " => " + version);
		}
		D.pl(count);
	}
	
	public static void showParentsManagements(MavenPom pom) {
		int level = 1;
		String prefix = "Managements";
		do {
			List<MavenDep> deps = pom.getManagements();
			String filename = FileUtil.filenameWithExtensionOf(pom.getPomPath());
			String info = StrUtil.occupy("{3}#{0} has {1} kids, {2}", level, deps.size(), filename, prefix);
			C.pl(pom.getPomPath());
			C.pl(info);
			if(!deps.isEmpty()) {
				D.pjsp(deps);
			}
			pom = pom.getParent();
			level++;
		} while (pom != null);
	}
	
	public static void showParentsDeps(MavenPom pom) {
		int level = 1;
		String prefix = "Deps";
		do {
			List<MavenDep> deps = pom.getDependencies();
			String filename = FileUtil.filenameWithExtensionOf(pom.getPomPath());
			String info = StrUtil.occupy("{3}#{0} has {1} kids, {2}", level, deps.size(), filename, prefix);
			C.pl(pom.getPomPath());
			C.pl(info);
			if(!deps.isEmpty()) {
				D.pjsp(deps);
			}
			pom = pom.getParent();
			level++;
		} while (pom != null);
	}

	public static void cleanCache() {
		Holder.allPoms.get().clear();
	}
	
	public static MavenPom parsePom(String pomPath, String repoPath, String type, int depth) {
		MavenPom kid = Holder.allPoms.get().get(pomPath);
		if(kid == null) {
			C.pl2(type + " " + StrUtil.repeat('$', depth + 1) + " " + pomPath);

			kid = new MavenPom();
			kid.setPomPath(pomPath);
			kid.setRepoPath(repoPath);
			kid.setDepth(depth);
			kid.process();
			
			Holder.allPoms.get().put(pomPath, kid);
		}
		
		return kid;
	}
	
	public static Map<String, MavenDep> depsToMap(List<MavenDep> deps) {
		Map<String, MavenDep> mars = new LinkedHashMap<>();
		for(MavenDep dep : deps) {
			mars.put(dep.keyOf(), dep);
		}

		return mars;
	}
}
