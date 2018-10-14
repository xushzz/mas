package com.sirap.geek.domain;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;

public class MavenDeps extends MavenItem {
	private List<MavenDep> dependencies = Lists.newArrayList();
	
	public List<MavenDep> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<MavenDep> dependencies) {
		this.dependencies = dependencies;
	}

	public void read(Object cake) {
		List items = Lists.newArrayList();
		if(List.class.isInstance(cake)) {
			items.addAll((List)cake);
		} else if(Map.class.isInstance(cake)) {
			items.add(cake);
		}
		
		for(Object item : items) {
			MavenDep dep = new MavenDep();
			dep.read(objOf("dependency", item));
			dependencies.add(dep);
		}
	}
	
	public String toJson() {
		return D.jsp(this, this.getClass());
	}
}
