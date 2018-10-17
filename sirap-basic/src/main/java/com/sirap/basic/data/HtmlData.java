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
	public static String SCRIPT = "EGGS.put(\"{0}\", ValuesItem.of((char){1}, \"{0}\", {1}, \"{2}\"));";
	static {
		EGGS.put("quot", ValuesItem.of((char)34, "quot", 34, "quotation mark"));
		EGGS.put("apos", ValuesItem.of((char)39, "apos", 39, "apostrophe"));
		EGGS.put("amp", ValuesItem.of((char)38, "amp", 38, "ampersand"));
		EGGS.put("lt", ValuesItem.of((char)60, "lt", 60, "less-than"));
		EGGS.put("gt", ValuesItem.of((char)62, "gt", 62, "greater-than"));
		EGGS.put("nbsp", ValuesItem.of((char)160, "nbsp", 160, "non-breaking space"));
		EGGS.put("iexcl", ValuesItem.of((char)161, "iexcl", 161, "inverted exclamation mark"));
		EGGS.put("cent", ValuesItem.of((char)162, "cent", 162, "cent"));
		EGGS.put("pound", ValuesItem.of((char)163, "pound", 163, "pound"));
		EGGS.put("curren", ValuesItem.of((char)164, "curren", 164, "currency"));
		EGGS.put("yen", ValuesItem.of((char)165, "yen", 165, "yen"));
		EGGS.put("brvbar", ValuesItem.of((char)166, "brvbar", 166, "broken vertical bar"));
		EGGS.put("sect", ValuesItem.of((char)167, "sect", 167, "section"));
		EGGS.put("uml", ValuesItem.of((char)168, "uml", 168, "spacing diaeresis"));
		EGGS.put("copy", ValuesItem.of((char)169, "copy", 169, "copyright"));
		EGGS.put("ordf", ValuesItem.of((char)170, "ordf", 170, "feminine ordinal indicator"));
		EGGS.put("laquo", ValuesItem.of((char)171, "laquo", 171, "angle quotation mark (left)"));
		EGGS.put("not", ValuesItem.of((char)172, "not", 172, "negation"));
		EGGS.put("shy", ValuesItem.of((char)173, "shy", 173, "soft hyphen"));
		EGGS.put("reg", ValuesItem.of((char)174, "reg", 174, "registered trademark"));
		EGGS.put("macr", ValuesItem.of((char)175, "macr", 175, "spacing macron"));
		EGGS.put("deg", ValuesItem.of((char)176, "deg", 176, "degree"));
		EGGS.put("plusmn", ValuesItem.of((char)177, "plusmn", 177, "plus-or-minus&nbsp;"));
		EGGS.put("acute", ValuesItem.of((char)180, "acute", 180, "spacing acute"));
		EGGS.put("micro", ValuesItem.of((char)181, "micro", 181, "micro"));
		EGGS.put("para", ValuesItem.of((char)182, "para", 182, "paragraph"));
		EGGS.put("middot", ValuesItem.of((char)183, "middot", 183, "middle dot"));
		EGGS.put("cedil", ValuesItem.of((char)184, "cedil", 184, "spacing cedilla"));
		EGGS.put("ordm", ValuesItem.of((char)186, "ordm", 186, "masculine ordinal indicator"));
		EGGS.put("raquo", ValuesItem.of((char)187, "raquo", 187, "angle quotation mark (right)"));
		EGGS.put("iquest", ValuesItem.of((char)191, "iquest", 191, "inverted question mark"));
		EGGS.put("times", ValuesItem.of((char)215, "times", 215, "multiplication"));
		EGGS.put("divide", ValuesItem.of((char)247, "divide", 247, "division"));
		EGGS.put("Agrave", ValuesItem.of((char)192, "Agrave", 192, "capital a, grave accent"));
		EGGS.put("Aacute", ValuesItem.of((char)193, "Aacute", 193, "capital a, acute accent"));
		EGGS.put("Acirc", ValuesItem.of((char)194, "Acirc", 194, "capital a, circumflex accent"));
		EGGS.put("Atilde", ValuesItem.of((char)195, "Atilde", 195, "capital a, tilde"));
		EGGS.put("Auml", ValuesItem.of((char)196, "Auml", 196, "capital a, umlaut mark"));
		EGGS.put("Aring", ValuesItem.of((char)197, "Aring", 197, "capital a, ring"));
		EGGS.put("AElig", ValuesItem.of((char)198, "AElig", 198, "capital ae"));
		EGGS.put("Ccedil", ValuesItem.of((char)199, "Ccedil", 199, "capital c, cedilla"));
		EGGS.put("Egrave", ValuesItem.of((char)200, "Egrave", 200, "capital e, grave accent"));
		EGGS.put("Eacute", ValuesItem.of((char)201, "Eacute", 201, "capital e, acute accent"));
		EGGS.put("Ecirc", ValuesItem.of((char)202, "Ecirc", 202, "capital e, circumflex accent"));
		EGGS.put("Euml", ValuesItem.of((char)203, "Euml", 203, "capital e, umlaut mark"));
		EGGS.put("Igrave", ValuesItem.of((char)204, "Igrave", 204, "capital i, grave accent"));
		EGGS.put("Iacute", ValuesItem.of((char)205, "Iacute", 205, "capital i, acute accent"));
		EGGS.put("Icirc", ValuesItem.of((char)206, "Icirc", 206, "capital i, circumflex accent"));
		EGGS.put("Iuml", ValuesItem.of((char)207, "Iuml", 207, "capital i, umlaut mark"));
		EGGS.put("ETH", ValuesItem.of((char)208, "ETH", 208, "capital eth, Icelandic"));
		EGGS.put("Ntilde", ValuesItem.of((char)209, "Ntilde", 209, "capital n, tilde"));
		EGGS.put("Ograve", ValuesItem.of((char)210, "Ograve", 210, "capital o, grave accent"));
		EGGS.put("Oacute", ValuesItem.of((char)211, "Oacute", 211, "capital o, acute accent"));
		EGGS.put("Ocirc", ValuesItem.of((char)212, "Ocirc", 212, "capital o, circumflex accent"));
		EGGS.put("Otilde", ValuesItem.of((char)213, "Otilde", 213, "capital o, tilde"));
		EGGS.put("Ouml", ValuesItem.of((char)214, "Ouml", 214, "capital o, umlaut mark"));
		EGGS.put("Oslash", ValuesItem.of((char)216, "Oslash", 216, "capital o, slash"));
		EGGS.put("Ugrave", ValuesItem.of((char)217, "Ugrave", 217, "capital u, grave accent"));
		EGGS.put("Uacute", ValuesItem.of((char)218, "Uacute", 218, "capital u, acute accent"));
		EGGS.put("Ucirc", ValuesItem.of((char)219, "Ucirc", 219, "capital u, circumflex accent"));
		EGGS.put("Uuml", ValuesItem.of((char)220, "Uuml", 220, "capital u, umlaut mark"));
		EGGS.put("Yacute", ValuesItem.of((char)221, "Yacute", 221, "capital y, acute accent"));
		EGGS.put("THORN", ValuesItem.of((char)222, "THORN", 222, "capital THORN, Icelandic"));
		EGGS.put("szlig", ValuesItem.of((char)223, "szlig", 223, "small sharp s, German"));
		EGGS.put("agrave", ValuesItem.of((char)224, "agrave", 224, "small a, grave accent"));
		EGGS.put("aacute", ValuesItem.of((char)225, "aacute", 225, "small a, acute accent"));
		EGGS.put("acirc", ValuesItem.of((char)226, "acirc", 226, "small a, circumflex accent"));
		EGGS.put("atilde", ValuesItem.of((char)227, "atilde", 227, "small a, tilde"));
		EGGS.put("auml", ValuesItem.of((char)228, "auml", 228, "small a, umlaut mark"));
		EGGS.put("aring", ValuesItem.of((char)229, "aring", 229, "small a, ring"));
		EGGS.put("aelig", ValuesItem.of((char)230, "aelig", 230, "small ae"));
		EGGS.put("ccedil", ValuesItem.of((char)231, "ccedil", 231, "small c, cedilla"));
		EGGS.put("egrave", ValuesItem.of((char)232, "egrave", 232, "small e, grave accent"));
		EGGS.put("eacute", ValuesItem.of((char)233, "eacute", 233, "small e, acute accent"));
		EGGS.put("ecirc", ValuesItem.of((char)234, "ecirc", 234, "small e, circumflex accent"));
		EGGS.put("euml", ValuesItem.of((char)235, "euml", 235, "small e, umlaut mark"));
		EGGS.put("igrave", ValuesItem.of((char)236, "igrave", 236, "small i, grave accent"));
		EGGS.put("iacute", ValuesItem.of((char)237, "iacute", 237, "small i, acute accent"));
		EGGS.put("icirc", ValuesItem.of((char)238, "icirc", 238, "small i, circumflex accent"));
		EGGS.put("iuml", ValuesItem.of((char)239, "iuml", 239, "small i, umlaut mark"));
		EGGS.put("eth", ValuesItem.of((char)240, "eth", 240, "small eth, Icelandic"));
		EGGS.put("ntilde", ValuesItem.of((char)241, "ntilde", 241, "small n, tilde"));
		EGGS.put("ograve", ValuesItem.of((char)242, "ograve", 242, "small o, grave accent"));
		EGGS.put("oacute", ValuesItem.of((char)243, "oacute", 243, "small o, acute accent"));
		EGGS.put("ocirc", ValuesItem.of((char)244, "ocirc", 244, "small o, circumflex accent"));
		EGGS.put("otilde", ValuesItem.of((char)245, "otilde", 245, "small o, tilde"));
		EGGS.put("ouml", ValuesItem.of((char)246, "ouml", 246, "small o, umlaut mark"));
		EGGS.put("oslash", ValuesItem.of((char)248, "oslash", 248, "small o, slash"));
		EGGS.put("ugrave", ValuesItem.of((char)249, "ugrave", 249, "small u, grave accent"));
		EGGS.put("uacute", ValuesItem.of((char)250, "uacute", 250, "small u, acute accent"));
		EGGS.put("ucirc", ValuesItem.of((char)251, "ucirc", 251, "small u, circumflex accent"));
		EGGS.put("uuml", ValuesItem.of((char)252, "uuml", 252, "small u, umlaut mark"));
		EGGS.put("yacute", ValuesItem.of((char)253, "yacute", 253, "small y, acute accent"));
		EGGS.put("thorn", ValuesItem.of((char)254, "thorn", 254, "small thorn, Icelandic"));
		EGGS.put("yuml", ValuesItem.of((char)255, "yuml", 255, "small y, umlaut mark"));
		EGGS.put("forall", ValuesItem.of((char)8704, "forall", 8704, "for all"));
		EGGS.put("part", ValuesItem.of((char)8706, "part", 8706, "part"));
		EGGS.put("exists", ValuesItem.of((char)8707, "exists", 8707, "exists"));
		EGGS.put("empty", ValuesItem.of((char)8709, "empty", 8709, "empty"));
		EGGS.put("nabla", ValuesItem.of((char)8711, "nabla", 8711, "nabla"));
		EGGS.put("isin", ValuesItem.of((char)8712, "isin", 8712, "isin"));
		EGGS.put("notin", ValuesItem.of((char)8713, "notin", 8713, "notin"));
		EGGS.put("ni", ValuesItem.of((char)8715, "ni", 8715, "ni"));
		EGGS.put("prod", ValuesItem.of((char)8719, "prod", 8719, "prod"));
		EGGS.put("sum", ValuesItem.of((char)8721, "sum", 8721, "sum"));
		EGGS.put("minus", ValuesItem.of((char)8722, "minus", 8722, "minus"));
		EGGS.put("lowast", ValuesItem.of((char)8727, "lowast", 8727, "lowast"));
		EGGS.put("radic", ValuesItem.of((char)8730, "radic", 8730, "square root"));
		EGGS.put("prop", ValuesItem.of((char)8733, "prop", 8733, "proportional to"));
		EGGS.put("infin", ValuesItem.of((char)8734, "infin", 8734, "infinity"));
		EGGS.put("ang", ValuesItem.of((char)8736, "ang", 8736, "angle"));
		EGGS.put("and", ValuesItem.of((char)8743, "and", 8743, "and"));
		EGGS.put("or", ValuesItem.of((char)8744, "or", 8744, "or"));
		EGGS.put("cap", ValuesItem.of((char)8745, "cap", 8745, "cap"));
		EGGS.put("cup", ValuesItem.of((char)8746, "cup", 8746, "cup"));
		EGGS.put("int", ValuesItem.of((char)8747, "int", 8747, "integral"));
		EGGS.put("sim", ValuesItem.of((char)8764, "sim", 8764, "simular to"));
		EGGS.put("cong", ValuesItem.of((char)8773, "cong", 8773, "approximately equal"));
		EGGS.put("asymp", ValuesItem.of((char)8776, "asymp", 8776, "almost equal"));
		EGGS.put("ne", ValuesItem.of((char)8800, "ne", 8800, "not equal"));
		EGGS.put("equiv", ValuesItem.of((char)8801, "equiv", 8801, "equivalent"));
		EGGS.put("le", ValuesItem.of((char)8804, "le", 8804, "less or equal"));
		EGGS.put("ge", ValuesItem.of((char)8805, "ge", 8805, "greater or equal"));
		EGGS.put("sub", ValuesItem.of((char)8834, "sub", 8834, "subset of"));
		EGGS.put("sup", ValuesItem.of((char)8835, "sup", 8835, "superset of"));
		EGGS.put("nsub", ValuesItem.of((char)8836, "nsub", 8836, "not subset of"));
		EGGS.put("sube", ValuesItem.of((char)8838, "sube", 8838, "subset or equal"));
		EGGS.put("supe", ValuesItem.of((char)8839, "supe", 8839, "superset or equal"));
		EGGS.put("oplus", ValuesItem.of((char)8853, "oplus", 8853, "circled plus"));
		EGGS.put("otimes", ValuesItem.of((char)8855, "otimes", 8855, "cirled times"));
		EGGS.put("perp", ValuesItem.of((char)8869, "perp", 8869, "perpendicular"));
		EGGS.put("sdot", ValuesItem.of((char)8901, "sdot", 8901, "dot operator"));
		EGGS.put("Alpha", ValuesItem.of((char)913, "Alpha", 913, "Alpha"));
		EGGS.put("Beta", ValuesItem.of((char)914, "Beta", 914, "Beta"));
		EGGS.put("Gamma", ValuesItem.of((char)915, "Gamma", 915, "Gamma"));
		EGGS.put("Delta", ValuesItem.of((char)916, "Delta", 916, "Delta"));
		EGGS.put("Epsilon", ValuesItem.of((char)917, "Epsilon", 917, "Epsilon"));
		EGGS.put("Zeta", ValuesItem.of((char)918, "Zeta", 918, "Zeta"));
		EGGS.put("Eta", ValuesItem.of((char)919, "Eta", 919, "Eta"));
		EGGS.put("Theta", ValuesItem.of((char)920, "Theta", 920, "Theta"));
		EGGS.put("Iota", ValuesItem.of((char)921, "Iota", 921, "Iota"));
		EGGS.put("Kappa", ValuesItem.of((char)922, "Kappa", 922, "Kappa"));
		EGGS.put("Lambda", ValuesItem.of((char)923, "Lambda", 923, "Lambda"));
		EGGS.put("Mu", ValuesItem.of((char)924, "Mu", 924, "Mu"));
		EGGS.put("Nu", ValuesItem.of((char)925, "Nu", 925, "Nu"));
		EGGS.put("Xi", ValuesItem.of((char)926, "Xi", 926, "Xi"));
		EGGS.put("Omicron", ValuesItem.of((char)927, "Omicron", 927, "Omicron"));
		EGGS.put("Pi", ValuesItem.of((char)928, "Pi", 928, "Pi"));
		EGGS.put("Rho", ValuesItem.of((char)929, "Rho", 929, "Rho"));
		EGGS.put("Sigma", ValuesItem.of((char)931, "Sigma", 931, "Sigma"));
		EGGS.put("Tau", ValuesItem.of((char)932, "Tau", 932, "Tau"));
		EGGS.put("Upsilon", ValuesItem.of((char)933, "Upsilon", 933, "Upsilon"));
		EGGS.put("Phi", ValuesItem.of((char)934, "Phi", 934, "Phi"));
		EGGS.put("Chi", ValuesItem.of((char)935, "Chi", 935, "Chi"));
		EGGS.put("Psi", ValuesItem.of((char)936, "Psi", 936, "Psi"));
		EGGS.put("Omega", ValuesItem.of((char)937, "Omega", 937, "Omega"));
		EGGS.put("alpha", ValuesItem.of((char)945, "alpha", 945, "alpha"));
		EGGS.put("beta", ValuesItem.of((char)946, "beta", 946, "beta"));
		EGGS.put("gamma", ValuesItem.of((char)947, "gamma", 947, "gamma"));
		EGGS.put("delta", ValuesItem.of((char)948, "delta", 948, "delta"));
		EGGS.put("epsilon", ValuesItem.of((char)949, "epsilon", 949, "epsilon"));
		EGGS.put("zeta", ValuesItem.of((char)950, "zeta", 950, "zeta"));
		EGGS.put("eta", ValuesItem.of((char)951, "eta", 951, "eta"));
		EGGS.put("theta", ValuesItem.of((char)952, "theta", 952, "theta"));
		EGGS.put("iota", ValuesItem.of((char)953, "iota", 953, "iota"));
		EGGS.put("kappa", ValuesItem.of((char)954, "kappa", 954, "kappa"));
		EGGS.put("lambda", ValuesItem.of((char)923, "lambda", 923, "lambda"));
		EGGS.put("mu", ValuesItem.of((char)956, "mu", 956, "mu"));
		EGGS.put("nu", ValuesItem.of((char)925, "nu", 925, "nu"));
		EGGS.put("xi", ValuesItem.of((char)958, "xi", 958, "xi"));
		EGGS.put("omicron", ValuesItem.of((char)959, "omicron", 959, "omicron"));
		EGGS.put("pi", ValuesItem.of((char)960, "pi", 960, "pi"));
		EGGS.put("rho", ValuesItem.of((char)961, "rho", 961, "rho"));
		EGGS.put("sigmaf", ValuesItem.of((char)962, "sigmaf", 962, "sigmaf"));
		EGGS.put("sigma", ValuesItem.of((char)963, "sigma", 963, "sigma"));
		EGGS.put("tau", ValuesItem.of((char)964, "tau", 964, "tau"));
		EGGS.put("upsilon", ValuesItem.of((char)965, "upsilon", 965, "upsilon"));
		EGGS.put("phi", ValuesItem.of((char)966, "phi", 966, "phi"));
		EGGS.put("chi", ValuesItem.of((char)967, "chi", 967, "chi"));
		EGGS.put("psi", ValuesItem.of((char)968, "psi", 968, "psi"));
		EGGS.put("omega", ValuesItem.of((char)969, "omega", 969, "omega"));
		EGGS.put("thetasym", ValuesItem.of((char)977, "thetasym", 977, "theta symbol"));
		EGGS.put("upsih", ValuesItem.of((char)978, "upsih", 978, "upsilon symbol"));
		EGGS.put("piv", ValuesItem.of((char)982, "piv", 982, "pi symbol"));
		EGGS.put("OElig", ValuesItem.of((char)338, "OElig", 338, "capital ligature OE"));
		EGGS.put("oelig", ValuesItem.of((char)339, "oelig", 339, "small ligature oe"));
		EGGS.put("Scaron", ValuesItem.of((char)352, "Scaron", 352, "capital S with caron"));
		EGGS.put("scaron", ValuesItem.of((char)353, "scaron", 353, "small S with caron"));
		EGGS.put("Yuml", ValuesItem.of((char)376, "Yuml", 376, "capital Y with diaeres"));
		EGGS.put("fnof", ValuesItem.of((char)402, "fnof", 402, "f with hook"));
		EGGS.put("circ", ValuesItem.of((char)710, "circ", 710, "modifier letter circumflex accent"));
		EGGS.put("tilde", ValuesItem.of((char)732, "tilde", 732, "small tilde"));
		EGGS.put("ensp", ValuesItem.of((char)8194, "ensp", 8194, "en space"));
		EGGS.put("emsp", ValuesItem.of((char)8195, "emsp", 8195, "em space"));
		EGGS.put("thinsp", ValuesItem.of((char)8201, "thinsp", 8201, "thin space"));
		EGGS.put("zwnj", ValuesItem.of((char)8204, "zwnj", 8204, "zero width non-joiner"));
		EGGS.put("zwj", ValuesItem.of((char)8205, "zwj", 8205, "zero width joiner"));
		EGGS.put("lrm", ValuesItem.of((char)8206, "lrm", 8206, "left-to-right mark"));
		EGGS.put("rlm", ValuesItem.of((char)8207, "rlm", 8207, "right-to-left mark"));
		EGGS.put("ndash", ValuesItem.of((char)8211, "ndash", 8211, "en dash"));
		EGGS.put("mdash", ValuesItem.of((char)8212, "mdash", 8212, "em dash"));
		EGGS.put("lsquo", ValuesItem.of((char)8216, "lsquo", 8216, "left single quotation mark"));
		EGGS.put("rsquo", ValuesItem.of((char)8217, "rsquo", 8217, "right single quotation mark"));
		EGGS.put("sbquo", ValuesItem.of((char)8218, "sbquo", 8218, "single low-9 quotation mark"));
		EGGS.put("ldquo", ValuesItem.of((char)8220, "ldquo", 8220, "left double quotation mark"));
		EGGS.put("rdquo", ValuesItem.of((char)8221, "rdquo", 8221, "right double quotation mark"));
		EGGS.put("bdquo", ValuesItem.of((char)8222, "bdquo", 8222, "double low-9 quotation mark"));
		EGGS.put("dagger", ValuesItem.of((char)8224, "dagger", 8224, "dagger"));
		EGGS.put("Dagger", ValuesItem.of((char)8225, "Dagger", 8225, "double dagger"));
		EGGS.put("bull", ValuesItem.of((char)8226, "bull", 8226, "bullet"));
		EGGS.put("hellip", ValuesItem.of((char)8230, "hellip", 8230, "horizontal ellipsis"));
		EGGS.put("permil", ValuesItem.of((char)8240, "permil", 8240, "per mille&nbsp;"));
		EGGS.put("prime", ValuesItem.of((char)8242, "prime", 8242, "minutes"));
		EGGS.put("Prime", ValuesItem.of((char)8243, "Prime", 8243, "seconds"));
		EGGS.put("lsaquo", ValuesItem.of((char)8249, "lsaquo", 8249, "single left angle quotation"));
		EGGS.put("rsaquo", ValuesItem.of((char)8250, "rsaquo", 8250, "single right angle quotation"));
		EGGS.put("oline", ValuesItem.of((char)8254, "oline", 8254, "overline"));
		EGGS.put("euro", ValuesItem.of((char)8364, "euro", 8364, "euro"));
		EGGS.put("trade", ValuesItem.of((char)8482, "trade", 8482, "trademark"));
		EGGS.put("larr", ValuesItem.of((char)8592, "larr", 8592, "left arrow"));
		EGGS.put("uarr", ValuesItem.of((char)8593, "uarr", 8593, "up arrow"));
		EGGS.put("rarr", ValuesItem.of((char)8594, "rarr", 8594, "right arrow"));
		EGGS.put("darr", ValuesItem.of((char)8595, "darr", 8595, "down arrow"));
		EGGS.put("harr", ValuesItem.of((char)8596, "harr", 8596, "left right arrow"));
		EGGS.put("crarr", ValuesItem.of((char)8629, "crarr", 8629, "carriage return arrow"));
		EGGS.put("lceil", ValuesItem.of((char)8968, "lceil", 8968, "left ceiling"));
		EGGS.put("rceil", ValuesItem.of((char)8969, "rceil", 8969, "right ceiling"));
		EGGS.put("lfloor", ValuesItem.of((char)8970, "lfloor", 8970, "left floor"));
		EGGS.put("rfloor", ValuesItem.of((char)8971, "rfloor", 8971, "right floor"));
		EGGS.put("loz", ValuesItem.of((char)9674, "loz", 9674, "lozenge"));
		EGGS.put("spades", ValuesItem.of((char)9824, "spades", 9824, "spade"));
		EGGS.put("clubs", ValuesItem.of((char)9827, "clubs", 9827, "club"));
		EGGS.put("hearts", ValuesItem.of((char)9829, "hearts", 9829, "heart"));
		EGGS.put("diams", ValuesItem.of((char)9830, "diams", 9830, "diamond"));
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
