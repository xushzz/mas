package com.sirap.basic.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.sirap.basic.tool.C;

public class NeighborScan {
	@Test
	public void scan() throws Exception {
		Runtime r = Runtime.getRuntime();
		String cmd = null;
		cmd = "ipconfig /all";
		cmd = "ipconfig";
		//cmd = "arp -a"; 
        Process p = r.exec(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String inline = br.readLine();
        while (inline != null) {
        	C.pl(inline);
        	inline = br.readLine();
        }
	}
}
