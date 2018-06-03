package com.sirap.orm.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class SirapDataSourceFactory extends UnpooledDataSourceFactory {
	protected DataSource dataSource;
	public SirapDataSourceFactory() {
	    this.dataSource = new ComboPooledDataSource();
	}
}
