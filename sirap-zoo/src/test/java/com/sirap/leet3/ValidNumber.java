package com.sirap.leet3;

import com.sirap.basic.tool.C;

public class ValidNumber {
	
	public static void main(String[] args) {
		ValidNumber james = new ValidNumber();
		C.pl(james.isNumber("56."));
		C.pl(james.isNumber(".56e-12"));
	}
			
	public boolean isNumber(String s) {
        if(s.trim().isEmpty()){
        	return false;
        }
        String regex = "[-+]?(\\d+\\.?|\\.\\d+)\\d*(e[-+]?\\d+)?";
        if(s.trim().matches(regex)){
        	return true;
        }else{
        	return false;
        }
	}
}
