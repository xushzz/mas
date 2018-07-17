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
	
	public static int INDEX_CODE = 1;
	public static final AlinkMap<String, ValuesItem> EGGS = Amaps.newLinkHashMap();
	public static String SCRIPT = "EGGS.put(\"{0}\", new ValuesItem(\"{0}\", {1}, \"{2}\"));";
	static {
		EGGS.put("quot", new ValuesItem("quot", 34, (char)34,  "quotation mark"));
		EGGS.put("apos", new ValuesItem("apos", 39, (char)39,  "apostrophe&nbsp;"));
		EGGS.put("amp", new ValuesItem("amp", 38, (char)38,  "ampersand"));
		EGGS.put("lt", new ValuesItem("lt", 60, (char)60,  "less-than"));
		EGGS.put("gt", new ValuesItem("gt", 62, (char)62,  "greater-than"));
		EGGS.put("nbsp", new ValuesItem("nbsp", 32, (char)32,  "non-breaking space")); ////fixed from 160 to 32
		EGGS.put("iexcl", new ValuesItem("iexcl", 161, (char)161,  "inverted exclamation mark"));
		EGGS.put("cent", new ValuesItem("cent", 162, (char)162,  "cent"));
		EGGS.put("pound", new ValuesItem("pound", 163, (char)163,  "pound"));
		EGGS.put("curren", new ValuesItem("curren", 164, (char)164,  "currency"));
		EGGS.put("yen", new ValuesItem("yen", 165, (char)165,  "yen"));
		EGGS.put("brvbar", new ValuesItem("brvbar", 166, (char)166,  "broken vertical bar"));
		EGGS.put("sect", new ValuesItem("sect", 167, (char)167,  "section"));
		EGGS.put("uml", new ValuesItem("uml", 168, (char)168,  "spacing diaeresis"));
		EGGS.put("copy", new ValuesItem("copy", 169, (char)169,  "copyright"));
		EGGS.put("ordf", new ValuesItem("ordf", 170, (char)170,  "feminine ordinal indicator"));
		EGGS.put("laquo", new ValuesItem("laquo", 171, (char)171,  "angle quotation mark (left)"));
		EGGS.put("not", new ValuesItem("not", 172, (char)172,  "negation"));
		EGGS.put("shy", new ValuesItem("shy", 173, (char)173,  "soft hyphen"));
		EGGS.put("reg", new ValuesItem("reg", 174, (char)174,  "registered trademark"));
		EGGS.put("macr", new ValuesItem("macr", 175, (char)175,  "spacing macron"));
		EGGS.put("deg", new ValuesItem("deg", 176, (char)176,  "degree"));
		EGGS.put("plusmn", new ValuesItem("plusmn", 177, (char)177,  "plus-or-minus&nbsp;"));
		EGGS.put("acute", new ValuesItem("acute", 180, (char)180,  "spacing acute"));
		EGGS.put("micro", new ValuesItem("micro", 181, (char)181,  "micro"));
		EGGS.put("para", new ValuesItem("para", 182, (char)182,  "paragraph"));
		EGGS.put("middot", new ValuesItem("middot", 183, (char)183,  "middle dot"));
		EGGS.put("cedil", new ValuesItem("cedil", 184, (char)184,  "spacing cedilla"));
		EGGS.put("ordm", new ValuesItem("ordm", 186, (char)186,  "masculine ordinal indicator"));
		EGGS.put("raquo", new ValuesItem("raquo", 187, (char)187,  "angle quotation mark (right)"));
		EGGS.put("iquest", new ValuesItem("iquest", 191, (char)191,  "inverted question mark"));
		EGGS.put("times", new ValuesItem("times", 215, (char)215,  "multiplication"));
		EGGS.put("divide", new ValuesItem("divide", 247, (char)247,  "division"));
		EGGS.put("Agrave", new ValuesItem("Agrave", 192, (char)192,  "capital a, grave accent"));
		EGGS.put("Aacute", new ValuesItem("Aacute", 193, (char)193,  "capital a, acute accent"));
		EGGS.put("Acirc", new ValuesItem("Acirc", 194, (char)194,  "capital a, circumflex accent"));
		EGGS.put("Atilde", new ValuesItem("Atilde", 195, (char)195,  "capital a, tilde"));
		EGGS.put("Auml", new ValuesItem("Auml", 196, (char)196,  "capital a, umlaut mark"));
		EGGS.put("Aring", new ValuesItem("Aring", 197, (char)197,  "capital a, ring"));
		EGGS.put("AElig", new ValuesItem("AElig", 198, (char)198,  "capital ae"));
		EGGS.put("Ccedil", new ValuesItem("Ccedil", 199, (char)199,  "capital c, cedilla"));
		EGGS.put("Egrave", new ValuesItem("Egrave", 200, (char)200,  "capital e, grave accent"));
		EGGS.put("Eacute", new ValuesItem("Eacute", 201, (char)201,  "capital e, acute accent"));
		EGGS.put("Ecirc", new ValuesItem("Ecirc", 202, (char)202,  "capital e, circumflex accent"));
		EGGS.put("Euml", new ValuesItem("Euml", 203, (char)203,  "capital e, umlaut mark"));
		EGGS.put("Igrave", new ValuesItem("Igrave", 204, (char)204,  "capital i, grave accent"));
		EGGS.put("Iacute", new ValuesItem("Iacute", 205, (char)205,  "capital i, acute accent"));
		EGGS.put("Icirc", new ValuesItem("Icirc", 206, (char)206,  "capital i, circumflex accent"));
		EGGS.put("Iuml", new ValuesItem("Iuml", 207, (char)207,  "capital i, umlaut mark"));
		EGGS.put("ETH", new ValuesItem("ETH", 208, (char)208,  "capital eth, Icelandic"));
		EGGS.put("Ntilde", new ValuesItem("Ntilde", 209, (char)209,  "capital n, tilde"));
		EGGS.put("Ograve", new ValuesItem("Ograve", 210, (char)210,  "capital o, grave accent"));
		EGGS.put("Oacute", new ValuesItem("Oacute", 211, (char)211,  "capital o, acute accent"));
		EGGS.put("Ocirc", new ValuesItem("Ocirc", 212, (char)212,  "capital o, circumflex accent"));
		EGGS.put("Otilde", new ValuesItem("Otilde", 213, (char)213,  "capital o, tilde"));
		EGGS.put("Ouml", new ValuesItem("Ouml", 214, (char)214,  "capital o, umlaut mark"));
		EGGS.put("Oslash", new ValuesItem("Oslash", 216, (char)216,  "capital o, slash"));
		EGGS.put("Ugrave", new ValuesItem("Ugrave", 217, (char)217,  "capital u, grave accent"));
		EGGS.put("Uacute", new ValuesItem("Uacute", 218, (char)218,  "capital u, acute accent"));
		EGGS.put("Ucirc", new ValuesItem("Ucirc", 219, (char)219,  "capital u, circumflex accent"));
		EGGS.put("Uuml", new ValuesItem("Uuml", 220, (char)220,  "capital u, umlaut mark"));
		EGGS.put("Yacute", new ValuesItem("Yacute", 221, (char)221,  "capital y, acute accent"));
		EGGS.put("THORN", new ValuesItem("THORN", 222, (char)222,  "capital THORN, Icelandic"));
		EGGS.put("szlig", new ValuesItem("szlig", 223, (char)223,  "small sharp s, German"));
		EGGS.put("agrave", new ValuesItem("agrave", 224, (char)224,  "small a, grave accent"));
		EGGS.put("aacute", new ValuesItem("aacute", 225, (char)225,  "small a, acute accent"));
		EGGS.put("acirc", new ValuesItem("acirc", 226, (char)226,  "small a, circumflex accent"));
		EGGS.put("atilde", new ValuesItem("atilde", 227, (char)227,  "small a, tilde"));
		EGGS.put("auml", new ValuesItem("auml", 228, (char)228,  "small a, umlaut mark"));
		EGGS.put("aring", new ValuesItem("aring", 229, (char)229,  "small a, ring"));
		EGGS.put("aelig", new ValuesItem("aelig", 230, (char)230,  "small ae"));
		EGGS.put("ccedil", new ValuesItem("ccedil", 231, (char)231,  "small c, cedilla"));
		EGGS.put("egrave", new ValuesItem("egrave", 232, (char)232,  "small e, grave accent"));
		EGGS.put("eacute", new ValuesItem("eacute", 233, (char)233,  "small e, acute accent"));
		EGGS.put("ecirc", new ValuesItem("ecirc", 234, (char)234,  "small e, circumflex accent"));
		EGGS.put("euml", new ValuesItem("euml", 235, (char)235,  "small e, umlaut mark"));
		EGGS.put("igrave", new ValuesItem("igrave", 236, (char)236,  "small i, grave accent"));
		EGGS.put("iacute", new ValuesItem("iacute", 237, (char)237,  "small i, acute accent"));
		EGGS.put("icirc", new ValuesItem("icirc", 238, (char)238,  "small i, circumflex accent"));
		EGGS.put("iuml", new ValuesItem("iuml", 239, (char)239,  "small i, umlaut mark"));
		EGGS.put("eth", new ValuesItem("eth", 240, (char)240,  "small eth, Icelandic"));
		EGGS.put("ntilde", new ValuesItem("ntilde", 241, (char)241,  "small n, tilde"));
		EGGS.put("ograve", new ValuesItem("ograve", 242, (char)242,  "small o, grave accent"));
		EGGS.put("oacute", new ValuesItem("oacute", 243, (char)243,  "small o, acute accent"));
		EGGS.put("ocirc", new ValuesItem("ocirc", 244, (char)244,  "small o, circumflex accent"));
		EGGS.put("otilde", new ValuesItem("otilde", 245, (char)245,  "small o, tilde"));
		EGGS.put("ouml", new ValuesItem("ouml", 246, (char)246,  "small o, umlaut mark"));
		EGGS.put("oslash", new ValuesItem("oslash", 248, (char)248,  "small o, slash"));
		EGGS.put("ugrave", new ValuesItem("ugrave", 249, (char)249,  "small u, grave accent"));
		EGGS.put("uacute", new ValuesItem("uacute", 250, (char)250,  "small u, acute accent"));
		EGGS.put("ucirc", new ValuesItem("ucirc", 251, (char)251,  "small u, circumflex accent"));
		EGGS.put("uuml", new ValuesItem("uuml", 252, (char)252,  "small u, umlaut mark"));
		EGGS.put("yacute", new ValuesItem("yacute", 253, (char)253,  "small y, acute accent"));
		EGGS.put("thorn", new ValuesItem("thorn", 254, (char)254,  "small thorn, Icelandic"));
		EGGS.put("yuml", new ValuesItem("yuml", 255, (char)255,  "small y, umlaut mark"));
		EGGS.put("forall", new ValuesItem("forall", 8704, (char)8704,  "for all"));
		EGGS.put("part", new ValuesItem("part", 8706, (char)8706,  "part"));
		EGGS.put("exists", new ValuesItem("exists", 8707, (char)8707,  "exists"));
		EGGS.put("empty", new ValuesItem("empty", 8709, (char)8709,  "empty"));
		EGGS.put("nabla", new ValuesItem("nabla", 8711, (char)8711,  "nabla"));
		EGGS.put("isin", new ValuesItem("isin", 8712, (char)8712,  "isin"));
		EGGS.put("notin", new ValuesItem("notin", 8713, (char)8713,  "notin"));
		EGGS.put("ni", new ValuesItem("ni", 8715, (char)8715,  "ni"));
		EGGS.put("prod", new ValuesItem("prod", 8719, (char)8719,  "prod"));
		EGGS.put("sum", new ValuesItem("sum", 8721, (char)8721,  "sum"));
		EGGS.put("minus", new ValuesItem("minus", 8722, (char)8722,  "minus"));
		EGGS.put("lowast", new ValuesItem("lowast", 8727, (char)8727,  "lowast"));
		EGGS.put("radic", new ValuesItem("radic", 8730, (char)8730,  "square root"));
		EGGS.put("prop", new ValuesItem("prop", 8733, (char)8733,  "proportional to"));
		EGGS.put("infin", new ValuesItem("infin", 8734, (char)8734,  "infinity"));
		EGGS.put("ang", new ValuesItem("ang", 8736, (char)8736,  "angle"));
		EGGS.put("and", new ValuesItem("and", 8743, (char)8743,  "and"));
		EGGS.put("or", new ValuesItem("or", 8744, (char)8744,  "or"));
		EGGS.put("cap", new ValuesItem("cap", 8745, (char)8745,  "cap"));
		EGGS.put("cup", new ValuesItem("cup", 8746, (char)8746,  "cup"));
		EGGS.put("int", new ValuesItem("int", 8747, (char)8747,  "integral"));
		EGGS.put("sim", new ValuesItem("sim", 8764, (char)8764,  "simular to"));
		EGGS.put("cong", new ValuesItem("cong", 8773, (char)8773,  "approximately equal"));
		EGGS.put("asymp", new ValuesItem("asymp", 8776, (char)8776,  "almost equal"));
		EGGS.put("ne", new ValuesItem("ne", 8800, (char)8800,  "not equal"));
		EGGS.put("equiv", new ValuesItem("equiv", 8801, (char)8801,  "equivalent"));
		EGGS.put("le", new ValuesItem("le", 8804, (char)8804,  "less or equal"));
		EGGS.put("ge", new ValuesItem("ge", 8805, (char)8805,  "greater or equal"));
		EGGS.put("sub", new ValuesItem("sub", 8834, (char)8834,  "subset of"));
		EGGS.put("sup", new ValuesItem("sup", 8835, (char)8835,  "superset of"));
		EGGS.put("nsub", new ValuesItem("nsub", 8836, (char)8836,  "not subset of"));
		EGGS.put("sube", new ValuesItem("sube", 8838, (char)8838,  "subset or equal"));
		EGGS.put("supe", new ValuesItem("supe", 8839, (char)8839,  "superset or equal"));
		EGGS.put("oplus", new ValuesItem("oplus", 8853, (char)8853,  "circled plus"));
		EGGS.put("otimes", new ValuesItem("otimes", 8855, (char)8855,  "cirled times"));
		EGGS.put("perp", new ValuesItem("perp", 8869, (char)8869,  "perpendicular"));
		EGGS.put("sdot", new ValuesItem("sdot", 8901, (char)8901,  "dot operator"));
		EGGS.put("Alpha", new ValuesItem("Alpha", 913, (char)913,  "Alpha"));
		EGGS.put("Beta", new ValuesItem("Beta", 914, (char)914,  "Beta"));
		EGGS.put("Gamma", new ValuesItem("Gamma", 915, (char)915,  "Gamma"));
		EGGS.put("Delta", new ValuesItem("Delta", 916, (char)916,  "Delta"));
		EGGS.put("Epsilon", new ValuesItem("Epsilon", 917, (char)917,  "Epsilon"));
		EGGS.put("Zeta", new ValuesItem("Zeta", 918, (char)918,  "Zeta"));
		EGGS.put("Eta", new ValuesItem("Eta", 919, (char)919,  "Eta"));
		EGGS.put("Theta", new ValuesItem("Theta", 920, (char)920,  "Theta"));
		EGGS.put("Iota", new ValuesItem("Iota", 921, (char)921,  "Iota"));
		EGGS.put("Kappa", new ValuesItem("Kappa", 922, (char)922,  "Kappa"));
		EGGS.put("Lambda", new ValuesItem("Lambda", 923, (char)923,  "Lambda"));
		EGGS.put("Mu", new ValuesItem("Mu", 924, (char)924,  "Mu"));
		EGGS.put("Nu", new ValuesItem("Nu", 925, (char)925,  "Nu"));
		EGGS.put("Xi", new ValuesItem("Xi", 926, (char)926,  "Xi"));
		EGGS.put("Omicron", new ValuesItem("Omicron", 927, (char)927,  "Omicron"));
		EGGS.put("Pi", new ValuesItem("Pi", 928, (char)928,  "Pi"));
		EGGS.put("Rho", new ValuesItem("Rho", 929, (char)929,  "Rho"));
		EGGS.put("Sigma", new ValuesItem("Sigma", 931, (char)931,  "Sigma"));
		EGGS.put("Tau", new ValuesItem("Tau", 932, (char)932,  "Tau"));
		EGGS.put("Upsilon", new ValuesItem("Upsilon", 933, (char)933,  "Upsilon"));
		EGGS.put("Phi", new ValuesItem("Phi", 934, (char)934,  "Phi"));
		EGGS.put("Chi", new ValuesItem("Chi", 935, (char)935,  "Chi"));
		EGGS.put("Psi", new ValuesItem("Psi", 936, (char)936,  "Psi"));
		EGGS.put("Omega", new ValuesItem("Omega", 937, (char)937,  "Omega"));
		EGGS.put("alpha", new ValuesItem("alpha", 945, (char)945,  "alpha"));
		EGGS.put("beta", new ValuesItem("beta", 946, (char)946,  "beta"));
		EGGS.put("gamma", new ValuesItem("gamma", 947, (char)947,  "gamma"));
		EGGS.put("delta", new ValuesItem("delta", 948, (char)948,  "delta"));
		EGGS.put("epsilon", new ValuesItem("epsilon", 949, (char)949,  "epsilon"));
		EGGS.put("zeta", new ValuesItem("zeta", 950, (char)950,  "zeta"));
		EGGS.put("eta", new ValuesItem("eta", 951, (char)951,  "eta"));
		EGGS.put("theta", new ValuesItem("theta", 952, (char)952,  "theta"));
		EGGS.put("iota", new ValuesItem("iota", 953, (char)953,  "iota"));
		EGGS.put("kappa", new ValuesItem("kappa", 954, (char)954,  "kappa"));
		EGGS.put("lambda", new ValuesItem("lambda", 923, (char)923,  "lambda"));
		EGGS.put("mu", new ValuesItem("mu", 956, (char)956,  "mu"));
		EGGS.put("nu", new ValuesItem("nu", 925, (char)925,  "nu"));
		EGGS.put("xi", new ValuesItem("xi", 958, (char)958,  "xi"));
		EGGS.put("omicron", new ValuesItem("omicron", 959, (char)959,  "omicron"));
		EGGS.put("pi", new ValuesItem("pi", 960, (char)960,  "pi"));
		EGGS.put("rho", new ValuesItem("rho", 961, (char)961,  "rho"));
		EGGS.put("sigmaf", new ValuesItem("sigmaf", 962, (char)962,  "sigmaf"));
		EGGS.put("sigma", new ValuesItem("sigma", 963, (char)963,  "sigma"));
		EGGS.put("tau", new ValuesItem("tau", 964, (char)964,  "tau"));
		EGGS.put("upsilon", new ValuesItem("upsilon", 965, (char)965,  "upsilon"));
		EGGS.put("phi", new ValuesItem("phi", 966, (char)966,  "phi"));
		EGGS.put("chi", new ValuesItem("chi", 967, (char)967,  "chi"));
		EGGS.put("psi", new ValuesItem("psi", 968, (char)968,  "psi"));
		EGGS.put("omega", new ValuesItem("omega", 969, (char)969,  "omega"));
		EGGS.put("thetasym", new ValuesItem("thetasym", 977, (char)977,  "theta symbol"));
		EGGS.put("upsih", new ValuesItem("upsih", 978, (char)978,  "upsilon symbol"));
		EGGS.put("piv", new ValuesItem("piv", 982, (char)982,  "pi symbol"));
		EGGS.put("OElig", new ValuesItem("OElig", 338, (char)338,  "capital ligature OE"));
		EGGS.put("oelig", new ValuesItem("oelig", 339, (char)339,  "small ligature oe"));
		EGGS.put("Scaron", new ValuesItem("Scaron", 352, (char)352,  "capital S with caron"));
		EGGS.put("scaron", new ValuesItem("scaron", 353, (char)353,  "small S with caron"));
		EGGS.put("Yuml", new ValuesItem("Yuml", 376, (char)376,  "capital Y with diaeres"));
		EGGS.put("fnof", new ValuesItem("fnof", 402, (char)402,  "f with hook"));
		EGGS.put("circ", new ValuesItem("circ", 710, (char)710,  "modifier letter circumflex accent"));
		EGGS.put("tilde", new ValuesItem("tilde", 732, (char)732,  "small tilde"));
		EGGS.put("ensp", new ValuesItem("ensp", 8194, (char)8194,  "en space"));
		EGGS.put("emsp", new ValuesItem("emsp", 8195, (char)8195,  "em space"));
		EGGS.put("thinsp", new ValuesItem("thinsp", 8201, (char)8201,  "thin space"));
		EGGS.put("zwnj", new ValuesItem("zwnj", 8204, (char)8204,  "zero width non-joiner"));
		EGGS.put("zwj", new ValuesItem("zwj", 8205, (char)8205,  "zero width joiner"));
		EGGS.put("lrm", new ValuesItem("lrm", 8206, (char)8206,  "left-to-right mark"));
		EGGS.put("rlm", new ValuesItem("rlm", 8207, (char)8207,  "right-to-left mark"));
		EGGS.put("ndash", new ValuesItem("ndash", 8211, (char)8211,  "en dash"));
		EGGS.put("mdash", new ValuesItem("mdash", 8212, (char)8212,  "em dash"));
		EGGS.put("lsquo", new ValuesItem("lsquo", 8216, (char)8216,  "left single quotation mark"));
		EGGS.put("rsquo", new ValuesItem("rsquo", 8217, (char)8217,  "right single quotation mark"));
		EGGS.put("sbquo", new ValuesItem("sbquo", 8218, (char)8218,  "single low-9 quotation mark"));
		EGGS.put("ldquo", new ValuesItem("ldquo", 8220, (char)8220,  "left double quotation mark"));
		EGGS.put("rdquo", new ValuesItem("rdquo", 8221, (char)8221,  "right double quotation mark"));
		EGGS.put("bdquo", new ValuesItem("bdquo", 8222, (char)8222,  "double low-9 quotation mark"));
		EGGS.put("dagger", new ValuesItem("dagger", 8224, (char)8224,  "dagger"));
		EGGS.put("Dagger", new ValuesItem("Dagger", 8225, (char)8225,  "double dagger"));
		EGGS.put("bull", new ValuesItem("bull", 8226, (char)8226,  "bullet"));
		EGGS.put("hellip", new ValuesItem("hellip", 8230, (char)8230,  "horizontal ellipsis"));
		EGGS.put("permil", new ValuesItem("permil", 8240, (char)8240,  "per mille&nbsp;"));
		EGGS.put("prime", new ValuesItem("prime", 8242, (char)8242,  "minutes"));
		EGGS.put("Prime", new ValuesItem("Prime", 8243, (char)8243,  "seconds"));
		EGGS.put("lsaquo", new ValuesItem("lsaquo", 8249, (char)8249,  "single left angle quotation"));
		EGGS.put("rsaquo", new ValuesItem("rsaquo", 8250, (char)8250,  "single right angle quotation"));
		EGGS.put("oline", new ValuesItem("oline", 8254, (char)8254,  "overline"));
		EGGS.put("euro", new ValuesItem("euro", 8364, (char)8364,  "euro"));
		EGGS.put("trade", new ValuesItem("trade", 8482, (char)8482,  "trademark"));
		EGGS.put("larr", new ValuesItem("larr", 8592, (char)8592,  "left arrow"));
		EGGS.put("uarr", new ValuesItem("uarr", 8593, (char)8593,  "up arrow"));
		EGGS.put("rarr", new ValuesItem("rarr", 8594, (char)8594,  "right arrow"));
		EGGS.put("darr", new ValuesItem("darr", 8595, (char)8595,  "down arrow"));
		EGGS.put("harr", new ValuesItem("harr", 8596, (char)8596,  "left right arrow"));
		EGGS.put("crarr", new ValuesItem("crarr", 8629, (char)8629,  "carriage return arrow"));
		EGGS.put("lceil", new ValuesItem("lceil", 8968, (char)8968,  "left ceiling"));
		EGGS.put("rceil", new ValuesItem("rceil", 8969, (char)8969,  "right ceiling"));
		EGGS.put("lfloor", new ValuesItem("lfloor", 8970, (char)8970,  "left floor"));
		EGGS.put("rfloor", new ValuesItem("rfloor", 8971, (char)8971,  "right floor"));
		EGGS.put("loz", new ValuesItem("loz", 9674, (char)9674,  "lozenge"));
		EGGS.put("spades", new ValuesItem("spades", 9824, (char)9824,  "spade"));
		EGGS.put("clubs", new ValuesItem("clubs", 9827, (char)9827,  "club"));
		EGGS.put("hearts", new ValuesItem("hearts", 9829, (char)9829,  "heart"));
		EGGS.put("diams", new ValuesItem("diams", 9830, (char)9830,  "diamond"));
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
