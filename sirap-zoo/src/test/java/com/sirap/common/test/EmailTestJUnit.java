package com.sirap.common.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

import org.junit.Test;

import com.sirap.basic.domain.EmailCommandRecord;
import com.sirap.basic.search.CriteriaFilter;
import com.sirap.basic.thirdparty.email.EmailFetcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.security.MrTrump;

public class EmailTestJUnit {
	
	@Test
	public void fetch() {
		String username = "piratewithoutsea";
		username = "yuzhending";
		username = "aol007";

		String pwd = MrTrump.decodeBySIRAP("5C5985E456AFC2E780D5D41AF127E283", "jinx");
		pwd = "wodexiuxiu";
		pwd = "loveyou";
		
		EmailFetcher mike = new EmailFetcher(username + "@163.com", pwd);
		
		SearchTerm what = new SubjectTerm("nekohtml"); //subject
		what = new FromStringTerm("netease"); //who sent
		
		Calendar calendar = Calendar.getInstance();    
		calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK - (Calendar.DAY_OF_WEEK - 1)) - 1);   
		Date mondayDate = calendar.getTime();   
		C.pl(mondayDate);
		
		Date date = DateUtil.construct(2015, 12, 13);
//		date = new Date();
		D.pl(date);
		SentDateTerm minDate = new SentDateTerm(ComparisonTerm.GT, DateUtil.construct(2017, 5, 1, 0, 0, 0));
		SentDateTerm maxDate = new SentDateTerm(ComparisonTerm.LT, DateUtil.construct(2017, 5, 2));
		what = new SentDateTerm(ComparisonTerm.GE, date); 
		what = new AndTerm(minDate, maxDate);
		
		List<EmailCommandRecord> items = null; //mike.fetch(what);
		C.list(items);
	}
	
	public void service() {
		String a = "";
		Object b = null;
		Object[] c = new Object[0];
		Collection d = new ArrayList();
		Map e = new HashMap();
//		DevService.nullCheck(a, ":Nothing is going to stop me.");
//		DevService.nullCheck(b);
		XXXUtil.nullCheck(c, "Object[] c");
		XXXUtil.nullCheckOnly(d);
		XXXUtil.nullCheckOnly(e);
	}
	public void criteriaFilter() {
		String input = "hi james>a@a.com;aol007@163.com;b@b.com";
		String output = "hi james>a@a.com;b@b.com";
		String target = "aol007@163.com";
		CriteriaFilter fk = new CriteriaFilter(target, input, ";x");
		D.eq(fk.getFixedCommand(), output);
//		assertEquals(fk.getFixedCommand(), output);
	}
	
//	@Test
	public void split() {
		String source = "He was also a delegate to the Virginia constitutional rate for drafting the first ten amendments to the Constitution, and thus is known as the \"Father of the nation\"";
		C.list(CollectionUtil.splitIntoRecords(source, 5));
//		CollectionUtil.splitIntoRecords("BarackHusseinOb", 51);
//		CollectionUtil.splitIntoRecords("BarackHusseinObama", 5);
	}
	//@Test
	public void findParam() {
		String regex = "(\\d)";
		String source = "a19b233c3de2121212fg";
		C.pl(StrUtil.findAllMatchedItems(regex, source));
		C.pl(StrUtil.findFirstMatchedItem("(\\d)", source));
//		C.pl(StrUtil.findFirstMatchedItem("\\d+", source));
	}
	
	public void countDown() {
		D.ts();
//		PanaceaBox.countDown(3);
		D.pl();
		D.ts();
	}
}
