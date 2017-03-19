package com.sirap.basic.domain.xml;

import java.io.File;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class PomArtifact extends MexItem {
	
	private String groupId;
	private String artifactId;
	private String version;
	
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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(groupId).append(", ");
		sb.append(artifactId).append(", ");
		sb.append(version);
		
		return sb.toString();
	}
	
	public String repoPath() {
		StringBuilder sb = new StringBuilder();
		if(groupId != null) {
			String temp = groupId.replace('.', File.separatorChar);
			sb.append(temp).append(File.separatorChar);
		}
		if(artifactId != null) {
			String temp = artifactId.replace('.', File.separatorChar);
			sb.append(temp).append(File.separatorChar);
		}
		if(version != null) {
			sb.append(version).append(File.separatorChar);
		}
		sb.append(artifactId).append("-").append(version).append(".jar");
		
		return sb.toString();
	}
}
