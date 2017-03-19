package com.sirap.qrcode;

import java.io.File;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
	
public class CommandQRCode extends CommandBase {

	private static final String KEY_QRCODE_ENCODE = "qr";
	private static final String KEY_QRCODE_DECODE = "de";

	public boolean handle() {
		
		String[] params = parseParams(KEY_QRCODE_ENCODE + "\\s(.*?)(|///(.*?))");
		if(params != null) {
			String nameInfo = params[2];
			if(nameInfo == null) {
				nameInfo = "";
			}

			String content = params[0];
			File file = parseFile(content);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					content = IOUtil.readFileWithRegularLineSeparator(filePath);
					if(EmptyUtil.isNullOrEmpty(nameInfo)) {
						nameInfo = FileUtil.extractFilenameWithoutExtension(filePath);
					}
				}
			}

			String[] filenameAndFormat = generateQRCodeImageFilenameAndFormat(nameInfo, content, "png");
			String filepath = filenameAndFormat[0];
			String format = filenameAndFormat[1];
			
			String filePath = QRCodeUtil.createImage(content, filepath, format, 200, 200);
			if (filePath != null) {
				export(filePath);
				tryToOpenGeneratedImage(filePath);
			}
			
			return true;
		}

		String param = parseParam(KEY_QRCODE_DECODE + "\\s+(.+?)");
		if(param != null) {
			File file = parseFile(param);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				String content = QRCodeUtil.decodeImage(filePath);
				export(content);
				
				return true;
			}
		}
		
		return false;
	}
}
