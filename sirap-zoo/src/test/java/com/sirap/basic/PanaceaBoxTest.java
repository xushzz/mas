package com.sirap.basic;

import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.sirap.basic.math.ArrangementCalculatorWithList;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.PanaceaBox;

public class PanaceaBoxTest {
	
	@Test
	public void jpg() {
		String path = "F:/Here().jpg";
		path = "F:/Here().mp3";
		PanaceaBox.openFile(path);
	}
	
	@Test(enabled=false)
	public void shutdown() {
		String cmd = "shutdown.exe -s";
		PanaceaBox.execute(cmd);
	}

	@Test(enabled=false)
	public void explorer() {
		String exe = "C:/Program Files/Google/Chrome/Application/chrome.exe";
		String file = "https://oj.leetcode.com/problems/edist-distance/";
		exe = "C:/Program Files/Google/Picasa3/PicasaPhotoViewer.exe";
//		exe = "C:/Program Files/Free Photo Viewer/FreePhotoViewer.exe";
		
//		file = "E:/Software2014/proguard4.11/lib/proguard.jar";
		file = "E:/MasPro/shot/20141023_111303_!@#$%^&-ZA()-.jpg";
		file = "E:/MasPro/shot/20141023_111202_are we safe-.jpg";
		PanaceaBox.openFile(file);
		try {
			String cmd = exe + " " + file;
//			D.pl(cmd);
//			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(enabled=false)
	public void cmd() {
		//http://liuyuru.iteye.com/blog/806365
		String filePath = "E:\\Klose\\a  d\\";
		String fileName = "Luo\" \"Ge.jpg";
		
		String cmd = "cmd.exe /c start " + fileName;
		Runtime rt = Runtime.getRuntime();
		try {
			String[] arr = {cmd, fileName};
			Process p = rt.exec(cmd, null, new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(enabled=false)
	public void cmd2() {
		String filePath = "E:\\Klose\\a  d\\Luo Ge.jpg";
		filePath = filePath.replace(" ", "\" \"");
		D.pl(filePath);
		String cmd = "cmd.exe /c start " + filePath;
		Runtime rt = Runtime.getRuntime();
		try {
			String[] arr = {cmd, filePath};
			rt.exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void split() {
//		calc.parseOperandsAndOperators(" 1 +  +	2+  3+ 4");
//		calc.parseOperandsAndOperators("-1+2+3+4");
//		calc.parseOperandsAndOperators("1+2+3+4");
//		calc.parseOperandsAndOperators("1-2-3-4");
//		calc.parseOperandsAndOperators("10+20+30+40");
//		calc.parseOperandsAndOperators("11-21-31-41");
//		calc.parseOperandsAndOperators("1+23-23-232/23x323");
//		calc.parseOperandsAndOperators("-11+23-232-2-2x32/23x323-3");
//		calc.parseOperandsAndOperators("-11+23x32/23x323");
//		calc.parseOperandsAndOperators("+-+-");
	}
	
	public void setLongLen() {
		double a1 = (double)Math.sqrt(Double.MAX_VALUE);
		double a2 = Double.MAX_VALUE;
		C.pl(a1);//3037000499
		C.pl(a2);//9223372036854775807
	}
	
	public void permutation() {
		List<String[]> source = new ArrayList<String[]>();
		source.add(new String[]{"C", "D", "E", "F"});
		source.add(new String[]{"A", "B"});
		source.add(new String[]{"1", "2"});
		source.add(new String[]{"3", "4"});
		ArrangementCalculatorWithList instance = new ArrangementCalculatorWithList(source);
		C.list(instance.getResult());
	}

	public void exec() {
		String exe = "\"C:\\Program Files\\Google\\Picasa3\\PicasaPhotoViewer.exe";
		String filePath = "E:\\Klose\\s\\20140916_225107.jpg";
		exe = "C:\\Program Files\\TTPlayer\\TTPlayer.exe";
		filePath = "F:\\music\\With an orchid.mp3";
		PanaceaBox.openFile(filePath);
//		PanaceaBox.exec("cmd /c start C:\\apache-pirate\\bin\\BKUE.bat");
//		PanaceaBox.exec("cmd /c start \"C:\\Program Files\\TTPlayer\\TTPlayer.exe\"");
//		PanaceaBox.exec("C:\\apache-pirate\\bin\\BKUE.bat");
	}

	public void test() {
		fail("Not yet impleme你好nted");
	}
	
	public void readFileFromDir() {
	}

	public void copyFile() {
//		String src = "D:/spitter_me.jpg";
//		IOUtil.copy(src, "D:/walter4.jpg");
	}
	
	public void saveFile() {
		String src = "D:/spitter_me.jpg";
		try {
			FileInputStream stream = new FileInputStream(new File(src));
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
