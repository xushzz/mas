package com.sirap.geek.domain;

import lombok.Data;

@Data
public class MavenExclusion extends MavenItem {
	private String groupId;
	private String artifactId;
	
	@Override
	public void read(Object source) {
		groupId = valueOf("groupId", source);
		artifactId = valueOf("artifactId", source);
	}
}
