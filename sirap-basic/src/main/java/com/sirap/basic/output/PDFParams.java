package com.sirap.basic.output;


public class PDFParams implements OutputParams {

	private boolean isPrintGreyRow = true;
	private boolean isPrintTopInfo = true;
	private boolean useAsianFont = false; 
	
	private String topInfo;
	private int[] cellsWidth;
	private int[] cellsAlign;

	public PDFParams() {
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

	public int[] getCellWidths() {
		return cellsWidth;
	}

	public void setCellWidths(int[] cellsWidth) {
		this.cellsWidth = cellsWidth;
	}

	public int[] getCellAligns() {
		return cellsAlign;
	}

	public void setCellAligns(int[] cellsAlign) {
		this.cellsAlign = cellsAlign;
	}

	public boolean isUseAsianFont() {
		return useAsianFont;
	}

	public void setUseAsianFont(boolean useAsianFont) {
		this.useAsianFont = useAsianFont;
	}
}
