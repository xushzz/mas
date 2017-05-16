package com.sirap.basic;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.tool.ScreenCaptor;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;

public class BasicTest {
	
	@DataProvider
    public Object[][] remoteDP(){
        Object[][] result = {{"$James"}, {"James"}, {"s"}, {"$"}, {""}};
        return result;
    }
	
	@Test(dataProvider="remoteDP")
	public void remote(String input) {
		String[] params = StrUtil.parseParams("(\\$|)(.*?)", input);
		D.pl(params);
	}
	
	public void capture() {
		String fileName = "E://" + DateUtil.timestamp();
		String sound = "F:/Music/apple.wav";
		ScreenCaptor george = new ScreenCaptor(fileName, sound, false);
		ThreadUtil.sleepInSeconds(2);
		PanaceaBox.openFile(george.capture());
	}
	
	@Test(enabled=false, dataProvider="mexFolder")
	public void mexFolder(String path) {
		assertEquals(FileUtil.isMaliciousPath(path), true);
	}
	
	@DataProvider
    public Object[][] mexFolder(){
        Object[][] result = {{"."},{".."},{"..."},{"/"},{"//"},{"///"},{"\\"},{"\\\\"},{"\\\\\\"},};
        return result;
    }
	
	public void isDiskName() {
//		D.pl(FileUtil.isDiskName("c:"));
//		D.pl(FileUtil.isDiskName("ca:"));
//		D.pl(FileUtil.isDiskName("C:"));
//		D.pl(FileUtil.isDiskName("D:"));
//		D.pl(FileUtil.isDiskName("C:\""));
//		D.pl(FileUtil.isDiskName("D:/"));
	}
	
	public void parse() {
		String regex = "z\\.(.{1,20})";
		D.pl("A", StrUtil.parseParam(regex, "z.da"));
//		D.pl("B", StrUtil.parseParam(regex, "z").length());
	}
	public void split() {
		String source = "He was also a delegate to the Virginia constitutional rate for drafting the first ten amendments to the Constitution, and thus is known as the \"Father of the nation\"";
		C.list(CollectionUtil.splitIntoRecords(source, 25));
//		CollectionUtil.splitIntoRecords("BarackHusseinOb", 51);
//		CollectionUtil.splitIntoRecords("BarackHusseinObama", 5);
	}
	public void list() {
		C.pl(System.getProperty("file.encoding"));
		//E:\JM\bk
		//C:\\Users\\dell\\.m2\\repository
		//"E:"
//		M.list(IOUtil.scan());
	}
}
