//package com.sirap.common.test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.net.ftp.FTPClient;
//import org.testng.annotations.Test;
//
//public class FTPTest {
//	
//	@Test
//	public void testUpload() { 
//        FTPClient ftpClient = new FTPClient(); 
//        FileInputStream fis = null; 
//
//        try { 
//            ftpClient.connect("localhost"); 
//            ftpClient.login("pirate", "withoutsea"); 
//
//            String fileName = "Homeland.S02E11.The Motherfucker with a Turban.mkv";
//            File srcFile = new File("F:\\Series\\Homeland\\" + fileName); 
//            fis = new FileInputStream(srcFile); 
//            //设置上传目录 
//            ftpClient.makeDirectory("music");
//            ftpClient.changeWorkingDirectory("/music"); 
//            ftpClient.setBufferSize(1024); 
//            ftpClient.setControlEncoding("GBK"); 
//            //设置文件类型（二进制） 
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
//            ftpClient.storeFile(fileName, fis); 
//        } catch (IOException e) { 
//            e.printStackTrace(); 
//            throw new RuntimeException("FTP客户端出错！", e); 
//        } finally { 
//            IOUtils.closeQuietly(fis); 
//            try { 
//                ftpClient.disconnect(); 
//            } catch (IOException e) { 
//                e.printStackTrace(); 
//                throw new RuntimeException("关闭FTP连接发生异常！", e); 
//            } 
//        } 
//    } 
//}
