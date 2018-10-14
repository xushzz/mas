package com.sirap.geek.domain;

public class MavenExclusion extends MavenItem {
	private String groupId;
	private String artifactId;
	
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

	@Override
	public void read(Object source) {
		groupId = valueOf("groupId", source);
		artifactId = valueOf("artifactId", source);
	}
}
