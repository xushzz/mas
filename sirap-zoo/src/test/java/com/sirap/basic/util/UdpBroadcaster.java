package com.sirap.basic.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.sirap.basic.tool.C;


public class UdpBroadcaster {

private static final int PORT = 9876;
private static final String MCAST_ADDR = "FF7E:230::1234";

private static InetAddress GROUP;

public static void main(String[] args) {
    try {
        //GROUP = "";
        Thread server = server();
        server.start();
        Thread.sleep(3000);
        Thread client = client();
        client.start();
        client.join();
    } catch (Exception e) {
        C.pl("Usage : [group-ip] [port]");
    }
}

private static Thread client() {
    return new Thread(new Runnable() {
        public void run() {
            MulticastSocket multicastSocket = null;
            try {
                multicastSocket = new MulticastSocket(PORT);
                multicastSocket.joinGroup(GROUP);
                while (true) {
                    try {
                        byte[] receiveData = new byte[256];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        multicastSocket.receive(receivePacket);
                        C.pl("Client received from : " + receivePacket.getAddress() + ", " + new String(receivePacket.getData()));
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
                multicastSocket.close();
            }
        }
    });
}

private static Thread server() {
    return new Thread(new Runnable() {
        public void run() {
            DatagramSocket serverSocket = null;
            try {
                serverSocket = new DatagramSocket();
                try {
                    while (true) {
                        byte[] sendData = new byte[256];
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, GROUP, PORT);
                        serverSocket.send(sendPacket);
                        ThreadUtil.sleepInMillis(1000);
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    });
}

}