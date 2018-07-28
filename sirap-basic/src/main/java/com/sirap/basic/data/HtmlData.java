package com.sirap.basic.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class HtmlData {
	
	public static int INDEX_CODE = 2;
	public static final AlinkMap<String, ValuesItem> EGGS = Amaps.newLinkHashMap();
	public static String SCRIPT = "EGGS.put(\"{0}\", new ValuesItem((char){1}, \"{0}\", {1}, \"{2}\"));";
	static {
		EGGS.put("quot", new ValuesItem((char)34, "quot", 34, "quotation mark"));
		EGGS.put("apos", new ValuesItem((char)39, "apos", 39, "apostrophe&nbsp;"));
		EGGS.put("amp", new ValuesItem((char)38, "amp", 38, "ampersand"));
		EGGS.put("lt", new ValuesItem((char)60, "lt", 60, "less-than"));
		EGGS.put("gt", new ValuesItem((char)62, "gt", 62, "greater-than"));
		EGGS.put("nbsp", new ValuesItem((char)160, "nbsp", 160, "non-breaking space"));
		EGGS.put("iexcl", new ValuesItem((char)161, "iexcl", 161, "inverted exclamation mark"));
		EGGS.put("cent", new ValuesItem((char)162, "cent", 162, "cent"));
		EGGS.put("pound", new ValuesItem((char)163, "pound", 163, "pound"));
		EGGS.put("curren", new ValuesItem((char)164, "curren", 164, "currency"));
		EGGS.put("yen", new ValuesItem((char)165, "yen", 165, "yen"));
		EGGS.put("brvbar", new ValuesItem((char)166, "brvbar", 166, "broken vertical bar"));
		EGGS.put("sect", new ValuesItem((char)167, "sect", 167, "section"));
		EGGS.put("uml", new ValuesItem((char)168, "uml", 168, "spacing diaeresis"));
		EGGS.put("copy", new ValuesItem((char)169, "copy", 169, "copyright"));
		EGGS.put("ordf", new ValuesItem((char)170, "ordf", 170, "feminine ordinal indicator"));
		EGGS.put("laquo", new ValuesItem((char)171, "laquo", 171, "angle quotation mark (left)"));
		EGGS.put("not", new ValuesItem((char)172, "not", 172, "negation"));
		EGGS.put("shy", new ValuesItem((char)173, "shy", 173, "soft hyphen"));
		EGGS.put("reg", new ValuesItem((char)174, "reg", 174, "registered trademark"));
		EGGS.put("macr", new ValuesItem((char)175, "macr", 175, "spacing macron"));
		EGGS.put("deg", new ValuesItem((char)176, "deg", 176, "degree"));
		EGGS.put("plusmn", new ValuesItem((char)177, "plusmn", 177, "plus-or-minus&nbsp;"));
		EGGS.put("acute", new ValuesItem((char)180, "acute", 180, "spacing acute"));
		EGGS.put("micro", new ValuesItem((char)181, "micro", 181, "micro"));
		EGGS.put("para", new ValuesItem((char)182, "para", 182, "paragraph"));
		EGGS.put("middot", new ValuesItem((char)183, "middot", 183, "middle dot"));
		EGGS.put("cedil", new ValuesItem((char)184, "cedil", 184, "spacing cedilla"));
		EGGS.put("ordm", new ValuesItem((char)186, "ordm", 186, "masculine ordinal indicator"));
		EGGS.put("raquo", new ValuesItem((char)187, "raquo", 187, "angle quotation mark (right)"));
		EGGS.put("iquest", new ValuesItem((char)191, "iquest", 191, "inverted question mark"));
		EGGS.put("times", new ValuesItem((char)215, "times", 215, "multiplication"));
		EGGS.put("divide", new ValuesItem((char)247, "divide", 247, "division"));
		EGGS.put("Agrave", new ValuesItem((char)192, "Agrave", 192, "capital a, grave accent"));
		EGGS.put("Aacute", new ValuesItem((char)193, "Aacute", 193, "capital a, acute accent"));
		EGGS.put("Acirc", new ValuesItem((char)194, "Acirc", 194, "capital a, circumflex accent"));
		EGGS.put("Atilde", new ValuesItem((char)195, "Atilde", 195, "capital a, tilde"));
		EGGS.put("Auml", new ValuesItem((char)196, "Auml", 196, "capital a, umlaut mark"));
		EGGS.put("Aring", new ValuesItem((char)197, "Aring", 197, "capital a, ring"));
		EGGS.put("AElig", new ValuesItem((char)198, "AElig", 198, "capital ae"));
		EGGS.put("Ccedil", new ValuesItem((char)199, "Ccedil", 199, "capital c, cedilla"));
		EGGS.put("Egrave", new ValuesItem((char)200, "Egrave", 200, "capital e, grave accent"));
		EGGS.put("Eacute", new ValuesItem((char)201, "Eacute", 201, "capital e, acute accent"));
		EGGS.put("Ecirc", new ValuesItem((char)202, "Ecirc", 202, "capital e, circumflex accent"));
		EGGS.put("Euml", new ValuesItem((char)203, "Euml", 203, "capital e, umlaut mark"));
		EGGS.put("Igrave", new ValuesItem((char)204, "Igrave", 204, "capital i, grave accent"));
		EGGS.put("Iacute", new ValuesItem((char)205, "Iacute", 205, "capital i, acute accent"));
		EGGS.put("Icirc", new ValuesItem((char)206, "Icirc", 206, "capital i, circumflex accent"));
		EGGS.put("Iuml", new ValuesItem((char)207, "Iuml", 207, "capital i, umlaut mark"));
		EGGS.put("ETH", new ValuesItem((char)208, "ETH", 208, "capital eth, Icelandic"));
		EGGS.put("Ntilde", new ValuesItem((char)209, "Ntilde", 209, "capital n, tilde"));
		EGGS.put("Ograve", new ValuesItem((char)210, "Ograve", 210, "capital o, grave accent"));
		EGGS.put("Oacute", new ValuesItem((char)211, "Oacute", 211, "capital o, acute accent"));
		EGGS.put("Ocirc", new ValuesItem((char)212, "Ocirc", 212, "capital o, circumflex accent"));
		EGGS.put("Otilde", new ValuesItem((char)213, "Otilde", 213, "capital o, tilde"));
		EGGS.put("Ouml", new ValuesItem((char)214, "Ouml", 214, "capital o, umlaut mark"));
		EGGS.put("Oslash", new ValuesItem((char)216, "Oslash", 216, "capital o, slash"));
		EGGS.put("Ugrave", new ValuesItem((char)217, "Ugrave", 217, "capital u, grave accent"));
		EGGS.put("Uacute", new ValuesItem((char)218, "Uacute", 218, "capital u, acute accent"));
		EGGS.put("Ucirc", new ValuesItem((char)219, "Ucirc", 219, "capital u, circumflex accent"));
		EGGS.put("Uuml", new ValuesItem((char)220, "Uuml", 220, "capital u, umlaut mark"));
		EGGS.put("Yacute", new ValuesItem((char)221, "Yacute", 221, "capital y, acute accent"));
		EGGS.put("THORN", new ValuesItem((char)222, "THORN", 222, "capital THORN, Icelandic"));
		EGGS.put("szlig", new ValuesItem((char)223, "szlig", 223, "small sharp s, German"));
		EGGS.put("agrave", new ValuesItem((char)224, "agrave", 224, "small a, grave accent"));
		EGGS.put("aacute", new ValuesItem((char)225, "aacute", 225, "small a, acute accent"));
		EGGS.put("acirc", new ValuesItem((char)226, "acirc", 226, "small a, circumflex accent"));
		EGGS.put("atilde", new ValuesItem((char)227, "atilde", 227, "small a, tilde"));
		EGGS.put("auml", new ValuesItem((char)228, "auml", 228, "small a, umlaut mark"));
		EGGS.put("aring", new ValuesItem((char)229, "aring", 229, "small a, ring"));
		EGGS.put("aelig", new ValuesItem((char)230, "aelig", 230, "small ae"));
		EGGS.put("ccedil", new ValuesItem((char)231, "ccedil", 231, "small c, cedilla"));
		EGGS.put("egrave", new ValuesItem((char)232, "egrave", 232, "small e, grave accent"));
		EGGS.put("eacute", new ValuesItem((char)233, "eacute", 233, "small e, acute accent"));
		EGGS.put("ecirc", new ValuesItem((char)234, "ecirc", 234, "small e, circumflex accent"));
		EGGS.put("euml", new ValuesItem((char)235, "euml", 235, "small e, umlaut mark"));
		EGGS.put("igrave", new ValuesItem((char)236, "igrave", 236, "small i, grave accent"));
		EGGS.put("iacute", new ValuesItem((char)237, "iacute", 237, "small i, acute accent"));
		EGGS.put("icirc", new ValuesItem((char)238, "icirc", 238, "small i, circumflex accent"));
		EGGS.put("iuml", new ValuesItem((char)239, "iuml", 239, "small i, umlaut mark"));
		EGGS.put("eth", new ValuesItem((char)240, "eth", 240, "small eth, Icelandic"));
		EGGS.put("ntilde", new ValuesItem((char)241, "ntilde", 241, "small n, tilde"));
		EGGS.put("ograve", new ValuesItem((char)242, "ograve", 242, "small o, grave accent"));
		EGGS.put("oacute", new ValuesItem((char)243, "oacute", 243, "small o, acute accent"));
		EGGS.put("ocirc", new ValuesItem((char)244, "ocirc", 244, "small o, circumflex accent"));
		EGGS.put("otilde", new ValuesItem((char)245, "otilde", 245, "small o, tilde"));
		EGGS.put("ouml", new ValuesItem((char)246, "ouml", 246, "small o, umlaut mark"));
		EGGS.put("oslash", new ValuesItem((char)248, "oslash", 248, "small o, slash"));
		EGGS.put("ugrave", new ValuesItem((char)249, "ugrave", 249, "small u, grave accent"));
		EGGS.put("uacute", new ValuesItem((char)250, "uacute", 250, "small u, acute accent"));
		EGGS.put("ucirc", new ValuesItem((char)251, "ucirc", 251, "small u, circumflex accent"));
		EGGS.put("uuml", new ValuesItem((char)252, "uuml", 252, "small u, umlaut mark"));
		EGGS.put("yacute", new ValuesItem((char)253, "yacute", 253, "small y, acute accent"));
		EGGS.put("thorn", new ValuesItem((char)254, "thorn", 254, "small thorn, Icelandic"));
		EGGS.put("yuml", new ValuesItem((char)255, "yuml", 255, "small y, umlaut mark"));
		EGGS.put("forall", new ValuesItem((char)8704, "forall", 8704, "for all"));
		EGGS.put("part", new ValuesItem((char)8706, "part", 8706, "part"));
		EGGS.put("exists", new ValuesItem((char)8707, "exists", 8707, "exists"));
		EGGS.put("empty", new ValuesItem((char)8709, "empty", 8709, "empty"));
		EGGS.put("nabla", new ValuesItem((char)8711, "nabla", 8711, "nabla"));
		EGGS.put("isin", new ValuesItem((char)8712, "isin", 8712, "isin"));
		EGGS.put("notin", new ValuesItem((char)8713, "notin", 8713, "notin"));
		EGGS.put("ni", new ValuesItem((char)8715, "ni", 8715, "ni"));
		EGGS.put("prod", new ValuesItem((char)8719, "prod", 8719, "prod"));
		EGGS.put("sum", new ValuesItem((char)8721, "sum", 8721, "sum"));
		EGGS.put("minus", new ValuesItem((char)8722, "minus", 8722, "minus"));
		EGGS.put("lowast", new ValuesItem((char)8727, "lowast", 8727, "lowast"));
		EGGS.put("radic", new ValuesItem((char)8730, "radic", 8730, "square root"));
		EGGS.put("prop", new ValuesItem((char)8733, "prop", 8733, "proportional to"));
		EGGS.put("infin", new ValuesItem((char)8734, "infin", 8734, "infinity"));
		EGGS.put("ang", new ValuesItem((char)8736, "ang", 8736, "angle"));
		EGGS.put("and", new ValuesItem((char)8743, "and", 8743, "and"));
		EGGS.put("or", new ValuesItem((char)8744, "or", 8744, "or"));
		EGGS.put("cap", new ValuesItem((char)8745, "cap", 8745, "cap"));
		EGGS.put("cup", new ValuesItem((char)8746, "cup", 8746, "cup"));
		EGGS.put("int", new ValuesItem((char)8747, "int", 8747, "integral"));
		EGGS.put("sim", new ValuesItem((char)8764, "sim", 8764, "simular to"));
		EGGS.put("cong", new ValuesItem((char)8773, "cong", 8773, "approximately equal"));
		EGGS.put("asymp", new ValuesItem((char)8776, "asymp", 8776, "almost equal"));
		EGGS.put("ne", new ValuesItem((char)8800, "ne", 8800, "not equal"));
		EGGS.put("equiv", new ValuesItem((char)8801, "equiv", 8801, "equivalent"));
		EGGS.put("le", new ValuesItem((char)8804, "le", 8804, "less or equal"));
		EGGS.put("ge", new ValuesItem((char)8805, "ge", 8805, "greater or equal"));
		EGGS.put("sub", new ValuesItem((char)8834, "sub", 8834, "subset of"));
		EGGS.put("sup", new ValuesItem((char)8835, "sup", 8835, "superset of"));
		EGGS.put("nsub", new ValuesItem((char)8836, "nsub", 8836, "not subset of"));
		EGGS.put("sube", new ValuesItem((char)8838, "sube", 8838, "subset or equal"));
		EGGS.put("supe", new ValuesItem((char)8839, "supe", 8839, "superset or equal"));
		EGGS.put("oplus", new ValuesItem((char)8853, "oplus", 8853, "circled plus"));
		EGGS.put("otimes", new ValuesItem((char)8855, "otimes", 8855, "cirled times"));
		EGGS.put("perp", new ValuesItem((char)8869, "perp", 8869, "perpendicular"));
		EGGS.put("sdot", new ValuesItem((char)8901, "sdot", 8901, "dot operator"));
		EGGS.put("Alpha", new ValuesItem((char)913, "Alpha", 913, "Alpha"));
		EGGS.put("Beta", new ValuesItem((char)914, "Beta", 914, "Beta"));
		EGGS.put("Gamma", new ValuesItem((char)915, "Gamma", 915, "Gamma"));
		EGGS.put("Delta", new ValuesItem((char)916, "Delta", 916, "Delta"));
		EGGS.put("Epsilon", new ValuesItem((char)917, "Epsilon", 917, "Epsilon"));
		EGGS.put("Zeta", new ValuesItem((char)918, "Zeta", 918, "Zeta"));
		EGGS.put("Eta", new ValuesItem((char)919, "Eta", 919, "Eta"));
		EGGS.put("Theta", new ValuesItem((char)920, "Theta", 920, "Theta"));
		EGGS.put("Iota", new ValuesItem((char)921, "Iota", 921, "Iota"));
		EGGS.put("Kappa", new ValuesItem((char)922, "Kappa", 922, "Kappa"));
		EGGS.put("Lambda", new ValuesItem((char)923, "Lambda", 923, "Lambda"));
		EGGS.put("Mu", new ValuesItem((char)924, "Mu", 924, "Mu"));
		EGGS.put("Nu", new ValuesItem((char)925, "Nu", 925, "Nu"));
		EGGS.put("Xi", new ValuesItem((char)926, "Xi", 926, "Xi"));
		EGGS.put("Omicron", new ValuesItem((char)927, "Omicron", 927, "Omicron"));
		EGGS.put("Pi", new ValuesItem((char)928, "Pi", 928, "Pi"));
		EGGS.put("Rho", new ValuesItem((char)929, "Rho", 929, "Rho"));
		EGGS.put("Sigma", new ValuesItem((char)931, "Sigma", 931, "Sigma"));
		EGGS.put("Tau", new ValuesItem((char)932, "Tau", 932, "Tau"));
		EGGS.put("Upsilon", new ValuesItem((char)933, "Upsilon", 933, "Upsilon"));
		EGGS.put("Phi", new ValuesItem((char)934, "Phi", 934, "Phi"));
		EGGS.put("Chi", new ValuesItem((char)935, "Chi", 935, "Chi"));
		EGGS.put("Psi", new ValuesItem((char)936, "Psi", 936, "Psi"));
		EGGS.put("Omega", new ValuesItem((char)937, "Omega", 937, "Omega"));
		EGGS.put("alpha", new ValuesItem((char)945, "alpha", 945, "alpha"));
		EGGS.put("beta", new ValuesItem((char)946, "beta", 946, "beta"));
		EGGS.put("gamma", new ValuesItem((char)947, "gamma", 947, "gamma"));
		EGGS.put("delta", new ValuesItem((char)948, "delta", 948, "delta"));
		EGGS.put("epsilon", new ValuesItem((char)949, "epsilon", 949, "epsilon"));
		EGGS.put("zeta", new ValuesItem((char)950, "zeta", 950, "zeta"));
		EGGS.put("eta", new ValuesItem((char)951, "eta", 951, "eta"));
		EGGS.put("theta", new ValuesItem((char)952, "theta", 952, "theta"));
		EGGS.put("iota", new ValuesItem((char)953, "iota", 953, "iota"));
		EGGS.put("kappa", new ValuesItem((char)954, "kappa", 954, "kappa"));
		EGGS.put("lambda", new ValuesItem((char)923, "lambda", 923, "lambda"));
		EGGS.put("mu", new ValuesItem((char)956, "mu", 956, "mu"));
		EGGS.put("nu", new ValuesItem((char)925, "nu", 925, "nu"));
		EGGS.put("xi", new ValuesItem((char)958, "xi", 958, "xi"));
		EGGS.put("omicron", new ValuesItem((char)959, "omicron", 959, "omicron"));
		EGGS.put("pi", new ValuesItem((char)960, "pi", 960, "pi"));
		EGGS.put("rho", new ValuesItem((char)961, "rho", 961, "rho"));
		EGGS.put("sigmaf", new ValuesItem((char)962, "sigmaf", 962, "sigmaf"));
		EGGS.put("sigma", new ValuesItem((char)963, "sigma", 963, "sigma"));
		EGGS.put("tau", new ValuesItem((char)964, "tau", 964, "tau"));
		EGGS.put("upsilon", new ValuesItem((char)965, "upsilon", 965, "upsilon"));
		EGGS.put("phi", new ValuesItem((char)966, "phi", 966, "phi"));
		EGGS.put("chi", new ValuesItem((char)967, "chi", 967, "chi"));
		EGGS.put("psi", new ValuesItem((char)968, "psi", 968, "psi"));
		EGGS.put("omega", new ValuesItem((char)969, "omega", 969, "omega"));
		EGGS.put("thetasym", new ValuesItem((char)977, "thetasym", 977, "theta symbol"));
		EGGS.put("upsih", new ValuesItem((char)978, "upsih", 978, "upsilon symbol"));
		EGGS.put("piv", new ValuesItem((char)982, "piv", 982, "pi symbol"));
		EGGS.put("OElig", new ValuesItem((char)338, "OElig", 338, "capital ligature OE"));
		EGGS.put("oelig", new ValuesItem((char)339, "oelig", 339, "small ligature oe"));
		EGGS.put("Scaron", new ValuesItem((char)352, "Scaron", 352, "capital S with caron"));
		EGGS.put("scaron", new ValuesItem((char)353, "scaron", 353, "small S with caron"));
		EGGS.put("Yuml", new ValuesItem((char)376, "Yuml", 376, "capital Y with diaeres"));
		EGGS.put("fnof", new ValuesItem((char)402, "fnof", 402, "f with hook"));
		EGGS.put("circ", new ValuesItem((char)710, "circ", 710, "modifier letter circumflex accent"));
		EGGS.put("tilde", new ValuesItem((char)732, "tilde", 732, "small tilde"));
		EGGS.put("ensp", new ValuesItem((char)8194, "ensp", 8194, "en space"));
		EGGS.put("emsp", new ValuesItem((char)8195, "emsp", 8195, "em space"));
		EGGS.put("thinsp", new ValuesItem((char)8201, "thinsp", 8201, "thin space"));
		EGGS.put("zwnj", new ValuesItem((char)8204, "zwnj", 8204, "zero width non-joiner"));
		EGGS.put("zwj", new ValuesItem((char)8205, "zwj", 8205, "zero width joiner"));
		EGGS.put("lrm", new ValuesItem((char)8206, "lrm", 8206, "left-to-right mark"));
		EGGS.put("rlm", new ValuesItem((char)8207, "rlm", 8207, "right-to-left mark"));
		EGGS.put("ndash", new ValuesItem((char)8211, "ndash", 8211, "en dash"));
		EGGS.put("mdash", new ValuesItem((char)8212, "mdash", 8212, "em dash"));
		EGGS.put("lsquo", new ValuesItem((char)8216, "lsquo", 8216, "left single quotation mark"));
		EGGS.put("rsquo", new ValuesItem((char)8217, "rsquo", 8217, "right single quotation mark"));
		EGGS.put("sbquo", new ValuesItem((char)8218, "sbquo", 8218, "single low-9 quotation mark"));
		EGGS.put("ldquo", new ValuesItem((char)8220, "ldquo", 8220, "left double quotation mark"));
		EGGS.put("rdquo", new ValuesItem((char)8221, "rdquo", 8221, "right double quotation mark"));
		EGGS.put("bdquo", new ValuesItem((char)8222, "bdquo", 8222, "double low-9 quotation mark"));
		EGGS.put("dagger", new ValuesItem((char)8224, "dagger", 8224, "dagger"));
		EGGS.put("Dagger", new ValuesItem((char)8225, "Dagger", 8225, "double dagger"));
		EGGS.put("bull", new ValuesItem((char)8226, "bull", 8226, "bullet"));
		EGGS.put("hellip", new ValuesItem((char)8230, "hellip", 8230, "horizontal ellipsis"));
		EGGS.put("permil", new ValuesItem((char)8240, "permil", 8240, "per mille&nbsp;"));
		EGGS.put("prime", new ValuesItem((char)8242, "prime", 8242, "minutes"));
		EGGS.put("Prime", new ValuesItem((char)8243, "Prime", 8243, "seconds"));
		EGGS.put("lsaquo", new ValuesItem((char)8249, "lsaquo", 8249, "single left angle quotation"));
		EGGS.put("rsaquo", new ValuesItem((char)8250, "rsaquo", 8250, "single right angle quotation"));
		EGGS.put("oline", new ValuesItem((char)8254, "oline", 8254, "overline"));
		EGGS.put("euro", new ValuesItem((char)8364, "euro", 8364, "euro"));
		EGGS.put("trade", new ValuesItem((char)8482, "trade", 8482, "trademark"));
		EGGS.put("larr", new ValuesItem((char)8592, "larr", 8592, "left arrow"));
		EGGS.put("uarr", new ValuesItem((char)8593, "uarr", 8593, "up arrow"));
		EGGS.put("rarr", new ValuesItem((char)8594, "rarr", 8594, "right arrow"));
		EGGS.put("darr", new ValuesItem((char)8595, "darr", 8595, "down arrow"));
		EGGS.put("harr", new ValuesItem((char)8596, "harr", 8596, "left right arrow"));
		EGGS.put("crarr", new ValuesItem((char)8629, "crarr", 8629, "carriage return arrow"));
		EGGS.put("lceil", new ValuesItem((char)8968, "lceil", 8968, "left ceiling"));
		EGGS.put("rceil", new ValuesItem((char)8969, "rceil", 8969, "right ceiling"));
		EGGS.put("lfloor", new ValuesItem((char)8970, "lfloor", 8970, "left floor"));
		EGGS.put("rfloor", new ValuesItem((char)8971, "rfloor", 8971, "right floor"));
		EGGS.put("loz", new ValuesItem((char)9674, "loz", 9674, "lozenge"));
		EGGS.put("spades", new ValuesItem((char)9824, "spades", 9824, "spade"));
		EGGS.put("clubs", new ValuesItem((char)9827, "clubs", 9827, "club"));
		EGGS.put("hearts", new ValuesItem((char)9829, "hearts", 9829, "heart"));
		EGGS.put("diams", new ValuesItem((char)9830, "diams", 9830, "diamond"));
	}
	
	public static List<ValuesItem> eggs() {
		List<ValuesItem> list = Lists.newArrayList(EGGS.values());
		Collections.sort(list, new Comparator<ValuesItem>() {

			@Override
			public int compare(ValuesItem o1, ValuesItem o2) {
				return getCode(o1) - getCode(o2);
			}
			
			private int getCode(ValuesItem vi) {
				String va = vi.getByIndex(INDEX_CODE) + "";
				Integer code = MathUtil.toInteger(va, 0);
				return code;
			}
		});
		
		return list;
	}
	
	public static List<String> toScript() {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching().useList();
				String temp = "E:/Mas/exp/20180717_200140_KO.txt";

				return temp;
			}

			@Override
			protected void parse() {
				String regex = "(\\d+),";
				
				for(String line : sourceList) {
					String temp = line.trim();
//					Matcher ma = createMatcher(regex, temp);
					String number = StrUtil.findFirstMatchedItem(regex, temp);
					if(number == null) {
						continue;
					}
					int val = Integer.parseInt(number);
					String newline = temp.replace(number + ",", number + ", (char)" + val + ", ");
					C.pl(newline);
				}

				Matcher ma = createMatcher(regex);
				int count = 0;
				while(ma.find()) {
//					if(count++ > 10) break; 
					String id = ma.group(2);
					String code = ma.group(3);
					String desc = ma.group(1).trim();
					mexItems.add(StrUtil.occupy(SCRIPT, id, code, desc));
				}
			}
		};
		
		return neymar.process().getItems();	
	}
	
	public static List<String> toScript2() {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public List<String> getUrls() {
				showFetching();
				List<String> urls = Lists.newArrayList();
				urls.add("http://www.w3school.com.cn/tags/html_ref_entities.html");
				urls.add("http://www.w3school.com.cn/tags/html_ref_symbols.html");
				
				return urls;
			}

			@Override
			protected void parse() {
				String regex = "<td>([^<>]+)</td>\\s*<td>&amp;([a-z]{1,99});</td>\\s*<td>&amp;#(\\d{1,7});</td>";

				Matcher ma = createMatcher(regex);
				int count = 0;
				while(ma.find()) {
//					if(count++ > 10) break; 
					String id = ma.group(2);
					String code = ma.group(3);
					String desc = ma.group(1).trim();
					mexItems.add(StrUtil.occupy(SCRIPT, id, code, desc));
				}
			}
		};
		
		return neymar.process().getItems();	
	}
}
