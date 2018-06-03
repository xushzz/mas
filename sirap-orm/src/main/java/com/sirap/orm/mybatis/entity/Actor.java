package com.sirap.orm.mybatis.entity;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.sirap.basic.tool.D;

//select actor_id, first_name, last_name, last_update from sakila.actor
@Alias("ActorX") 
public class Actor {
	private int actorId;
	private String firstName;
	private String lastName;
	private Date lastUpdate;
	public int getActorId() {
		return actorId;
	}
	public void setActorId(int actorId) {
		this.actorId = actorId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	@Override
	public String toString() {
		return D.jsp(this, getClass());
	}
}
