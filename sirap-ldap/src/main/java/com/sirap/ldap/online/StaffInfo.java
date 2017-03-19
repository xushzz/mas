package com.sirap.ldap.online;

public class StaffInfo implements Comparable<StaffInfo> {
	private String account;
	private String whenCreated;
	private String detail;
	private String manager;
	private String directReports;
	
	public StaffInfo() {}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(String whenCreated) {
		this.whenCreated = whenCreated;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}
	
	public String getDirectReports() {
		return directReports;
	}

	public void setDirectReports(String directReports) {
		this.directReports = directReports;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaffInfo other = (StaffInfo) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		return true;
	}

	@Override
	public int compareTo(StaffInfo who) {
		String whenA = getWhenCreated();
		String whenB = who.getWhenCreated();
		if(whenA == null || whenB == null) {
			return 0;
		}
		
		return whenA.compareTo(whenB);
	}
	
	public String toString() {
		return detail;
	}
}