package com.sirap.third.image.qrcode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class QRCodeHelper {
	
	public static String createImage_OLD(String codeText, String filepath, String format, int width, int height, String show) {
		try {
			Map<EncodeHintType, Object> hintMap = new HashMap<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			hintMap.put(EncodeHintType.CHARACTER_SET, Konstants.CODE_UTF8);
			
			MultiFormatWriter james = new MultiFormatWriter();
			BitMatrix matrix = james.encode(codeText, BarcodeFormat.QR_CODE, width, height, hintMap);
			if(!EmptyUtil.isNullOrEmpty(show)) {
				D.pl();
				BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
				Graphics mike = image.getGraphics();    
		        mike.setColor(Color.WHITE);    
		        mike.fillRect(0, 0, 100, 20);    
		        mike.setColor(Color.RED);    
		        mike.setFont(new Font("Times New Roman", Font.BOLD, 40));    

		        int xPoint = 0;
		        int yPoint = 0;
		        mike.drawString(show, xPoint, yPoint);
		        D.pl(show);
			}
					
			MatrixToImageWriter.writeToFile(matrix, format, new File(filepath));
			
			return filepath;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	public static void createImage(String contents, String logoPath, String imagePath, String info, int width) {
		try {
        	int qrcodeWidth = width;
        	int qrcodeHeight = width;
        	
        	int imageWidth = qrcodeWidth;
        	
        	boolean showInfo = EmptyUtil.isNotEmpty(info);
        	int wordHeight = showInfo ? 30 : 0;
        	int imageHeight = qrcodeHeight + wordHeight;
        	
        	int colorQRCode = 0xFF000000;
        	int colorBack = 0xFFFFFFFF;
        	
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.CHARACTER_SET, Konstants.CODE_UTF8);
            hints.put(EncodeHintType.MARGIN, 0);
//			D.pla(imageWidth, qrcodeHeight);
            BitMatrix bm = multiFormatWriter.encode(contents, BarcodeFormat.QR_CODE, imageWidth, qrcodeHeight, hints);

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
            outg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            outg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            outg.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
            
            if (FileUtil.exists(logoPath)) {
                BufferedImage logo = ImageIO.read(FileUtil.getIfNormalFile(logoPath));
                int logoX = (qrcodeWidth - logo.getWidth()) / 2;
                int logoY = (qrcodeHeight - logo.getHeight()) / 2;
                outg.drawImage(logo, logoX, logoY, logo.getWidth(), logo.getHeight(), null);
                logo.flush();
            }
            
            if(showInfo) {
            	outg.setColor(Color.GRAY);
                // 字体、字型、字号
            	Font myfont = new Font("微软雅黑", Font.PLAIN, 18);
                outg.setFont(myfont);
                //文字长度
                int strWidth = outg.getFontMetrics(myfont).stringWidth(info);

                int wordStartX = (imageWidth - strWidth) / 2;
                int offset = wordHeight / 2 + 3;
                int wordStartY = qrcodeHeight + offset;
                
                outg.drawString(info, wordStartX, wordStartY);
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
            String format = FileUtil.extensionOf(imagePath);
            ImageIO.write(image, format, new File(imagePath));
            
        } catch (Exception ex) {
            XXXUtil.alert(ex);
        }
	}
	
	public static byte[] createBytes(String codeText, String format, int width, int height) {
		try {
			Map<EncodeHintType, Object> hintMap = new HashMap<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			hintMap.put(EncodeHintType.CHARACTER_SET, Konstants.CODE_UTF8);
			
			MultiFormatWriter james = new MultiFormatWriter();
			BitMatrix matrix = james.encode(codeText, BarcodeFormat.QR_CODE, width, height, hintMap);
			BufferedImage buf = MatrixToImageWriter.toBufferedImage(matrix);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buf, format, baos);
			byte[] data = baos.toByteArray();
			
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	public static String decodeImage(String filePath) {
		try {
			BufferedImage buf = ImageIO.read(new FileInputStream(filePath));
			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(buf)));
			
			Map<DecodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
			hintMap.put(DecodeHintType.TRY_HARDER, ErrorCorrectionLevel.L);
			
			Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
			return qrCodeResult.getText();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		//createImage
		//logo
		   String logo = "E:/KDB/tasks/0110_QRCode/ding.jpg";
		   //生成图片
	      String temp = "E:/KDB/tasks/0110_QRCode/{0}.jpg";
	      String image = StrUtil.occupy(temp, DateUtil.timestamp());
	        File qrCodeFile = new File(image);
	        //二维码内容
	        String content = "https://w.url.cn/s/AYmfAV3";
	        content = RandomUtil.names(5) + "";
	        //二维码下面的文字
	        String words = "ajp taylor";
	        words = content;
//	        words = "whoami.jpg";
	        D.pl(image);
	        createImage(content, logo, image, words, 240);
	        PanaceaBox.openFile(image);
	        C.pl(qrCodeFile);
	}
}
