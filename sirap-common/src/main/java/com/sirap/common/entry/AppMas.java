package com.sirap.common.entry;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.common.framework.App;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.JanitorMD5;
import com.sirap.common.framework.SimpleKonfig;

public class AppMas extends App {
	
	public static void main(String[] args) {
		App app = new AppMas();
		app.init(args);
		app.run();
	}

	@Override
	public SimpleKonfig getKonfig() {
		return SimpleKonfig.g();
	}

	@Override
	public void initKonfig(String[] args) {
		SimpleKonfig.init(PanaceaBox.firstArgument(args));
		setDelayMinutes(getKonfig().getTimeoutMinutes());
	}

	@Override
	protected Janitor getJanitor() {
		Janitor smith = new JanitorMD5(getKonfig());
		
		return smith;
	}

	@Override
	protected String prompt() {
		String timestamp = DateUtil.displayNow(DateUtil.TIME_ONLY);
		String temp = timestamp + " $ ";
		if(PanaceaBox.isMacOrLinuxOrUnix()) {
			temp = timestamp + " M$ ";
		}
		
		return temp;
	}

	@Override
	protected String system() {
		return "Mas";
	}
}
