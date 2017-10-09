package com.sirap.basic.tool;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.ThreadUtil;

public class ScreenCaptor {

    public ScreenCaptor() {
    }
    
	public RenderedImage captureEntireScreen() {
		try {
	    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			Robot robot = new Robot();
			RenderedImage image = robot.createScreenCapture(new Rectangle(dim));
			
			return image;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
    }

    public RenderedImage captureCurrentWindow() {
    	cleanClipboard();
    	
    	RenderedImage image = null;
    	long delay = 20;
    	
    	while(image == null) {
    		if(delay >= 2000) {
    			break;
    		}
    		image = useClipboard(delay);
    		delay += 10;
    	}
    	
    	return image;
    }
	
	private RenderedImage useClipboard(long delayInMillis) {
		try {
			Robot robot = new Robot();
	        robot.keyPress(KeyEvent.VK_ALT);
	        robot.keyPress(KeyEvent.VK_PRINTSCREEN);
	        robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
	        robot.keyRelease(KeyEvent.VK_ALT);

	        ThreadUtil.sleepInMillis(delayInMillis);

	        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	        RenderedImage image = (RenderedImage) t.getTransferData(DataFlavor.imageFlavor);

	        return image;
		} catch (Exception ex) {
			//throw new MexException(ex);
			
			return null;
		}
    }
	
	private void cleanClipboard() {
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection("");
		sysc.setContents(tText, null);
	}
    
//    public bolcreateImage() {
//        try {
//        	long delay = 20;
//        	RenderedImage image = null;
//        	if(isEntireScreen) {
//        		image = entireScreen();
//        	} else {
//        		cleanClipboard();
//        		while(image == null) {
//            		if(delay >= 2000) {
//            			break;
//            		}
//            		image = isEntireScreen ? entireScreen() : currentWindow(delay);
//            		delay += 10;
//            	}
//        	}
//        	if(image == null) {
//                C.pl(" Uncanny, try again if you will.");
//                return null;
//        	}
//            File file = new File(fileName);
//            boolean flag = ImageIO.write(image, format, file);
//            if(!flag) {
//            	C.pl(" Uncanny, please check image format [" + format + "], make sure one of [" + Konstants.IMG_FORMATS + "]");
//            }
//
//            return file.getAbsolutePath();
//        } catch (Exception ex) {
//        	ex.printStackTrace();
//        }
//        
//        return null;
//    }
}