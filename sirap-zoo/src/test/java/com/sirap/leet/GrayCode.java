package com.sirap.leet;

import java.util.ArrayList;
import java.util.List;

/**
gray code中文名叫格雷码，一看题就知道是模拟类型，写几个例子出来找规律。
以3位格雷码为例。
0 0 0
0 0 1
0 1 1
0 1 0
1 1 0
1 1 1
1 0 1
1 0 0
可以看到第n位的格雷码由两部分构成，一部分是n-1位格雷码，再加上 1<<(n-1)和n-1位格雷码的逆序的和。

找到规律后就好办了，轻松AC。
 */
public class GrayCode {
    public List<Integer> grayCode(int n) {
        if(n==0) {
        	List<Integer> result = new ArrayList<Integer>();
            result.add(0);
            return result;
        }
        
        List<Integer> tmp = grayCode(n-1);
        int addNumber = 1 << (n-1);
        ArrayList<Integer> result = new ArrayList<Integer>(tmp);
        for(int i=tmp.size()-1;i>=0;i--) {
            result.add(addNumber + tmp.get(i));
        }
        return result;
    }
}