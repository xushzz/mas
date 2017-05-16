package com.sirap.leet;

public class DecodeWays {
	public int numDecodings(String s) {
	    if(s==null||s.length()==0||s.equals("0"))
	        return 0;
	 
	 
	    int[] t = new int[s.length()+1];
	    t[0] = 1;
	 
	    //if(s.charAt(0)!='0')
	    if(isValid(s.substring(0,1)))
	        t[1]=1;
	    else
	        t[1]=0;
	 
	    for(int i=2; i<=s.length(); i++){
	        if(isValid(s.substring(i-1,i))){
	            t[i]+=t[i-1];
	        }
	 
	        if(isValid(s.substring(i-2,i))){
	            t[i]+=t[i-2];
	        }
	    }
	 
	    return t[s.length()];
	}
	 
	public boolean isValid(String s){
	    if(s.charAt(0)=='0')
	        return false;
	    int value = Integer.parseInt(s);
	    return value>=1&&value<=26;
	}
}
