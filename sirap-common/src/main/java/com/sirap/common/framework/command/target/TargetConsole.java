package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.output.ConsoleParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.common.framework.Stash;

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
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		simplePrint(records, params);
	}
	
	public static void simplePrint(List records, ConsoleParams params) {
		if(params == null) {
			params = new ConsoleParams(true, false);
		}
		
		if(params.isToSplit()) {
			for(Object record:records) {
				List<String> splittedRecords = CollUtil.splitIntoRecords(record + "", params.getCharsPerLineWhenSplit());
				C.listWithoutTotal(splittedRecords);
			}
			
			if(records.size() > 5 && params.isPrintTotal()) {
				C.total(records.size());
			}
		} else {
			Object startObj = Stash.g().readAndRemove(Stash.KEY_START_IN_MILLIS);
			if(startObj instanceof Long) {
				long start = (Long)startObj;
				C.list(records, params.isPrintTotal(), start);
			} else {
				C.list(records, params.isPrintTotal());
			}
		}

		C.pl();
		return;
	}
}
