package com.sirap.basic.output;

public class ConsoleParams implements OuputParams {
	
	private boolean isPrintTotal = true;
	private boolean isToSplit;
	private int charsPerLineWhenSplit = 100;

	public ConsoleParams() {
		
	}
	
	public ConsoleParams(boolean isPrintTotal) {
		this.isPrintTotal = isPrintTotal;
	}

	public ConsoleParams(boolean isPrintTotal, boolean isToSplit) {
		this.isPrintTotal = isPrintTotal;
		this.isToSplit = isToSplit;
	}

	public ConsoleParams(boolean isPrintTotal, boolean isToSplit, int charsPerLine) {
		this.isPrintTotal = isPrintTotal;
		this.isToSplit = isToSplit;
		this.charsPerLineWhenSplit = charsPerLine;
	}

	public boolean isPrintTotal() {
		return isPrintTotal;
	}

	public void setPrintTotal(boolean isPrintTotal) {
		this.isPrintTotal = isPrintTotal;
	}

	public boolean isToSplit() {
		return isToSplit;
	}

	public void setToSplit(boolean isToSplit) {
		this.isToSplit = isToSplit;
	}

	public int getCharsPerLineWhenSplit() {
		return charsPerLineWhenSplit;
	}

	public void setCharsPerLineWhenSplit(int charsPerLineWhenSplit) {
		this.charsPerLineWhenSplit = charsPerLineWhenSplit;
	}
}