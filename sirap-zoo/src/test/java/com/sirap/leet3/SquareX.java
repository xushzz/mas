package com.sirap.leet3;

public class SquareX {
	public int sqrt(int x) {
	    if(x<0) return -1;
	    if(x==0) return 0;
	    int l=1;
	    int r=x/2+1;
	    while(l<=r)
	    {
	        int m = (l+r)/2;
	        if(m<=x/m && x/(m+1)<m+1)
	            return m;
	        if(x/m<m)
	        {
	            r = m-1;
	        }
	        else
	        {
	            l = m+1;
	        }
	    }
	    return 0;
	}
}
