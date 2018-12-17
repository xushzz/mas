package com.sirap.basic.util;

import java.io.File;
import java.util.Comparator;

public class ComparatorUtils {

	public static Comparator<File> fileLastModifiedAscend() {
		return new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				Long v1 = f1.lastModified();
				Long v2 = f2.lastModified();
//				return v1.compareTo(v2);
				int val = v1.compareTo(v2);
				if(val == 0) {
					return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
				} else {
					return val;
				}
			}
		};
	}
	
	public static Comparator<File> fileNameAscend() {
		return new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
			}
		};
	}
	
	public static Comparator<File> fileLastModifiedDescend() {
		return new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				Long v1 = f1.lastModified();
				Long v2 = f2.lastModified();
				int val = v2.compareTo(v1);
				if(val == 0) {
					return f2.getAbsolutePath().compareTo(f1.getAbsolutePath());
				} else {
					return val;
				}
			}
		};
	}
}
