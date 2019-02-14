package com.sirap.third.image.qrcode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
 
/**
 * 二维码工具类
 * 
 * @ClassName: BarcodeUtils.java
 * @version: v1.0.0
 * @author: pll
 * @date: 2018年6月4日 下午2:51:54
 */
public class BarcodeUtils {
 
   private static final int QRCOLOR = 0xFF000000; // 二维码颜色 默认是黑色
   private static final int BGWHITE = 0xFFFFFFFF; // 背景颜色
 
   private static final int WIDTH = 215; // 二维码宽
   private static final int HEIGHT = 215; // 二维码高
   private static final int HEIGHT_WORD = 20; // 二维码高
 
 
   /**
    * 用于设置QR二维码参数
    */
   private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
      private static final long serialVersionUID = 1L;
      {
         // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
         put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
         // 设置编码方式
         put(EncodeHintType.CHARACTER_SET, "utf-8");
         put(EncodeHintType.MARGIN, 0);
      }
   };
 
 
   /**
    * 设置 Graphics2D 属性  （抗锯齿）
    * @param graphics2D
    */
   private static void setGraphics2D(Graphics2D graphics2D){
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
      Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
      graphics2D.setStroke(s);
   }
 
   /**
    * @description 生成带logo的二维码图片 二维码下面带文字
    * @param logoFile loge图片的路径
    * @param bgFile 背景图片的路径
    * @param codeFile 图片输出路径
    * @param qrUrl 二维码内容
    * @param words 二维码下面的文字
    */
   public static void drawLogoQRCode(String logoPath, String imagePath, String qrUrl, String words) {
        try {
        	int qrcodeWidth = WIDTH;
        	int qrcodeHeight = HEIGHT;
        	int wordHeight = HEIGHT_WORD;
        	
        	int imageWidth = qrcodeWidth;
        	int imageHeight = qrcodeHeight + wordHeight;
        	
        	int colorQRCode = QRCOLOR;
        	int colorBack = BGWHITE;
        	
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, imageWidth, qrcodeHeight, hints);

            {
                // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
                for (int x = 0; x < imageWidth; x++) {
                    for (int y = 0; y < imageHeight; y++) {
                    	boolean hit = y < qrcodeHeight && bm.get(x, y);
                    	int mycolor = hit ? colorQRCode : colorBack;
                    	image.setRGB(x, y, mycolor);
                    }
                }
            }

            Graphics2D outg = image.createGraphics();
            setGraphics2D(outg);
            
            if (FileUtil.exists(logoPath)) {
                BufferedImage logo = ImageIO.read(FileUtil.getIfNormalFile(logoPath));
                int logoX = (qrcodeWidth - logo.getWidth()) / 2;
                int logoY = (qrcodeHeight - logo.getHeight()) / 2;
                outg.drawImage(logo, logoX, logoY, logo.getWidth(), logo.getHeight(), null);
                logo.flush();
            }
            
            if(EmptyUtil.isNotEmpty(words)) {
            	outg.setColor(Color.GRAY);
                // 字体、字型、字号
            	Font myfont = new Font("微软雅黑", Font.PLAIN, 18);
                outg.setFont(myfont);
                //文字长度
                int strWidth = outg.getFontMetrics(myfont).stringWidth(words);
                int strHeight = outg.getFontMetrics(myfont).getHeight();

                int wordStartX = (imageWidth - strWidth) / 2;
                int wordStartY = qrcodeHeight + wordHeight / 2;
                
                int diff = (wordHeight - strHeight);
                int off = (diff / 2);
                int cut = 2;
                D.pla(diff, off, cut);

                D.pla(strWidth, imageWidth);
                outg.drawString(words, wordStartX, wordStartY + 2);
            }
            
            {
            	//Rectangle
            	outg.setColor(Color.BLUE);
                // 字体、字型、字号
            	outg.setStroke(new BasicStroke(2.0f));
            	outg.drawRect(0, 0, imageWidth - 1, imageHeight - 1);
            }
            
            outg.dispose();
 
            image.flush();
            ImageIO.write(image, "png", new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
   
   public static void main(String[] args) {
      //logo
	   String logo = "E:/KDB/tasks/0110_QRCode/ding.jpgx";
	   //生成图片
      String temp = "E:/KDB/tasks/0110_QRCode/{0}.jpg";
      String image = StrUtil.occupy(temp, DateUtil.timestamp());
        File qrCodeFile = new File(image);
        //二维码内容
        String content = "https://w.url.cn/s/AYmfAV3";
        content = RandomUtil.names(2) + "";
        //二维码下面的文字
        String words = "ajp taylor";
        words = content;
//        words = "whoami.jpg";
        D.pl(image);
        drawLogoQRCode(logo, image, content, words);
        PanaceaBox.openFile(image);
        C.pl(qrCodeFile);
   }
 
}

