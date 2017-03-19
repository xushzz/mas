package com.sirap.qrcode;

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

public class QRCodeUtil {
	
	public static String createImage(String codeText, String filepath, String format, int width, int height) {
		try {
			Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			
			MultiFormatWriter james = new MultiFormatWriter();
			BitMatrix matrix = james.encode(codeText, BarcodeFormat.QR_CODE, width, height, hintMap);
			MatrixToImageWriter.writeToFile(matrix, format, new File(filepath));
			
			return filepath;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	public static byte[] createBytes(String codeText, String format, int width, int height) {
		try {
			Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			
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
}
