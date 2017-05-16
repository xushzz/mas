package com.sirap.common.test;

import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.command.CommandQuit;

public class OjbectTest {
	
	@Test
	public void con() {
		CommandBase obj = ObjectUtil.createInstanceViaConstructor(CommandQuit.class, CommandBase.class);
		
		C.pl(obj);
	}
}
