package com.sirap.basic.component.comparator;

import java.io.File;
import java.util.Comparator;

import com.sirap.basic.domain.MexFile;

public class MexFileComparator implements Comparator<MexFile> {

	private Boolean byDateAsc;
	private Boolean bySizeAsc;
	private boolean byNameAsc = true;
	
	public MexFileComparator() {
	}
	
	public MexFileComparator setBySizeAsc(Boolean bySizeAsc) {
		this.bySizeAsc = bySizeAsc;
		return this;
	}
	
	public MexFileComparator setByDateAsc(Boolean byDateAsc) {
		this.byDateAsc = byDateAsc;
		return this;
	}
	
	public MexFileComparator(boolean byNameAsc) {
		this.byNameAsc= byNameAsc;
	}
	
	@Override
	public int compare(MexFile ma, MexFile mb) {
		File fa = ma.getFile();
		File fb = mb.getFile();
		
		int value = 0;
		
		if(byDateAsc != null) {
			value = fa.lastModified() < fb.lastModified() ? 1 : -1;
			if(byDateAsc) {
				value *= -1;
			}
		}
		
		if(value == 0 && bySizeAsc != null) {
			value = fa.length() < fb.length()? 1 : -1;
			if(bySizeAsc) {
				value *= -1;
			}
		}

		if(value == 0) {
			value = fa.getAbsolutePath().compareTo(fb.getAbsolutePath());
			if(!byNameAsc) {
				value *=- 1;
			}
		}
		
		return value;
	}
}
