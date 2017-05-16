package com.sirap.basic.tool;

import java.io.FileInputStream;
import java.io.IOException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import com.sirap.basic.util.ThreadUtil;

@SuppressWarnings("restriction")
public class MexedAudioPlayer {
	
	private AudioStream as;
    private String filePath;
 
    public MexedAudioPlayer(String filePath) {
    	this.filePath = filePath;
    	
    	init();
    }
    
	private void init() {
    	try {
			as = new AudioStream(new FileInputStream(filePath));
		} catch (IOException e) {
			C.pr("[Silent Hill]");
		}
    }
 
    public void play() {
    	if(as == null) {
    		return;
    	}

    	ThreadUtil.executeInNewThread(new Runnable() {
			@Override
			public void run() {
				AudioPlayer.player.start(as);
				ThreadUtil.sleepInSeconds(3);
				AudioPlayer.player.stop(as);
				
				try {
					as.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
    }
 
	public void stop() {
        if(as != null) {
        	AudioPlayer.player.stop(as);
        }
    }
}