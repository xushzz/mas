package com.sirap.common.test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.XXXUtil;

public class IOTest {
	
	@Test
	public void copy() {
		String target = "E:/";
		String path = target + "DOUG.txt";
		IOUtil.copyFile(path, target);
	}
	
	public void robot() {
		try {
		Robot robot = new Robot();
		//定义5秒的延迟以便你打开notepad 
		// Robot 开始写
		robot.delay(5000);
		robot.keyPress(KeyEvent.VK_H);
		robot.keyPress(KeyEvent.VK_I);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyPress(KeyEvent.VK_O);
		robot.keyPress(KeyEvent.VK_E);
		robot.keyPress(KeyEvent.VK_R);
		} catch (AWTException e) {
		e.printStackTrace();
		}
	}

	public void capture() {
//		String fileName = "E:\\Klose\\s\\" + DateUtil.getTimestampWithSpace();
//		IOUtil.takePhoto(fileName, null, 1);
	}
	
	public void locale() {
		
//		M.list(ExtractorUtil.getDisplayCountryCodes(Locale.US));
		XXXUtil.alert("das");
		C.pl("fsd");
	}
	
	@Test
	public void readURL() {
		String filePath = "E:\\Klose\\Links_UEFA.txt";
		filePath = "E://Work//TianRecord_04Apr.txt";
//		C.pl(IOUtil.readFile(filePath));
	}
	
	public void list() {
		//E:\\JM\bk\\jesus
		//C:\\Users\\dell\\.m2\\repository
		//"E:"
//		C.list(FileUtil.scanContent("E:\\JM\\bk", 3, ".txt"));
	}
	
	public void image() {
//    	Camera cam = new Camera("d:\\qq", "jpeg");//
//        cam.snap();
	}
	public void disk() {
//		NetUtil.readAndSend();
	}
	
	public void mail() {
//		String content = "[啊, 的, 饿, 去, 飞, f, 3, 434, 23, 4]";
//		Mail mail = new N63Mail("sssssssssssssss");
//		MailMan jack = new MailMan(mail);
//		jack.send();
	}

	public void fileName() {
		C.pl(FileUtil.getIfNormalFile("E:\\JM\\storage/..."));
		C.pl(FileUtil.getIfNormalFile("E:\\JM\\storage/bk"));
		C.pl(FileUtil.getIfNormalFile("E:\\JM\\storage/IMEI_20140731_Detail.txt"));
		C.pl(FileUtil.getIfNormalFile("E:\\JM\\storage/AS"));
	}
	
	public void kiees() {
//		String url = "http://www.kiees.cn/sf.php?wen=114750081239";
//		String url = "http://open.baidu.com/special/time/";
//		M.pl(IOUtil.readURL(url, Konstants.CHARSET_GBK));
	}
	
	public void mkdirs() {
		FileUtil.makeDirectoriesIfNonExist("E:/a/a");
	}
}
