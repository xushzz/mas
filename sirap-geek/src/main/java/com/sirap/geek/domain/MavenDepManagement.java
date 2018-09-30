package com.sirap.geek.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;

import lombok.Data;

@Data
public class MavenDepManagement extends MavenItem {
	private List<MavenDep> dependencies = Lists.newArrayList();
	
	@Override
	public boolean parse(String source) {
		String dogs = readElementValue(source ,"dependencies");
		if(dogs != null) {
			MavenDeps deps = new MavenDeps();
			deps.parse(dogs);
			dependencies.addAll(deps.getDependencies());
		}

		return true;
	}
	
	public String toJson() {
		return D.jsp(this, this.getClass());
	}
}
