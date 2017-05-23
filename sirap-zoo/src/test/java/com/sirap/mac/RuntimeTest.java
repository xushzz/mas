package com.sirap.mac;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.TrumpUtil;

public class RuntimeTest {

	
	public void run() {
		String cmd = "java";
		List<String> items = PanaceaBox.executeAndRead(cmd);
		C.list(items);
		
	}
	public void seed() {
		String passcode = "Obamacare";
		String sa = "A";
		C.pl(TrumpUtil.encodeBySIRAP(sa, passcode));
		C.pl(TrumpUtil.encodeBySIRAP(sa, passcode));
		C.pl(TrumpUtil.encodeBySIRAP(sa, passcode));
	}
	
	@Test
	public void open() {
		String sa = "/Users/ky/Documents/mas/shot/20170516_115723_QR_abc.png";
//		sa = "/Users/";
		PanaceaBox.openFile(sa);
		String[] arr = {"open", sa};
		try {
//			PanaceaBox.openFile(sa);
			//Runtime.getRuntime().exec(arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean execute(String command) throws MexException {
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
