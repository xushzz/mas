package com.sirap.common.framework.command.target;

import com.sirap.basic.output.ConsoleParams;

public class TargetConsole extends Target {

	private ConsoleParams params;
	private boolean becauseOfNoSpecifiedTarget;

	public TargetConsole() {}
	
	public TargetConsole(boolean becauseOfNoSpecifiedTarget) {
		this.becauseOfNoSpecifiedTarget = becauseOfNoSpecifiedTarget;
	}
	
	public boolean isBecauseOfNoSpecifiedTarget() {
		return becauseOfNoSpecifiedTarget;
	}

	public void setBecauseOfNoSpecifiedTarget(boolean becauseOfNoSpecifiedTarget) {
		this.becauseOfNoSpecifiedTarget = becauseOfNoSpecifiedTarget;
	}

	@Override
	public ConsoleParams getParams() {
		
		if(params instanceof ConsoleParams) {
			return params;
		}
		
		if(params == null) {
			params = new ConsoleParams();
			return params;
		}
		
		return null;
	}

	public void setParams(ConsoleParams params) {
		this.params = params;
	}
}
