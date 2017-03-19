package com.sirap.basic.output;


public class PDFParams implements OuputParams {

	private boolean isPrintGreyRow = true;
	private boolean isPrintTopInfo = true;
	private boolean useAsianFont = false; 
	
	private String topInfo;
	private int[] cellsWidth = {1};
	private int[] cellsAlign = {0};

	public PDFParams() {
	}
	
	public PDFParams(int[] cellsWidth) {
		this.cellsWidth = cellsWidth;
	}
	
	public PDFParams(int[] cellsWidth, int[] cellsAlign) {
		this.cellsWidth = cellsWidth;
		this.cellsAlign = cellsAlign;
	}
	
	public boolean isPrintGreyRow() {
		return isPrintGreyRow;
	}

	public void setPrintGreyRow(boolean isPrintGreyRow) {
		this.isPrintGreyRow = isPrintGreyRow;
	}

	public boolean isPrintTopInfo() {
		return isPrintTopInfo;
	}

	public void setPrintTopInfo(boolean isPrintTopInfo) {
		this.isPrintTopInfo = isPrintTopInfo;
	}

	public String getTopInfo() {
		return topInfo;
	}

	public void setTopInfo(String topInfo) {
		this.topInfo = topInfo;
	}

	public int[] getCellsWidth() {
		return cellsWidth;
	}

	public void setCellsWidth(int[] cellsWidth) {
		this.cellsWidth = cellsWidth;
	}

	public int[] getCellsAlign() {
		return cellsAlign;
	}

	public void setCellsAlign(int[] cellsAlign) {
		this.cellsAlign = cellsAlign;
	}

	public boolean isUseAsianFont() {
		return useAsianFont;
	}

	public void setUseAsianFont(boolean useAsianFont) {
		this.useAsianFont = useAsianFont;
	}
}
