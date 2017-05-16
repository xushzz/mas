package com.sirap.common.entry;

import com.sirap.basic.util.PanaceaBox;
import com.sirap.common.framework.AppBase;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.VerbalJanitor;

public class AppMasVerbal extends AppBase {

	public static void main(String[] args) {
		AppBase app = new AppMasVerbal();
		app.init(args);
		app.run();
	}

	@Override
	public SimpleKonfig getKonfig() {
		return SimpleKonfig.g();
	}

	@Override
	public void initKonfig(String[] args) {
		SimpleKonfig.init(PanaceaBox.firstParam(args), "MasVerbal", PanaceaBox.getParam(args, 1));
		setDelayMinutes(getKonfig().getTimeoutMinutes());
	}

	@Override
	protected Janitor getJanitor() {
		Janitor smith = new VerbalJanitor(getKonfig());
		
		return smith;
	}

	@Override
	protected String prompt() {
		return "V$ ";
	}

	@Override
	protected String system() {
		return "Verbal";
	}
}
