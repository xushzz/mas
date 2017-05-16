package com.sirap.leet;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class RestoreIPAddress2 {

	public static void main(String[] args) {
		RestoreIPAddress2 jk = new RestoreIPAddress2();
		C.list(jk.restoreIpAddresses("101821420"));
	}

	public List<String> restoreIpAddresses(String s) {
		List<String> ret = new ArrayList<String>();
		if (s.length() > 12)
			return ret;
		for (int i = 0; i < s.length(); i++) {// [0, i]
			for (int j = i + 1; j < s.length(); j++) {// [i+1, j]
				for (int k = j + 1; k < s.length() - 1; k++) {// [j+1, k], [k+1,
															// s.size()-1]
					String ip1 = s.substring(0, i + 1);
					String ip2 = s.substring(i + 1, j + 1);
					String ip3 = s.substring(j + 1, k + 1);
					String ip4 = s.substring(k + 1);
					if (check(ip1) && check(ip2) && check(ip3) && check(ip4)) {
						String ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
						ret.add(ip);
					}
				}
			}
		}
		return ret;
	}

	public boolean check(String ip) {
		int value = Integer.parseInt(ip);
		if (ip.charAt(0) == '0') {
			return (ip.length() == 1);
		} else {
			if (value <= 255)
				return true;
			else
				return false;
		}
	}
}
