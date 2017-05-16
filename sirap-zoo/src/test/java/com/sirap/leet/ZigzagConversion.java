package com.sirap.leet;

import com.sirap.basic.tool.D;

public class ZigzagConversion {
	
	public static void main(String[] args) {
		String s = "PAYPALISHIRING";
		int nRows = 4;
		String ret = convert(s, nRows);
		D.pl(ret);
	}
	
	public static String convert(String s, int nRows) {  
        if(s == null || s.length()==0 || nRows <=0)  
            return "";  
        if(nRows == 1)  
            return s;
            
        StringBuilder res = new StringBuilder();  
        int size = 2*nRows-2;  
        for(int i=0;i<nRows;i++){  
            for(int j=i;j<s.length();j+=size){  
                res.append(s.charAt(j));  
                if(i != 0 && i != nRows - 1){//except the first row and the last row
                    int temp = j+size-2*i;
                    if(temp<s.length())
                        res.append(s.charAt(temp));
                }
            }                  
        }  
        return res.toString();  
    }
}
