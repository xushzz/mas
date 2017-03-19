package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

public class PanaceaBox {
	
	public static boolean openFile(String filePath) {
		String temp = wrapWithQuotes(filePath);
		String cmd = "cmd /c call " + temp;
		
		return execute(cmd);
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
			List<String> regularResult = IOUtil.readStreamIntoList(ps.getInputStream(), printAlong);
			result.addAll(regularResult);

			List<String> errorResult = IOUtil.readStreamIntoList(ps.getErrorStream(), printAlong);
			result.addAll(errorResult);
		} catch (IOException e) {
			throw new MexException(e.getMessage());
		}
		
		return result;
	}
	
	public static String firstParam(String[] args) {
		return getParam(args, 0, null);
	}

	public static String getParam(String[] args, int index) {
		return getParam(args, index, null);
	}

	public static String getParam(String[] args, int index, String defaultValue) {
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
}
