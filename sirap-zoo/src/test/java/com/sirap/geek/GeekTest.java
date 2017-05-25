package com.sirap.geek;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.IDCardUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.geek.jenkins.JenkinsBuildRecord;
import com.sirap.geek.jenkins.JenkinsManager;
import com.sirap.geek.manager.GithubIssuesExtractor;
import com.sirap.geek.manager.MavenManager;

public class GeekTest {

	String url = "http://elastos.org/jenkins";
	//Daily-Elastos-Framework
	//AutoMakeForElastos
	//testForTestlink
	//ElastosTest
	//GitstatsForElastosOrg
	//testForTestlink
	String jobName = "GitstatsForElastosOrg";
	
	private JenkinsManager nick = JenkinsManager.g2(url);
	
//	@Test
	public void ascii() {
		String src = "D:/1.txt";
		List<String> items = IOUtil.readFileIntoList(src);
		for(int i = 0; i < items.size(); i++) {
			String temp = "ASCII_INFO.put({0}, \"{1}\");";
			String item = items.get(i).replaceAll("\\s+[a-z]{1}", "") + " - NATO";
			String value = StrUtil.occupy(temp, 65 + i, item);
			C.pl(value);
			value = StrUtil.occupy(temp, 97 + i, item);
			C.pl(value);
		}
	}
	public void latestBuildId() {
	}
	
	//@Test
	public void displayStatus() {
		String buildId = "337";
		C.pl(nick.getLatestBuildRecord(jobName));
		C.pl(nick.getBuildRecordByNumber("337", jobName));
	}
	
	//@Test
	public void mvnDeps() {
		String fileName = "E:\\Mas\\tasks\\0216_CmdExecutionWithC\\deps.txt";
		List<String> buildResult = IOUtil.readFileIntoList(fileName);
		MavenManager.validateAndParseDeps(null, buildResult);
	}
	
	//@Test
	public void parseDep() {
		String va = "org.apache.pdfbox:fontbox:jar:2.0.3:compile";
		//va = "net.sf.json-lib:json-lib:jar:jdk15:2.2.2:compile";
		String v2 = MavenManager.parseDetailAndConstructPath("D:\\M2REPO", va);
		C.pl(v2);
	}
	
	public void displayKStatus() {
		List<JenkinsBuildRecord> items = nick.getLatestKBuildRecords(jobName, 40);
		C.list(items);
	}
	
	public void issues() {
		String repo = "piratesea/mas";
		repo = "datacharmer/mysql-sandbox";
		GithubIssuesExtractor frank = new GithubIssuesExtractor(repo);
		frank.process();
		C.list(frank.getMexItems());
	}
	
	@Test
	public void checkCode() {
		C.pl('1' - '0');
//		C.pl(StrUtil.checkCode("45272319940110083a"));
		C.pl("452723199401100834".replaceAll("\\d$", "X"));
		C.pl(IDCardUtil.checkCodeChina("45272319940110083X"));
		C.pl(IDCardUtil.checkCodeChina("132302198908270037"));
		C.pl(IDCardUtil.checkCodeChina("45272319880529083"));
	}
}
