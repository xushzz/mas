package com.sirap.basic.tool;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class CaptchaGenerator {

    private static final int WIDTH_PER_CHAR = 20;

    private Random random = new Random();
    private String text;
    
    public CaptchaGenerator(String text) {
    	this.text = text;
    }
    
    private Color getRandomColor(int fc,int bc){
        if(fc > 255)
            fc = 255;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc-fc-16);
        int g = fc + random.nextInt(bc-fc-14);
        int b = fc + random.nextInt(bc-fc-18);
        return new Color(r,g,b);
    }
    
    public boolean writeImageTo(String filePath) {
    	File file = new File(filePath);
    	try {
        	ImageIO.write(generateBufferedImage(), "JPEG", file);
    		return true;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public byte[] getImageBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(generateBufferedImage(), "JPEG", output);
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private BufferedImage generateBufferedImage() {
    	int stringNum = text.length();
    	int width = stringNum * WIDTH_PER_CHAR;
        int height = 26;
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman",Font.ROMAN_BASELINE,18));
        g.setColor(getRandomColor(110, 133));

        int lineSize = 40;
        for(int i=0;i<=lineSize;i++){
        	drawLine(g, width, height);
        }

        for(int i = 0; i < text.length(); i++){
        	char ch = text.charAt(i);
            drawChar(g, ch, i);
        }
        
        g.dispose();
        
        return image;
    }
    
    /*
     * 绘制字符串
     */
    private void drawChar(Graphics g, char ch, int i){
        g.setFont(new Font("Fixedsys",Font.CENTER_BASELINE,18));
        g.setColor(new Color(random.nextInt(101),random.nextInt(111),random.nextInt(121)));
        g.translate(random.nextInt(3), random.nextInt(3));
        String temp = ch + "";
        g.drawString(temp, 13 * i, 16);
    }
    
    /*
     * 绘制干扰线
     */
    private void drawLine(Graphics g, int width, int height){
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x+xl, y+yl);
    }
}