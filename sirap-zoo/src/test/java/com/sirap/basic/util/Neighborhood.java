package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Neighborhood {
	private static String getNeighborhood(String command, String domain) {
		String line = "";

		boolean begin = false;

		StringBuffer sb = new StringBuffer();

		try {
			Process process = Runtime.getRuntime().exec(command);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			while ((line = br.readLine()) != null) {
				line = line.trim();

				// 你的工作组千万不要命名为“命令成功完成。”或者是“The command completed successfully.”
				if (line.endsWith("命令成功完成。")
						|| line.equalsIgnoreCase("The command completed successfully.")) {
					begin = false;

					break;
				}

				if (begin) {
					if (domain.length() > 0) {
						sb.append("  ").append(line).append("\r\n");

						// System.out.println( "得到   "+line );
					} else {
						sb.append(line).append("\r\n");

						// System.out.println( "得到 "+line );

						sb.append(getNeighborhood("net view /domain:" + line,
								line));
					}
				}

				if (line.endsWith("-")) {
					begin = true;
				}
			}

			br.close();

			process.waitFor();
		} catch (IOException ioe) {
			System.out.println(ioe);
		} catch (Exception e) {
			System.out.println(e);
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out
				.println(Neighborhood.getNeighborhood("net view /domain", ""));
	}
}