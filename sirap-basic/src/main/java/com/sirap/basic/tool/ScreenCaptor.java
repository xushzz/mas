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
import java.io.File;

import javax.imageio.ImageIO;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.EmptyUtil;

public class ScreenCaptor {

    private String fileName;
    private String format = Konstants.IMG_BMP;
    private boolean isEntireScreen;

    public ScreenCaptor(String fileName, String format, boolean isEntireScreen) {
    	this.fileName = fileName;
    	if(!EmptyUtil.isNullOrEmptyOrBlankOrLiterallyNull(format)) {
        	this.format = format.toLowerCase();
    	}
        this.isEntireScreen = isEntireScreen;
    }

    public String capture() {
    	String filePath = createImage();
    	
    	return filePath;
    }
	
	private static RenderedImage currentWindow(long delayInMillis) {
        try {
            Robot robot = new Robot();

            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_PRINTSCREEN);
            robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
            robot.keyRelease(KeyEvent.VK_ALT);

            Thread.sleep(delayInMillis);

            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            RenderedImage image = (RenderedImage) t.getTransferData(DataFlavor.imageFlavor);
            return image;
        } catch (Exception ex) {
//        	ex.printStackTrace();
        }
        
        return null;
    }
    
	private static RenderedImage entireScreen() {
    	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			Robot robot = new Robot();
			RenderedImage image = robot.createScreenCapture(new Rectangle(d));
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return null;
    }
	
	private void cleanClipboard() {
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection("");
		sysc.setContents(tText, null);
	}
    
    private String createImage() {
        try {
        	long delay = 20;
        	RenderedImage image = null;
        	if(isEntireScreen) {
        		image = entireScreen();
        	} else {
        		cleanClipboard();
        		while(image == null) {
            		if(delay >= 2000) {
            			break;
            		}
            		image = isEntireScreen ? entireScreen() : currentWindow(delay);
            		delay += 10;
            	}
        	}
        	if(image == null) {
                C.pl(" Uncanny, try again if you will.");
                return null;
        	}
            File file = new File(fileName);
            boolean flag = ImageIO.write(image, format, file);
            if(!flag) {
            	C.pl(" Uncanny, please check image format [" + format + "], make sure one of [" + Konstants.IMG_FORMATS + "]");
                return null;
            }

            return file.getAbsolutePath();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        return null;
    }
}