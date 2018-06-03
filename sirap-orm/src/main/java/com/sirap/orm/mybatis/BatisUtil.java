package com.sirap.orm.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.TypeAliasRegistry;

import com.google.common.collect.Lists;

public class BatisUtil {

	public static List<List> getBuiltInAliases() {
		Map<String, Class<?>> builtin = (new TypeAliasRegistry()).getTypeAliases();
		
		List<List> items2 = Lists.newArrayList();
		List<BatisAlias> items = Lists.newArrayList();
		for(Entry<String, Class<?>> entry: builtin.entrySet()) {
			String key = entry.getKey();
			String temp = "* " + entry.getValue().getName();
			BatisAlias item = new BatisAlias(key, entry.getValue().getSimpleName(),temp );
			items.add(item);
		}
		
		Collections.sort(items);
		for(BatisAlias bi : items) {
			items2.add(Lists.newArrayList(bi.getAlias(), bi.getSimple(), bi.getFull()));
		}
		
		return items2;
	}
	
	public static List<List> getSessionAliases(SqlSession session) {
		Map<String, Class<?>> currentsession = session.getConfiguration().getTypeAliasRegistry().getTypeAliases();
		Set<String> builtinKeys = (new TypeAliasRegistry()).getTypeAliases().keySet();
		
		List<List> items2 = Lists.newArrayList();
		List<BatisAlias> items = Lists.newArrayList();
		for(Entry<String, Class<?>> entry: currentsession.entrySet()) {
			String key = entry.getKey();
			String temp;
			if(builtinKeys.contains(key)) {
				temp = "* " + entry.getValue().getName();
			} else {
				temp = entry.getValue().getName();
			}
			BatisAlias item = new BatisAlias(key, entry.getValue().getSimpleName(),temp );
			items.add(item);
		}
		
		Collections.sort(items);
		for(BatisAlias bi : items) {
			items2.add(Lists.newArrayList(bi.getAlias(), bi.getSimple(), bi.getFull()));
		}
		
		return items2;
	}
	
    //Mybatis 通过SqlSessionFactory获取SqlSession, 然后才能通过SqlSession与数据库进行交互  
    public static SqlSessionFactory getSessionFactory(String resource) {    
        SqlSessionFactory sessionFactory = null;
        try {    
        	InputStream is = Resources.getResourceAsStream(resource);
//        	Reader reader = Resources.getResourceAsReader(resource);
            sessionFactory = new SqlSessionFactoryBuilder().build(is);  
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
        return sessionFactory;    
    }    
}
