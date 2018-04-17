package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

public class PanaceaBox {
	
	public static boolean openFile(String filePath) {
		String temp = wrapWithQuotes(filePath);
		String cmd = "";
		if(isWindows()) {
			cmd = "cmd /c call " + temp;
			execute(cmd);
		} else if(isMac()) {
			String[] arr = {"open", filePath};
			try {
				Runtime.getRuntime().exec(arr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new UnsupportedOperationException("shit happens.");
		}
		
		return true;
	}
	
	public static boolean openFile(String opener, String filePath) {
		String fullCommand = buildFullCommand(opener, filePath);
		
		return execute(fullCommand);
	}
	
	private static String wrapWithQuotes(String source) {
		String quote = "\"";
		
		StringBuffer sb = new StringBuffer();
		if(!source.startsWith(quote)) {
			sb.append(quote);
		}
		sb.append(source);
		if(!source.endsWith(quote)) {
			sb.append(quote);
		}
		
		return sb.toString();
	}
	
	private static String buildFullCommand(String... params) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < params.length; i++) {
			String param = params[i];
			if(param == null) {
				continue;
			}
			
			String temp = wrapWithQuotes(param);
			sb.append(temp);
			
			if(i != params.length - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}
	
	public static boolean execute(String command) throws MexException {
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static List<String> executeAndRead(String command) throws MexException {
		return executeAndRead(command, false);
	}
	
	public static List<String> executeAndRead(String command, boolean printAlong) throws MexException {
		List<String> result = new ArrayList<>();
		try {
			Process ps = Runtime.getRuntime().exec(command);
			List<String> regularResult = readStreamIntoList(ps.getInputStream(), printAlong);
			result.addAll(regularResult);

			List<String> errorResult = readStreamIntoList(ps.getErrorStream(), printAlong);
			result.addAll(errorResult);
		} catch (IOException e) {
			throw new MexException(e.getMessage());
		}
		
		return result;
	}
	
	public static List<String> readStreamIntoList(InputStream ins, boolean printAlong) {
		List<String> list = new ArrayList<String>();
		
		try {
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(isr);
			String record;
			while ((record = br.readLine()) != null) {
				if(printAlong) {
					C.pl(record);
				}
				list.add(record);
			}

			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
	}
	
	public static String firstArgument(String[] args) {
		return getArgument(args, 0, null);
	}

	public static String getArgument(String[] args, int index) {
		return getArgument(args, index, null);
	}

	public static String getArgument(String[] args, int index, String defaultValue) {
		XXXUtil.nullCheck(args, ":Found no program argument, please check...");
		
		if(index >= 0 && index < args.length) {
			return args[index];
		} else {
			return defaultValue;
		}
	}
	
	public static String askForInput(String prompt) {
		C.pr(prompt);
		
		try {
			BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
			String input = systemReader.readLine();
			if(input != null) {
				return input.trim();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static String askForHiddenInput(String prompt) {
		C.pr(prompt);

		Console cons = System.console();
		if(cons == null) {
			return null;
		}
		
		char[] passwd = cons.readPassword(); 
		
		String input = new String(passwd);
		
		return input;
	}
	
	public static String getOperatingSystemName() {
		return System.getProperty("os.name");
	}
	
	public static boolean isWindows() {
		String name = getOperatingSystemName();
		boolean flag = StrUtil.contains(name, Konstants.OS_WINDOWS);
		
		return flag;
	}
	
	public static boolean isMac() {
		String name = getOperatingSystemName();
		boolean flag = StrUtil.contains(name, Konstants.OS_MAC);
		
		return flag;
	}
	
	public static boolean isMacOrLinuxOrUnix() {
		String name = getOperatingSystemName();
		List<String> items = new ArrayList<>();
		items.add(Konstants.OS_LINUX);
		items.add(Konstants.OS_UNIX);
		items.add(Konstants.OS_MAC);
		boolean flag = StrUtil.containsIgnoreCase(name, items);

		return flag;
	}
}
