package com.sirap.executor;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.executor.ChefUtil;
import com.sirap.executor.ssh.SSHCommandExecutor;
import com.sirap.security.MrTrump;

public class SSHTest {
	
	public void parseCookbook() {
		String source = "nasaddhost::nasaddhostService";
		String regex = "(\\S+)::(\\S+)";
		String[] params = StrUtil.parseParams(regex, source);
		D.pl(params);
	}
	
	public void extract() {
		String source = "if \"#{node['vip']['S_netmask']}\".strip().empty?() or \"#{node['vip']['S_nic']}\".strip().empty?()  then";
		List<String> records = StrUtil.split(source, ',');
		C.pl(records);
		C.pl(ChefUtil.extractParams(records, "vip"));
	}
	
	@Test
    public void ssh() {
		String ip = MrTrump.decodeBySIRAP("CC5CEFF645EDC32CE0D09ABCEDEE9916", "ninja");
		String username = MrTrump.decodeBySIRAP("6EFB129B314C1D24EE6042A6C1744F1D", "ninja");
		String password = MrTrump.decodeBySIRAP("3655E1DA0F41850E17D76A8AFED1EF4C", "ninja");
        SSHCommandExecutor sshExecutor = SSHCommandExecutor.g2(ip, username, password);
        String cmd = "uname -s -r -v";
//        cmd = "date +%s";
        cmd = "ls";
        C.pl(ip);
        List items = sshExecutor.execute(cmd);
        
        C.list(items);
        C.pl(sshExecutor.getMilliSecondsFrom1970());
        
    }  
}
