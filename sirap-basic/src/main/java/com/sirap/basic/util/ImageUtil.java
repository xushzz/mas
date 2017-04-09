package com.sirap.basic.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sirap.basic.exception.MexException;

public class ImageUtil {

	public static String getRealFormat(String filepath) {
		File file = new File(filepath);

		try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				String msg = "No readers found for image [" + filepath + "]";
				throw new MexException(msg);
			}

			ImageReader reader = iter.next();
			String value = reader.getFormatName();

			return value;
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}

	public static int[] readImageWidthHeight(String filepath) {
		File file = new File(filepath);

		try {
			BufferedImage bi = ImageIO.read(file);
			if(bi == null) {
				String msg = "Not valid image [" + filepath + "]";
				throw new MexException(msg);
			}
			
			int[] arr = { bi.getWidth(), bi.getHeight() };

			return arr;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}

}
