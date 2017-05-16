package com.sirap.leet;

import com.sirap.basic.tool.C;

public class IntToRoman {

	public static void main(String[] args) {
		String s = toRoman(92);
		int i = toInt(s);
		C.pl(s);
		C.pl(i);
	}
	
	public static String toRoman(int num) {
		String str = "";    
        String [] symbol = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};    
        int [] value = {1000,900,500,400, 100, 90,  50, 40,  10, 9,   5,  4,   1};   
        for(int i=0;num!=0;i++){  
            while(num >= value[i]){  
                num -= value[i];
                str += symbol[i];  
            }  
        }  
        return str;  
	}
	
	public static int toInt(String source) {
		int sum = 0;
        String [] symbol = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};    
        int [] value = {1000,900,500,400, 100, 90,  50, 40,  10, 9,   5,  4,   1};
        for(int i = 0; i < symbol.length; i++) {
        	String tmp = symbol[i];
        	if(source.startsWith(tmp)) {
        		sum += value[i];
        		source = source.substring(tmp.length());
        		i--;
        	}
        }
        
        return sum;
	}
}
