package com.sirap.common.entry;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.common.framework.AppBase;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;

public class AppMas extends AppBase {
	
	public static void main(String[] args) {
		AppBase app = new AppMas();
		app.init(args);
		app.run();
	}

	@Override
	public SimpleKonfig getKonfig() {
		return SimpleKonfig.g();
	}

	@Override
	public void initKonfig(String[] args) {
		SimpleKonfig.init(PanaceaBox.firstParam(args), "Mas", PanaceaBox.getParam(args, 1));
	}

	@Override
	protected Janitor getJanitor() {
		Janitor smith = new Janitor(getKonfig());
		
		return smith;
	}

	@Override
	protected String prompt() {
		String timestamp = DateUtil.displayNow(DateUtil.TIME_ONLY);
		String temp = timestamp + " $ ";
		if(PanaceaBox.isMac()) {
			temp = timestamp + " M$ ";
		}
		
		return temp;
	}

	@Override
	protected String system() {
		return "Mas";
	}
}
