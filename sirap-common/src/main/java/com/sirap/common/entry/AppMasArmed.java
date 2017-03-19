package com.sirap.common.entry;

import com.sirap.basic.util.PanaceaBox;
import com.sirap.common.framework.AppBase;
import com.sirap.common.framework.ArmedJanitor;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;

public class AppMasArmed extends AppBase {

	public static void main(String[] args) {
		AppBase app = new AppMasArmed();
		app.init(args);
		app.run();
	}

	@Override
	public SimpleKonfig getKonfig() {
		return SimpleKonfig.g();
	}

	public void initKonfig(String[] args) {
		SimpleKonfig.init(PanaceaBox.firstParam(args), "MasArmed", PanaceaBox.getParam(args, 1));
		setDelayMinutes(getKonfig().getTimeoutMinutes());
	}

	@Override
	protected Janitor getJanitor() {
		Janitor smith = new ArmedJanitor(getKonfig());
		
		return smith;
	}

	@Override
	protected String prompt() {
		return "A$ ";
	}

	@Override
	protected String system() {
		return "Armed";
	}
}
