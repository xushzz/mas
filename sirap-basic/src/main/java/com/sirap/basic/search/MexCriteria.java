package com.sirap.basic.search;

import java.util.List;

public class MexCriteria {
	private String logic = MexFilter.LOGIC_AND;
	private List<String> criterias;
	
	public MexCriteria(List<String> list) {
		this.criterias = list;
	}
	
	public MexCriteria(String logic, List<String> list) {
		this.logic = logic;
		this.criterias = list;
	}
	
	public String getLogic() {
		return logic;
	}
	public void setLogic(String logic) {
		this.logic = logic;
	}
	public List<String> getCriterias() {
		return criterias;
	}
	public void setCriterias(List<String> criterias) {
		this.criterias = criterias;
	}
}
