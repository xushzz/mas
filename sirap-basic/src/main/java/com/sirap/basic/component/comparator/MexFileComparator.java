package com.sirap.basic.component.comparator;

import java.io.File;
import java.util.Comparator;

import com.sirap.basic.domain.MexFile;

public class MexFileComparator implements Comparator<MexFile> {

	private Boolean byDateAsc;
	private Boolean bySizeAsc;
	private Boolean byTypeAsc;
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
	
	public MexFileComparator setByTypeAsc(Boolean byTypeAsc) {
		this.byTypeAsc = byTypeAsc;
		return this;
	}
	
	public MexFileComparator(boolean byNameAsc) {
		this.byNameAsc= byNameAsc;
	}
	
	private int valueOfType(File file) {
		int value = 0;
		if(file.isDirectory()) {
			value = 1;
		} else if(file.isFile()) {
			value = 2;
		}
		
		return value;
	}
	
	@Override
	public int compare(MexFile ma, MexFile mb) {
		File fa = ma.getFile();
		File fb = mb.getFile();
		
		int value = 0;
		
		if(byTypeAsc != null) {
			value = valueOfType(fa) - valueOfType(fb);
			if(!byTypeAsc) {
				value *= -1;
			}
		}
		
		if(byDateAsc != null) {
			value = fa.lastModified() < fb.lastModified() ? 1 : -1;
			if(byDateAsc) {
				value *= -1;
			}
		}
		
		if(value == 0 && bySizeAsc != null) {
			value = ma.getFileFize() < mb.getFileFize()? 1 : -1;
			if(bySizeAsc) {
				value *= -1;
			}
		}

		if(value == 0) {
			value = fa.getAbsolutePath().toLowerCase().compareTo(fb.getAbsolutePath().toLowerCase());
			if(!byNameAsc) {
				value *=- 1;
			}
		}
		
		return value;
	}
}
