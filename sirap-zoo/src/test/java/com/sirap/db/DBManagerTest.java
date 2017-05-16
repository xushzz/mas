package com.sirap.db;

import org.junit.Test;

import com.sirap.basic.component.MexedMap;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.db.parser.ConfigItemParser;
import com.sirap.db.parser.ConfigItemParserMySQL;
import com.sirap.db.parser.SchemaNameParser;


/**
 * Unit test for simple App.
 */
public class DBManagerTest {
	
//	private String url = MrTrump.decodeBySIRAP("073AE6B44027A6ECA724FF70DFD0B5A8C064A21BE0C10D1AE89B4BA0E54CB1AC58C53F7A83292DAA616FB7C89810B89F", "ninja");
//	private String username = "iams";
//	private String password = "iams";
	
	private String url = "jdbc:mysql://127.0.0.1/sirap";  
	private String username = "root";    
	private String password = "ninja";

//	private String url = MrTrump.decodeBySIRAP("DE5976AEE3E9426E21EEB25EF81534CE3648181D238C8B2552317A7B15C49B26FED0D9CC7F6CE6C7E4E4715CED9930021F417900AA8E72A22FF18496254ADB2234C6201E3C2755998C5604B13D8CE25D3D5C941C1ABE94258D5E15C80072FC0DB8157E297BDAB019DD6448BC836D7A10", "ninja");  
//	private String username = "root";    
//	private String password = "Paic1234"; 
	
	public void tablename() {
		String sql = "from sirap.jack";
		String regex = "\\{\\s*\\d*\\s*\\}";
		String url = "jdbc:db2://localhost:5000/{0}?james=abc";
		String temp = url.replaceFirst(regex, "");
		C.pl(temp);
	}
	
	//@Test
	public void parseSchema() {
		String url = null;
		url = "jdbc:mysql://localhost:3306/email?useUnicode=true&amp;characterEncoding=UTF-8";
		url = "jdbc:db2://localhost:5000/testDB";
		url = "jdbc:db2://localhost:5000/local";
		url = "jdbc:oracle:thin:@localhost:1521:orcle";
		url = "jdbc:mysql://127.0.0.1/sirap?james=3";
//		url = "jdbc:mysql://127.0.0.1";
//		url = "jdbc:mysql://127.0.0.1/";
//		url = "jdbc:mysql://127.0.0.1?a=c";
		SchemaNameParser brave = new SchemaNameParser();
//		SchemaNameParser brave = new SchemaParserOracle();
		String schema = "jacks";
		C.pl(url);
		String fixedUrl = brave.fixUrlByChangingSchema(url, schema);
		C.pl(fixedUrl);
		C.pl(brave.parseSchema(fixedUrl));
	}
	
	public void take() {
		C.pl(DBHelper.takeAsColumnOrTableName("sir$$$ack"));
		C.pl(DBHelper.takeAsColumnOrTableName("sirap.jack"));
		C.pl(DBHelper.takeAsColumnOrTableName("cust_omer"));
		C.pl(DBHelper.takeAsColumnOrTableName("custo+mer"));
		C.pl(DBHelper.takeAsColumnOrTableName("custo?mer"));
		C.pl(DBHelper.takeAsColumnOrTableName("mer"));
		C.pl(DBHelper.takeAsColumnOrTableName("customer"));
		C.pl(DBHelper.takeAsColumnOrTableName("c232"));
		C.pl(DBHelper.takeAsColumnOrTableName("232omer"));
	}
	public void read() {
		String url = "D:/Github/mas/scripts/dev.properties";
		MexedMap mm = IOUtil.createMexedMapByRegularFile(url);
		
//		Map<String, DBRecord> map = StrUtil.getDbRecordsMap(mm.getContainer());
//		C.pl(map);
	}
	
	public void update() {
		String sql = null;  
	    
		sql = "show tables";//SQL语句  
		sql = "describe customer";
		sql = "insert into customer values(11, 'james', 24)";
//		sql = "update customer set age = 44 where  name = 'felix'";
//		sql = "update customer set age = 44 where  name = 'ky'";
//		sql = "delete from customer where name like 'mk%'";
  
        try {
        	int records = DBManager.g2(url, username, password).update(sql);
        	C.pl(records);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
//	@Test
	public void select() {
		String sql = null;  
		sql = "show tables";
//		sql = "show databases";
//		sql = "show create table IAMS_DLE_BK";
//		sql = "show create table dp_user";
//		sql = "select * from IAMS_DLE_BK where id in(140,141)   limit    19";
//		sql = "select * from dp_user    limit    19";
//		sql = "describe customer";
//		sql = "select * from employees limit 100";
//		sql = "select table_name from user_tables";
//		sql = "select * from TEMP_IMP_MW";
//		sql = "explain select * from users";
//		sql = "select table_name, table_rows from information_schema.tables";
  
        try {
        	//url = "jdbc:oracle:thin:@10.202.50.236:1521:devora";
        	url = "jdbc:mysql://127.0.0.1";
        	DBManager xian = DBManager.g2(url, username, password);
//        	xian.setSchema("devora");
        	QueryWatcher records = xian.query(sql, true, true);
        	C.list(records.exportListItems());
        } catch (Exception e) {
            e.printStackTrace();  
        }  
	}

	public void getDriverName() {
		String url = "";
		url="JDBC:oracle:thin:@localhost:1521:orcl";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:db32://localhost:5000/testDb";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:mysql://localhost:8080/testDB";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:microsoft:sqlserver://localhost:1433;DatabaseName=testDb";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:postgresql://localhost/testDb";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:sybase:Tds:localhost:5007/testDb";
		C.pl(StrUtil.getDbDriverByUrl(url));
		url="JDBC:informix-sqli:localhost:1533/testDb:INFORMIXSERVER=myserver\"user=testUser;password=testpassword";
		C.pl(StrUtil.getDbDriverByUrl(url));
	}
	
	public void parseDBType() {
		String url = "";
		url="JDBC:oracle:thin:@localhost:1521:orcl";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:db2://localhost:5000/testDb";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:mysql://localhost:8080/testDB";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:microsoft:sqlserver://localhost:1433;DatabaseName=testDb";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:postgresql://localhost/testDb";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:sybase:Tds:localhost:5007/testDb";
		C.pl(StrUtil.parseDbTypeByUrl(url));
		url="JDBC:informix-sqli:localhost:1533/testDb:INFORMIXSERVER=myserver\"user=testUser;password=testpassword";
		C.pl(StrUtil.parseDbTypeByUrl(url));
	}
	
	@Test
	public void createConfigItem() {
		String source = "mysql -uroot -pninja -h localhost -P3306 -Dsirap";
		source = "mysql -uroot -p ninja";
		ConfigItemParser hai = new ConfigItemParserMySQL();
		DBConfigItem item = hai.parse(source);
		C.pl(item);
	}
}
