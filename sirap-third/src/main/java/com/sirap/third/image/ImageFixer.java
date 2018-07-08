package com.sirap.third.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.XXXUtil;

public class ImageFixer {
	
	public String commpress(Object source, String target, long targetSize) {
        try {
        	if(source instanceof String) {
                Thumbnails.of((String)source).scale(1f).toFile(target);  
        	} else if(source instanceof BufferedImage) {
                Thumbnails.of((BufferedImage)source).scale(1f).toFile(target);  
        	} else if(source instanceof InputStream) {
                Thumbnails.of((InputStream)source).scale(1f).toFile(target);  
        	} else if(source instanceof URL) {
                Thumbnails.of((URL)source).scale(1f).toFile(target);  
        	} else if(source instanceof File) {
                Thumbnails.of((File)source).scale(1f).toFile(target);  
        	} else {
        		XXXUtil.alert("Non-supported source: " + source);
        	}
            double accuracy = 0.9;
            commpressRecursively(target, targetSize, accuracy);  
        } catch (Exception ex) {
        	new MexException(ex);
        }  
        return target;
    }  
  
    private void commpressRecursively(String filePath, long targetSize, double accuracy) throws IOException {  
        File nick = new File(filePath);  
        if (nick.length() <= targetSize) {  
            return;
        }
        
        BufferedImage lucy = ImageIO.read(nick);  
        int width = (int)(lucy.getWidth() * accuracy);
        int height = (int)(lucy.getHeight() * accuracy);
        
        Thumbnails.of(filePath).size(width, height).outputQuality(accuracy).toFile(filePath);  
        commpressRecursively(filePath, targetSize, accuracy);  
    }
}
