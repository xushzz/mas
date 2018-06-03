package com.sirap.orm.mybatis;

import org.apache.ibatis.session.SqlSession;

public class BatisManager {
	
	private static BatisManager instance;
	
	private SqlSession session;
	private static final String MYBATIS_CONFIG = "batis-config.xml";
	
	public static BatisManager g() {
		if(instance == null) {
			instance = new BatisManager();
			instance.session = BatisUtil.getSessionFactory(MYBATIS_CONFIG).openSession();
		}
		
		return instance;
	}
	
	public SqlSession getSession() {
		return session;
	}
	
	public void closeSession() {
		session.close();
	}
}
