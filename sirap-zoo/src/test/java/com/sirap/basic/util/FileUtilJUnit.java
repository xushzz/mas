package com.sirap.basic.util;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.logging.Level;

import javax.activation.MimetypesFileTypeMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.datatype.NumberFixedLength;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.junit.Test;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.thirdparty.media.MediaFileUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;

public class FileUtilJUnit {
	
	private static String audioFile = "C:\\Users\\Public\\Music\\Sample Music\\Kalimba.mp3";
	
	@Test
	public void inputAnalyzer() {

		//v >4g
		//v home&>45g
		//v home&>45g>anc
		//v home&>4g&land>@
		String va = "v >4g";
		va = "6.23233232332454545m";
		va = "3.k";
		va = ".5k";
		va = "0.5k";
		
		C.pl(FileSizeInputAnalyzer.isFileSizeOpoeartor(va));
		SizeCriteria so = new SizeCriteria();
		so.parse("~" + va);
		C.pl(so);
	}
	
	public void format() throws Exception {
		NumberFormat pretty = NumberFormat.getNumberInstance();
		pretty.setMaximumFractionDigits(10);
		pretty.setRoundingMode(RoundingMode.HALF_UP);
		
		String s2 = "1,0,2,,,,,,5.23";
		s2 = "9223372036.8547758072";
		Number n1 = pretty.parse(s2);
		D.pl("LAOPO", n1);
		
		boolean flag = n1.doubleValue() > Long.MAX_VALUE;
		C.pl("Too large? " + flag);
		C.pl(n1.doubleValue());
		
		double number = 1025.230;
		String s1 = pretty.format(number);
		D.sink(s1);
	}
	
	//@Test
	public void sizeOper() {
		String va = ">789M";
		va = "~12k";
		va = "=1g";
		va = "<3837b";
		SizeCriteria so = new SizeCriteria();
		so.parse(va);
		C.pl(so);
	}
	
//	@Test
	public void displaySize() {
		//1025230 1,001.2K
		long a1 = 1925230;
		C.pl(a1);
		String s1 = FileUtil.formatFileSize(a1);
		C.pl(s1);
		long g1 = FileUtil.parseFileSize(s1);
		D.pl(g1);
		C.pl(FileUtil.formatFileSize(1024000));
		D.pl(FileUtil.formatFileSize(102760448));
		D.pl(FileUtil.parseFileSize("130.89B"));
		D.pl(FileUtil.formatFileSize(11151));
		D.pl(FileUtil.parseFileSize("98M"));
	}
	
	/****
	 * Kalimba.mp3 5:48
	 * Maid mp3 2:49
	 * Sleep mp3 3:20
	 * apple.wav 
	 */
	//@Test
	public void mime() {
		String va = "C:\\Users\\Public\\Music\\Sample Music\\Sleep Away.mp3"; //application/octet-stream
		va = "D:/Mas/exp/ah java.pdf"; //application/octet-stream
		va = "D:/Mas/exp/ah.pd.txt"; //text/plain
		va = "D:/Mas/exp/ah.pd.txt"; //text/plain
		va = "D:/KDB/docs/KY/OMATP_各种表单_0811.xlsx"; //application/octet-stream
		va = "E:/GitProjects/SIRAP/mas/scripts/SHUN/mas.properties"; //application/octet-stream
		va = "D:/KDB/issues/1215_SqlSingleFile/SQL.txt";
		File file = new File(va);
		C.pl(new MimetypesFileTypeMap().getContentType(file));
	}
	public void media() {
		String va = "C:\\Users\\Public\\Music\\Sample Music\\Sleep Away.mp3";
		D.pl(va, MediaFileUtil.readMp3DurationInSeconds(va));
	}
	
	public void tagger() {
		NumberFixedLength.logger.setLevel(Level.SEVERE);
		AudioFileIO.logger.setLevel(Level.SEVERE); 
		ID3v23Frame.logger.setLevel(Level.SEVERE);  
		ID3v23Tag.logger.setLevel(Level.SEVERE);
		
		try {
			String va = "E:\\GitProjects\\SIRAP\\mas-app\\jars\\apple.wav";
			va = "C:\\Users\\Public\\Music\\Sample Music\\Kalimba.mp3";
			va = "C:\\Users\\Public\\Music\\Sample Music\\Maid with the Flaxen Hair.mp3";
			va = "C:\\Users\\Public\\Music\\Sample Music\\Sleep Away.mp3";
//			va = "C:\\Users\\Public\\Videos\\Sample Videos\\Wildlife.wmv";
//			va = "D:\\KDB\\issues\\1208_DurationForMp3Mp4\\Windows Logoff Sound.wav";
			File file = new File(va);
	        MP3File f = (MP3File)AudioFileIO.read(file);  
	        MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();  
	        int len = audioHeader.getTrackLength();
	        C.pl(len);
	    } catch(Exception ex) {  
	        ex.printStackTrace();
	    }  
	}

//	@Test
	public void duration() {
		
		String vc = "E:\\GitProjects\\SIRAP\\mas-app\\jars\\apple.wav";
		vc = "D:\\KDB\\issues\\1208_DurationForMp3Mp4\\Windows Logoff Sound.wav";
		File file = FileUtil.getIfNormalFile(vc);
		
		C.pl(file.exists());
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AudioFormat format = audioInputStream.getFormat();
		long audioFileLength = file.length();
		int frameSize = format.getFrameSize();
		float frameRate = format.getFrameRate();
		float durationInSeconds = (audioFileLength / (frameSize * frameRate));
		C.pl(durationInSeconds);
	}
//	
//	public void sauron() {
//		String va = "E:\\GitProjects\\SIRAP\\mas-app\\jars\\apple.wav";
//		va = "C:\\Users\\Public\\Music\\Sample Music\\Maid with the Flaxen Hair.smp3";
//		File file = new File(audioFile);
//		C.pl(file);
//		FFMPEGLocator locator = null;//where();
//		Encoder encoder = locator != null ? new Encoder(locator) : new Encoder();
//        try {
//             MultimediaInfo m = encoder.getInfo(file);
//             long ls = m.getDuration();
//             C.pl(ls);
//             //System.out.println("此视频时长为:"+ls/60000+"分"+(ls`000)/1000+"秒！");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//	}
//	
//	public FFMPEGLocator where() {
//		FFMPEGLocator locator = new FFMPEGLocator() {  
//            @Override  
//            protected String getFFMPEGExecutablePath() {  
//                // <ffmpeg_path>是你的ffmpeg.exe路径  
//                return "<ffmpeg_path>\\ffmpeg.exe";  
//            }
//        };
//        
//        return locator;
//	}
	
	//@Test
	public void audio() throws Exception {
		String va = "D:\\KDB\\issues\\1208_DurationForMp3Mp4\\Windows Logoff Sound.wav";
//		va = "E:\\GitProjects\\SIRAP\\mas-app\\jars\\apple.wav";
//		va = "C:\\Users\\Public\\Music\\Sample Music\\Maid with the Flaxen Hair.mp3";
		File file = new File(va);
		C.pl("exists? " + file.exists());
        Clip clip = AudioSystem.getClip();
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        clip.open(ais);
        System.out.println( clip.getMicrosecondLength() / 1000000D + " s" );		
	}
}
