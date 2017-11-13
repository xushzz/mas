package com.sirap.basic.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.util.CollUtil;

public class AsciiData {
	public static Map<Integer, String> EGGS = new LinkedHashMap<Integer, String>();
	static {
		EGGS.put(0, "NUL(null)");
		EGGS.put(1, "SOH(start of headling)");
		EGGS.put(2, "STX(start of text)");
		EGGS.put(3, "ETX(end of text)");
		EGGS.put(4, "EOT(end of transmission)");
		EGGS.put(5, "ENQ(enquiry)");
		EGGS.put(6, "ACK(acknowledge)");
		EGGS.put(7, "BEL(bell)");
		EGGS.put(8, "BS(backspace)");
		EGGS.put(9, "HT(horizontal tab)");
		EGGS.put(10, "LF(NL line feed, new line)");
		EGGS.put(11, "VT(vertical tab)");
		EGGS.put(12, "FF(NP form feed, new page)");
		EGGS.put(13, "CR(carriage return)");
		EGGS.put(14, "SO(shift out)");
		EGGS.put(15, "SI(shift in)");
		EGGS.put(16, "DLE(data link escape)");
		EGGS.put(17, "DC1(device control 1)");
		EGGS.put(18, "DC2(device control 2)");
		EGGS.put(19, "DC3(device control 3)");
		EGGS.put(20, "DC4(device control 4)");
		EGGS.put(21, "NAK(negative acknowledge)");
		EGGS.put(22, "SYN(synchronous idle)");
		EGGS.put(23, "ETB(end of trans. block)");
		EGGS.put(24, "CAN(cancel)");
		EGGS.put(25, "EM(end of medium)");
		EGGS.put(26, "SUB(substitute)");
		EGGS.put(27, "ESC(escape)");
		EGGS.put(28, "FS(file separator)");
		EGGS.put(29, "GS(group separator)");
		EGGS.put(30, "RS(record separator)");
		EGGS.put(31, "US(unit separator)");
		EGGS.put(32, "(space)");
		EGGS.put(127, "DEL(delete)");
		EGGS.put(65, "Alpha");
		EGGS.put(97, "Alpha");
		EGGS.put(66, "Bravo");
		EGGS.put(98, "Bravo");
		EGGS.put(67, "Charlie");
		EGGS.put(99, "Charlie");
		EGGS.put(68, "Delta");
		EGGS.put(100, "Delta");
		EGGS.put(69, "Echo");
		EGGS.put(101, "Echo");
		EGGS.put(70, "Foxtrot");
		EGGS.put(102, "Foxtrot");
		EGGS.put(71, "Golf");
		EGGS.put(103, "Golf");
		EGGS.put(72, "Hotel");
		EGGS.put(104, "Hotel");
		EGGS.put(73, "India");
		EGGS.put(105, "India");
		EGGS.put(74, "Juliet");
		EGGS.put(106, "Juliet");
		EGGS.put(75, "Kilo");
		EGGS.put(107, "Kilo");
		EGGS.put(76, "Lima");
		EGGS.put(108, "Lima");
		EGGS.put(77, "Mike");
		EGGS.put(109, "Mike");
		EGGS.put(78, "November");
		EGGS.put(110, "November");
		EGGS.put(79, "Oscar");
		EGGS.put(111, "Oscar");
		EGGS.put(80, "Papa");
		EGGS.put(112, "Papa");
		EGGS.put(81, "Quebec");
		EGGS.put(113, "Quebec");
		EGGS.put(82, "Romeo");
		EGGS.put(114, "Romeo");
		EGGS.put(83, "Sierra");
		EGGS.put(115, "Sierra");
		EGGS.put(84, "Tango");
		EGGS.put(116, "Tango");
		EGGS.put(85, "Uniform");
		EGGS.put(117, "Uniform");
		EGGS.put(86, "Victor");
		EGGS.put(118, "Victor");
		EGGS.put(87, "Whiskey");
		EGGS.put(119, "Whiskey");
		EGGS.put(88, "X-ray");
		EGGS.put(120, "X-ray");
		EGGS.put(89, "Yankee");
		EGGS.put(121, "Yankee");
		EGGS.put(90, "Zulu");
		EGGS.put(122, "Zulu");
	}

	public static List<String> eggs() {
		return CollUtil.toList(EGGS, ", ");
	}
}
