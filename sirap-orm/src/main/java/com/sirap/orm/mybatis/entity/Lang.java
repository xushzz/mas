package com.sirap.orm.mybatis.entity;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.sirap.basic.tool.D;

@Alias("LangX") 
public class Lang {
	private int id;
	private String name;
	private Date lastUpdate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	@Override
	public String toString() {
		return D.js(this, getClass());
	}
}
