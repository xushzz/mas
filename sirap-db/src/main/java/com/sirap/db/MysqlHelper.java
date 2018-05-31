package com.sirap.db;

import java.sql.ResultSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.db.domain.MysqlHelpCategory;
import com.sirap.db.domain.MysqlHelpTopic;
import com.sirap.db.resultset.ResultSetAnalyzer;

public class MysqlHelper {
	
	public static List<MysqlHelpCategory> getHelpCategories(DBManager manager) {
		String sqlCat = "select * from mysql.help_category order by name";
		ResultSetAnalyzer<List<MysqlHelpCategory>> category = new ResultSetAnalyzer<List<MysqlHelpCategory>>() {
			@Override
			public List<MysqlHelpCategory> analyze(ResultSet rest) throws Exception {
		    	List<MysqlHelpCategory> records = Lists.newArrayList();
		        while (rest.next()) {
		        	int id = rest.getInt(1);
		        	String name = rest.getString(2);
		        	int parent = rest.getInt(3);
			        records.add(new MysqlHelpCategory(id,  name, parent));
		        }
		        
				return records;
			}
		};
		
		return manager.query(category, sqlCat);
	}
	
	public static List<MysqlHelpTopic> getHelpTopics(DBManager manager) {
		String sqlTopic = "select * from mysql.help_topic order by name";
		ResultSetAnalyzer<List<MysqlHelpTopic>> topic = new ResultSetAnalyzer<List<MysqlHelpTopic>>() {
			@Override
			public List<MysqlHelpTopic> analyze(ResultSet rest) throws Exception {
		    	List<MysqlHelpTopic> records = Lists.newArrayList();
		        while (rest.next()) {
		        	int id = rest.getInt(1);
		        	String name = rest.getString(2);
		        	int parent = rest.getInt(3);
		        	String desc = rest.getString(4);
		        	String example = rest.getString(5);
			        records.add(new MysqlHelpTopic(id,  name, parent, desc, example));
		        }
		        
				return records;
			}
		};
		
		return manager.query(topic, sqlTopic);
	}
	
	public static MysqlHelpTopic getHelpTopic(DBManager manager, int topicId) {
		String sqlTopic = "select * from mysql.help_topic where help_topic_id = " + topicId;
		ResultSetAnalyzer<MysqlHelpTopic> topic = new ResultSetAnalyzer<MysqlHelpTopic>() {
			@Override
			public MysqlHelpTopic analyze(ResultSet rest) throws Exception {
		        if (rest.next()) {
		        	int id = rest.getInt(1);
		        	String name = rest.getString(2);
		        	int parent = rest.getInt(3);
		        	String desc = rest.getString(4);
		        	String example = rest.getString(5);
			        return new MysqlHelpTopic(id,  name, parent, desc, example);
		        }
		        
				return null;
			}
		};
		
		return manager.query(topic, sqlTopic);
	}
	
	public static MysqlHelpTopic getHelpTopic(DBManager manager, String name) {
		String sqlTopic = StrUtil.occupy("select * from mysql.help_topic where name = '{0}'", name);
		ResultSetAnalyzer<MysqlHelpTopic> topic = new ResultSetAnalyzer<MysqlHelpTopic>() {
			@Override
			public MysqlHelpTopic analyze(ResultSet rest) throws Exception {
		        if (rest.next()) {
		        	int id = rest.getInt(1);
		        	String name = rest.getString(2);
		        	int parent = rest.getInt(3);
		        	String desc = rest.getString(4);
		        	String example = rest.getString(5);
			        return new MysqlHelpTopic(id,  name, parent, desc, example);
		        }
		        
				return null;
			}
		};
		
		return manager.query(topic, sqlTopic);
	}
	
	public static List<MysqlHelpCategory> addTopics(List<MysqlHelpCategory> cats, List<MysqlHelpTopic> topics) {
		for(MysqlHelpTopic top : topics) {
			int parent = top.getParent();
			MysqlHelpCategory cat = findById(cats, top.getParent());
			if(cat == null) {
				XXXUtil.info("No category for topic {0} {1}", parent, top.getName());
				continue;
			}
			
			cat.getTopics().add(top);
		}
		
		return cats;
	}
	
	public static MysqlHelpCategory findById(List<MysqlHelpCategory> cats, int id) {
		for(MysqlHelpCategory cat : cats) {
			if(cat.getId() == id) {
				return cat;
			}
		}
		
		return null;
	}
		
	public static MysqlHelpCategory categoryTreeOf(List<MysqlHelpCategory> cats) {
		MysqlHelpCategory root = new MysqlHelpCategory(0, "ROOT", -1);
		sonsOf(cats, root);
		
		return root;
	}
	
	private static void sonsOf(List<MysqlHelpCategory> cats, MysqlHelpCategory dad) {
		int dadId = dad.getId();
		List<MysqlHelpCategory> sons = Lists.newArrayList();
		for(MysqlHelpCategory cat : cats) {
			if(cat.getParent() == dadId) {
				sonsOf(cats, cat);
				sons.add(cat);
			}
		}
		dad.setSons(sons);
	}
}
