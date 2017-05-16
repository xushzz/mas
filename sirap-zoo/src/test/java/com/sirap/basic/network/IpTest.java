package com.sirap.basic.network;

import java.net.InetAddress;

public class IpTest implements Runnable{
    private static StringBuffer targetIp;
    public  void searchIp(){
        try {
            InetAddress is = InetAddress.getLocalHost();
            byte[] ip = is.getAddress();
            targetIp = new StringBuffer();
            for(int i=0; i<(ip.length-1); i++) {
                if(i > 0) {
                    targetIp.append(".");
                }
                targetIp.append(ip[i]&0xff);
            }
            //int a = ip[3]&0xff;
            new Thread(this).start();
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
     
    @Override
    public void run() {
        String host = null;
        try {
            for(int s=1; s<254; s++) {
                host = targetIp.toString() + "." + s;
                InetAddress it = InetAddress.getByName(host);
                System.out.println(host);
                String str = it.getHostName();
                if(!str.equals(host)) {
                    System.out.println(host + "/" + str);
                }               
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void main(String[] args) {
        ///new IpTest().searchIp();
    }
}