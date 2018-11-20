package com.sirap.titus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class BibleData {
	static final Map<Integer, int[]> CHAPTERS_OLD_NEW_ALL = new HashMap<>();
	static {
		CHAPTERS_OLD_NEW_ALL.put(39, new int[] {1, 39});
		CHAPTERS_OLD_NEW_ALL.put(27, new int[] {40, 66});
		CHAPTERS_OLD_NEW_ALL.put(66, new int[] {1, 66});
	}
	
	public static BibleBook findByBookNumber(int k) {
		for(BibleBook book : BOOKS) {
			if(book.getId() == k) {
				return book;
			}
		}
		
		XXXUtil.alert("No such book with order {0}, max is {1}", k, BOOKS.size());
		return null;
	}
	
	public static final List<BibleBook> BOOKS = new ArrayList<>();
	static {
		BOOKS.add(new BibleBook(1, "OT", "Genesis", 50));
		BOOKS.add(new BibleBook(2, "OT", "Exodus", 40));
		BOOKS.add(new BibleBook(3, "OT", "Leviticus", 27));
		BOOKS.add(new BibleBook(4, "OT", "Numbers", 36));
		BOOKS.add(new BibleBook(5, "OT", "Deuteronomy", 34));
		BOOKS.add(new BibleBook(6, "OT", "Joshua", 24));
		BOOKS.add(new BibleBook(7, "OT", "Judges", 21));
		BOOKS.add(new BibleBook(8, "OT", "Ruth", 4));
		BOOKS.add(new BibleBook(9, "OT", "1 Samuel", 31));
		BOOKS.add(new BibleBook(10, "OT", "2 Samuel", 24));
		BOOKS.add(new BibleBook(11, "OT", "1 Kings", 22));
		BOOKS.add(new BibleBook(12, "OT", "2 Kings", 25));
		BOOKS.add(new BibleBook(13, "OT", "1 Chronicles", 29));
		BOOKS.add(new BibleBook(14, "OT", "2 Chronicles", 36));
		BOOKS.add(new BibleBook(15, "OT", "Ezra", 10));
		BOOKS.add(new BibleBook(16, "OT", "Nehemiah", 13));
		BOOKS.add(new BibleBook(19, "OT", "Esther", 10));
		BOOKS.add(new BibleBook(22, "OT", "Job", 42));
		BOOKS.add(new BibleBook(23, "OT", "Psalm", 150));
		BOOKS.add(new BibleBook(24, "OT", "Proverbs", 31));
		BOOKS.add(new BibleBook(25, "OT", "Ecclesiastes", 12));
		BOOKS.add(new BibleBook(26, "OT", "Song of Solomon", 8));
		BOOKS.add(new BibleBook(29, "OT", "Isaiah", 66));
		BOOKS.add(new BibleBook(30, "OT", "Jeremiah", 52));
		BOOKS.add(new BibleBook(31, "OT", "Lamentations", 5));
		BOOKS.add(new BibleBook(33, "OT", "Ezekiel", 48));
		BOOKS.add(new BibleBook(34, "OT", "Daniel", 12));
		BOOKS.add(new BibleBook(35, "OT", "Hosea", 14));
		BOOKS.add(new BibleBook(36, "OT", "Joel", 3));
		BOOKS.add(new BibleBook(37, "OT", "Amos", 9));
		BOOKS.add(new BibleBook(38, "OT", "Obadiah", 1));
		BOOKS.add(new BibleBook(39, "OT", "Jonah", 4));
		BOOKS.add(new BibleBook(40, "OT", "Micah", 7));
		BOOKS.add(new BibleBook(41, "OT", "Nahum", 3));
		BOOKS.add(new BibleBook(42, "OT", "Habakkuk", 3));
		BOOKS.add(new BibleBook(43, "OT", "Zephaniah", 3));
		BOOKS.add(new BibleBook(44, "OT", "Haggai", 2));
		BOOKS.add(new BibleBook(45, "OT", "Zechariah", 14));
		BOOKS.add(new BibleBook(46, "OT", "Malachi", 4));
		BOOKS.add(new BibleBook(47, "NT", "Matthew", 28));
		BOOKS.add(new BibleBook(48, "NT", "Mark", 16));
		BOOKS.add(new BibleBook(49, "NT", "Luke", 24));
		BOOKS.add(new BibleBook(50, "NT", "John", 21));
		BOOKS.add(new BibleBook(51, "NT", "Acts", 28));
		BOOKS.add(new BibleBook(52, "NT", "Romans", 16));
		BOOKS.add(new BibleBook(53, "NT", "1 Corinthians", 16));
		BOOKS.add(new BibleBook(54, "NT", "2 Corinthians", 13));
		BOOKS.add(new BibleBook(55, "NT", "Galatians", 6));
		BOOKS.add(new BibleBook(56, "NT", "Ephesians", 6));
		BOOKS.add(new BibleBook(57, "NT", "Philippians", 4));
		BOOKS.add(new BibleBook(58, "NT", "Colossians", 4));
		BOOKS.add(new BibleBook(59, "NT", "1 Thessalonians", 5));
		BOOKS.add(new BibleBook(60, "NT", "2 Thessalonians", 3));
		BOOKS.add(new BibleBook(61, "NT", "1 Timothy", 6));
		BOOKS.add(new BibleBook(62, "NT", "2 Timothy", 4));
		BOOKS.add(new BibleBook(63, "NT", "Titus", 3));
		BOOKS.add(new BibleBook(64, "NT", "Philemon", 1));
		BOOKS.add(new BibleBook(65, "NT", "Hebrews", 13));
		BOOKS.add(new BibleBook(66, "NT", "James", 5));
		BOOKS.add(new BibleBook(67, "NT", "1 Peter", 5));
		BOOKS.add(new BibleBook(68, "NT", "2 Peter", 3));
		BOOKS.add(new BibleBook(69, "NT", "1 John", 5));
		BOOKS.add(new BibleBook(70, "NT", "2 John", 1));
		BOOKS.add(new BibleBook(71, "NT", "3 John", 1));
		BOOKS.add(new BibleBook(72, "NT", "Jude", 1));
		BOOKS.add(new BibleBook(73, "NT", "Revelation", 22));
	}

	public static final String HOMEPAGE = "http://www.biblestudytools.com";
	public static final List<ValuesItem> VERSIONS = new ArrayList<>();

	public static String versionCodeOf(String code) {
		for(ValuesItem item : VERSIONS) {
			String current = item.stringAt(0);
			if(StrUtil.equals(current, code)) {
				return current;
			}
			if(StrUtil.equals(current.replace("-", ""), code)) {
				return current;
			}
		}
		
		XXXUtil.alert("No such version with code {0}", code);
		return null;
	}
	
	static {
		VERSIONS.add(ValuesItem.of("AMU", "Amuzgo de Guerrero (AMU)"));
		VERSIONS.add(ValuesItem.of("ERV-AR", "Arabic Bible: Easy-to-Read Version (ERV-AR)"));
		VERSIONS.add(ValuesItem.of("NAV", "Ketab El Hayat (NAV)"));
		VERSIONS.add(ValuesItem.of("ERV-AWA", "Awadhi Bible: Easy-to-Read Version (ERV-AWA)"));
		VERSIONS.add(ValuesItem.of("BG1940", "1940 Bulgarian Bible (BG1940)"));
		VERSIONS.add(ValuesItem.of("BULG", "Bulgarian Bible (BULG)"));
		VERSIONS.add(ValuesItem.of("ERV-BG", "Bulgarian New Testament: Easy-to-Read Version (ERV-BG)"));
		VERSIONS.add(ValuesItem.of("CBT", "Библия, нов превод от оригиналните езици (с неканоничните книги) (CBT)"));
		VERSIONS.add(ValuesItem.of("BOB", "Библия, синодално издание (BOB)"));
		VERSIONS.add(ValuesItem.of("BPB", "Библия, ревизирано издание (BPB)"));
		VERSIONS.add(ValuesItem.of("CCO", "Chinanteco de Comaltepec (CCO)"));
		VERSIONS.add(ValuesItem.of("APSD-CEB", "Ang Pulong Sa Dios (APSD-CEB)"));
		VERSIONS.add(ValuesItem.of("CHR", "Cherokee New Testament (CHR)"));
		VERSIONS.add(ValuesItem.of("CKW", "Cakchiquel Occidental (CKW)"));
		VERSIONS.add(ValuesItem.of("B21", "Bible 21 (B21)"));
		VERSIONS.add(ValuesItem.of("SNC", "Slovo na cestu (SNC)"));
		VERSIONS.add(ValuesItem.of("BWM", "Beibl William Morgan (BWM)"));
		VERSIONS.add(ValuesItem.of("BPH", "Bibelen p? hverdagsdansk (BPH)"));
		VERSIONS.add(ValuesItem.of("DN1933", "Dette er Biblen p? dansk (DN1933)"));
		VERSIONS.add(ValuesItem.of("HOF", "Hoffnung für Alle (HOF)"));
		VERSIONS.add(ValuesItem.of("LUTH1545", "Luther Bibel 1545 (LUTH1545)"));
		VERSIONS.add(ValuesItem.of("NGU-DE", "Neue Genfer ?bersetzung (NGU-DE)"));
		VERSIONS.add(ValuesItem.of("SCH1951", "Schlachter 1951 (SCH1951)"));
		VERSIONS.add(ValuesItem.of("SCH2000", "Schlachter 2000 (SCH2000)"));
		VERSIONS.add(ValuesItem.of("KJ21", "21st Century King James Version (KJ21)"));
		VERSIONS.add(ValuesItem.of("ASV", "American Standard Version (ASV)"));
		VERSIONS.add(ValuesItem.of("AMP", "Amplified Bible (AMP)"));
		VERSIONS.add(ValuesItem.of("AMPC", "Amplified Bible, Classic Edition (AMPC)"));
		VERSIONS.add(ValuesItem.of("BRG", "BRG Bible (BRG)"));
		VERSIONS.add(ValuesItem.of("CSB", "Christian Standard Bible (CSB)"));
		VERSIONS.add(ValuesItem.of("CEB", "Common English Bible (CEB)"));
		VERSIONS.add(ValuesItem.of("CJB", "Complete Jewish Bible (CJB)"));
		VERSIONS.add(ValuesItem.of("CEV", "Contemporary English Version (CEV)"));
		VERSIONS.add(ValuesItem.of("DARBY", "Darby Translation (DARBY)"));
		VERSIONS.add(ValuesItem.of("DLNT", "Disciples’ Literal New Testament (DLNT)"));
		VERSIONS.add(ValuesItem.of("DRA", "Douay-Rheims 1899 American Edition (DRA)"));
		VERSIONS.add(ValuesItem.of("ERV", "Easy-to-Read Version (ERV)"));
		VERSIONS.add(ValuesItem.of("EHV", "Evangelical Heritage Version (EHV)"));
		VERSIONS.add(ValuesItem.of("ESV", "English Standard Version (ESV)"));
		VERSIONS.add(ValuesItem.of("ESVUK", "English Standard Version Anglicised (ESVUK)"));
		VERSIONS.add(ValuesItem.of("EXB", "Expanded Bible (EXB)"));
		VERSIONS.add(ValuesItem.of("GNV", "1599 Geneva Bible (GNV)"));
		VERSIONS.add(ValuesItem.of("GW", "GOD’S WORD Translation (GW)"));
		VERSIONS.add(ValuesItem.of("GNT", "Good News Translation (GNT)"));
		VERSIONS.add(ValuesItem.of("HCSB", "Holman Christian Standard Bible (HCSB)"));
		VERSIONS.add(ValuesItem.of("ICB", "International Children’s Bible (ICB)"));
		VERSIONS.add(ValuesItem.of("ISV", "International Standard Version (ISV)"));
		VERSIONS.add(ValuesItem.of("PHILLIPS", "J.B. Phillips New Testament (PHILLIPS)"));
		VERSIONS.add(ValuesItem.of("JUB", "Jubilee Bible 2000 (JUB)"));
		VERSIONS.add(ValuesItem.of("KJV", "King James Version (KJV)"));
		VERSIONS.add(ValuesItem.of("AKJV", "Authorized (King James) Version (AKJV)"));
		VERSIONS.add(ValuesItem.of("LEB", "Lexham English Bible (LEB)"));
		VERSIONS.add(ValuesItem.of("TLB", "Living Bible (TLB)"));
		VERSIONS.add(ValuesItem.of("MSG", "The Message (MSG)"));
		VERSIONS.add(ValuesItem.of("MEV", "Modern English Version (MEV)"));
		VERSIONS.add(ValuesItem.of("MOUNCE", "Mounce Reverse-Interlinear New Testament (MOUNCE)"));
		VERSIONS.add(ValuesItem.of("NOG", "Names of God Bible (NOG)"));
		VERSIONS.add(ValuesItem.of("NABRE", "New American Bible (Revised Edition) (NABRE)"));
		VERSIONS.add(ValuesItem.of("NASB", "New American Standard Bible (NASB)"));
		VERSIONS.add(ValuesItem.of("NCV", "New Century Version (NCV)"));
		VERSIONS.add(ValuesItem.of("NET", "New English Translation (NET Bible)"));
		VERSIONS.add(ValuesItem.of("NIRV", "New International Reader&#039;s Version (NIRV)"));
		VERSIONS.add(ValuesItem.of("NIVUK", "New International Version - UK (NIVUK)"));
		VERSIONS.add(ValuesItem.of("NKJV", "New King James Version (NKJV)"));
		VERSIONS.add(ValuesItem.of("NLV", "New Life Version (NLV)"));
		VERSIONS.add(ValuesItem.of("NLT", "New Living Translation (NLT)"));
		VERSIONS.add(ValuesItem.of("NMB", "New Matthew Bible (NMB)"));
		VERSIONS.add(ValuesItem.of("NRSV", "New Revised Standard Version (NRSV)"));
		VERSIONS.add(ValuesItem.of("NRSVA", "New Revised Standard Version, Anglicised (NRSVA)"));
		VERSIONS.add(ValuesItem.of("NRSVACE", "New Revised Standard Version, Anglicised Catholic Edition (NRSVACE)"));
		VERSIONS.add(ValuesItem.of("NRSVCE", "New Revised Standard Version Catholic Edition (NRSVCE)"));
		VERSIONS.add(ValuesItem.of("NTE", "New Testament for Everyone (NTE)"));
		VERSIONS.add(ValuesItem.of("OJB", "Orthodox Jewish Bible (OJB)"));
		VERSIONS.add(ValuesItem.of("TPT", "The Passion Translation (TPT)"));
		VERSIONS.add(ValuesItem.of("RSV", "Revised Standard Version (RSV)"));
		VERSIONS.add(ValuesItem.of("RSVCE", "Revised Standard Version Catholic Edition (RSVCE)"));
		VERSIONS.add(ValuesItem.of("TLV", "Tree of Life Version (TLV)"));
		VERSIONS.add(ValuesItem.of("VOICE", "The Voice (VOICE)"));
		VERSIONS.add(ValuesItem.of("WEB", "World English Bible (WEB)"));
		VERSIONS.add(ValuesItem.of("WE", "Worldwide English (New Testament) (WE)"));
		VERSIONS.add(ValuesItem.of("WYC", "Wycliffe Bible (WYC)"));
		VERSIONS.add(ValuesItem.of("YLT", "Young&#039;s Literal Translation (YLT)"));
		VERSIONS.add(ValuesItem.of("LBLA", "La Biblia de las Américas (LBLA)"));
		VERSIONS.add(ValuesItem.of("DHH", "Dios Habla Hoy (DHH)"));
		VERSIONS.add(ValuesItem.of("JBS", "Jubilee Bible 2000 (Spanish) (JBS)"));
		VERSIONS.add(ValuesItem.of("NBLH", "Nueva Biblia Latinoamericana de Hoy (NBLH)"));
		VERSIONS.add(ValuesItem.of("NBV", "Nueva Biblia Viva (NBV)"));
		VERSIONS.add(ValuesItem.of("NTV", "Nueva Traducción Viviente (NTV)"));
		VERSIONS.add(ValuesItem.of("NVI", "Nueva Versión Internacional (NVI)"));
		VERSIONS.add(ValuesItem.of("CST", "Nueva Versión Internacional (Castilian) (CST)"));
		VERSIONS.add(ValuesItem.of("PDT", "Palabra de Dios para Todos (PDT)"));
		VERSIONS.add(ValuesItem.of("BLP", "La Palabra (Espa?a) (BLP)"));
		VERSIONS.add(ValuesItem.of("BLPH", "La Palabra (Hispanoamérica) (BLPH)"));
		VERSIONS.add(ValuesItem.of("RVA-2015", "Reina Valera Actualizada (RVA-2015)"));
		VERSIONS.add(ValuesItem.of("RVC", "Reina Valera Contemporánea (RVC)"));
		VERSIONS.add(ValuesItem.of("RVR1960", "Reina-Valera 1960 (RVR1960)"));
		VERSIONS.add(ValuesItem.of("RVR1977", "Reina Valera Revisada (RVR1977)"));
		VERSIONS.add(ValuesItem.of("RVR1995", "Reina-Valera 1995 (RVR1995)"));
		VERSIONS.add(ValuesItem.of("RVA", "Reina-Valera Antigua (RVA)"));
		VERSIONS.add(ValuesItem.of("SRV-BRG", "Spanish Blue Red and Gold Letter Edition (SRV-BRG)"));
		VERSIONS.add(ValuesItem.of("TLA", "Traducción en lenguaje actual (TLA)"));
		VERSIONS.add(ValuesItem.of("R1933", "Raamattu 1933/38 (R1933)"));
		VERSIONS.add(ValuesItem.of("BDS", "La Bible du Semeur (BDS)"));
		VERSIONS.add(ValuesItem.of("LSG", "Louis Segond (LSG)"));
		VERSIONS.add(ValuesItem.of("NEG1979", "Nouvelle Edition de Genève – NEG1979 (NEG1979)"));
		VERSIONS.add(ValuesItem.of("SG21", "Segond 21 (SG21)"));
		VERSIONS.add(ValuesItem.of("TR1550", "1550 Stephanus New Testament (TR1550)"));
		VERSIONS.add(ValuesItem.of("WHNU", "1881 Westcott-Hort New Testament (WHNU)"));
		VERSIONS.add(ValuesItem.of("TR1894", "1894 Scrivener New Testament (TR1894)"));
		VERSIONS.add(ValuesItem.of("SBLGNT", "SBL Greek New Testament (SBLGNT)"));
		VERSIONS.add(ValuesItem.of("HHH", "Habrit Hakhadasha/Haderekh (HHH)"));
		VERSIONS.add(ValuesItem.of("WLC", "The Westminster Leningrad Codex (WLC)"));
		VERSIONS.add(ValuesItem.of("ERV-HI", "Hindi Bible: Easy-to-Read Version (ERV-HI)"));
		VERSIONS.add(ValuesItem.of("HLGN", "Ang Pulong Sang Dios (HLGN)"));
		VERSIONS.add(ValuesItem.of("HNZ-RI", "Hrvatski Novi Zavjet – Rijeka 2001 (HNZ-RI)"));
		VERSIONS.add(ValuesItem.of("CRO", "Knijga O Kristu (CRO)"));
		VERSIONS.add(ValuesItem.of("HCV", "Haitian Creole Version (HCV)"));
		VERSIONS.add(ValuesItem.of("KAR", "Hungarian Károli (KAR)"));
		VERSIONS.add(ValuesItem.of("ERV-HU", "Hungarian Bible: Easy-to-Read Version (ERV-HU)"));
		VERSIONS.add(ValuesItem.of("NT-HU", "Hungarian New Translation (NT-HU)"));
		VERSIONS.add(ValuesItem.of("HWP", "Hawai‘i Pidgin (HWP)"));
		VERSIONS.add(ValuesItem.of("ICELAND", "Icelandic Bible (ICELAND)"));
		VERSIONS.add(ValuesItem.of("BDG", "La Bibbia della Gioia (BDG)"));
		VERSIONS.add(ValuesItem.of("CEI", "Conferenza Episcopale Italiana (CEI)"));
		VERSIONS.add(ValuesItem.of("LND", "La Nuova Diodati (LND)"));
		VERSIONS.add(ValuesItem.of("NR1994", "Nuova Riveduta 1994 (NR1994)"));
		VERSIONS.add(ValuesItem.of("NR2006", "Nuova Riveduta 2006 (NR2006)"));
		VERSIONS.add(ValuesItem.of("JLB", "Japanese Living Bible (JLB)"));
		VERSIONS.add(ValuesItem.of("JAC", "Jacalteco, Oriental (JAC)"));
		VERSIONS.add(ValuesItem.of("KEK", "Kekchi (KEK)"));
		VERSIONS.add(ValuesItem.of("KLB", "Korean Living Bible (KLB)"));
		VERSIONS.add(ValuesItem.of("VULGATE", "Biblia Sacra Vulgata (VULGATE)"));
		VERSIONS.add(ValuesItem.of("MAORI", "Maori Bible (MAORI)"));
		VERSIONS.add(ValuesItem.of("MNT", "Macedonian New Testament (MNT)"));
		VERSIONS.add(ValuesItem.of("ERV-MR", "Marathi Bible: Easy-to-Read Version (ERV-MR)"));
		VERSIONS.add(ValuesItem.of("MVC", "Mam, Central (MVC)"));
		VERSIONS.add(ValuesItem.of("MVJ", "Mam de Todos Santos Chuchumatán (MVJ)"));
		VERSIONS.add(ValuesItem.of("REIMER", "Reimer 2001 (REIMER)"));
		VERSIONS.add(ValuesItem.of("ERV-NE", "Nepali Bible: Easy-to-Read Version (ERV-NE)"));
		VERSIONS.add(ValuesItem.of("NGU", "Náhuatl de Guerrero (NGU)"));
		VERSIONS.add(ValuesItem.of("BB", "BasisBijbel (BB)"));
		VERSIONS.add(ValuesItem.of("HTB", "Het Boek (HTB)"));
		VERSIONS.add(ValuesItem.of("DNB1930", "Det Norsk Bibelselskap 1930 (DNB1930)"));
		VERSIONS.add(ValuesItem.of("LB", "En Levende Bok (LB)"));
		VERSIONS.add(ValuesItem.of("ERV-OR", "Oriya Bible: Easy-to-Read Version (ERV-OR)"));
		VERSIONS.add(ValuesItem.of("ERV-PA", "Punjabi Bible: Easy-to-Read Version (ERV-PA)"));
		VERSIONS.add(ValuesItem.of("NP", "Nowe Przymierze (NP)"));
		VERSIONS.add(ValuesItem.of("SZ-PL", "S?owo ?ycia (SZ-PL)"));
		VERSIONS.add(ValuesItem.of("UBG", "Updated Gdańsk Bible (UBG)"));
		VERSIONS.add(ValuesItem.of("NBTN", "Ne Bibliaj Tik Nawat (NBTN)"));
		VERSIONS.add(ValuesItem.of("ARC", "Almeida Revista e Corrigida 2009 (ARC)"));
		VERSIONS.add(ValuesItem.of("NTLH", "Nova Tradu??o na Linguagem de Hoje 2000 (NTLH)"));
		VERSIONS.add(ValuesItem.of("NVT", "Nova Vers?o Transformadora (NVT)"));
		VERSIONS.add(ValuesItem.of("NVI-PT", "Nova Vers?o Internacional (NVI-PT)"));
		VERSIONS.add(ValuesItem.of("OL", "O Livro (OL)"));
		VERSIONS.add(ValuesItem.of("VFL", "Portuguese New Testament: Easy-to-Read Version (VFL)"));
		VERSIONS.add(ValuesItem.of("MTDS", "Mushuj Testamento Diospaj Shimi (MTDS)"));
		VERSIONS.add(ValuesItem.of("QUT", "Quiché, Centro Occidental (QUT)"));
		VERSIONS.add(ValuesItem.of("RMNN", "Cornilescu 1924 - Revised 2010, 2014 (RMNN)"));
		VERSIONS.add(ValuesItem.of("NTLR", "Nou? Traducere ?n Limba Rom?n? (NTLR)"));
		VERSIONS.add(ValuesItem.of("NRT", "New Russian Translation (NRT)"));
		VERSIONS.add(ValuesItem.of("CARS", "Священное Писание (Восточный Перевод) (CARS)"));
		VERSIONS.add(ValuesItem.of("CARST", "Священное Писание (Восточный перевод), версия для Таджикистана (CARST)"));
		VERSIONS.add(ValuesItem.of("CARSA", "Священное Писание (Восточный перевод), версия с ?Аллахом? (CARSA)"));
		VERSIONS.add(ValuesItem.of("ERV-RU", "Russian New Testament: Easy-to-Read Version (ERV-RU)"));
		VERSIONS.add(ValuesItem.of("RUSV", "Russian Synodal Version (RUSV)"));
		VERSIONS.add(ValuesItem.of("NPK", "Nádej pre kazdého (NPK)"));
		VERSIONS.add(ValuesItem.of("SOM", "Somali Bible (SOM)"));
		VERSIONS.add(ValuesItem.of("ALB", "Albanian Bible (ALB)"));
		VERSIONS.add(ValuesItem.of("ERV-SR", "Serbian New Testament: Easy-to-Read Version (ERV-SR)"));
		VERSIONS.add(ValuesItem.of("NUB", "nuBibeln (Swedish Contemporary Bible) (NUB)"));
		VERSIONS.add(ValuesItem.of("SV1917", "Svenska 1917 (SV1917)"));
		VERSIONS.add(ValuesItem.of("SFB", "Svenska Folkbibeln (SFB)"));
		VERSIONS.add(ValuesItem.of("SFB15", "Svenska Folkbibeln 2015 (SFB15)"));
		VERSIONS.add(ValuesItem.of("SNT", "Neno: Bibilia Takatifu (SNT)"));
		VERSIONS.add(ValuesItem.of("ERV-TA", "Tamil Bible: Easy-to-Read Version (ERV-TA)"));
		VERSIONS.add(ValuesItem.of("TNCV", "Thai New Contemporary Bible (TNCV)"));
		VERSIONS.add(ValuesItem.of("ERV-TH", "Thai New Testament: Easy-to-Read Version (ERV-TH)"));
		VERSIONS.add(ValuesItem.of("FSV", "Ang Bagong Tipan: Filipino Standard Version (FSV)"));
		VERSIONS.add(ValuesItem.of("ABTAG1978", "Ang Biblia (1978) (ABTAG1978)"));
		VERSIONS.add(ValuesItem.of("ABTAG2001", "Ang Biblia, 2001 (ABTAG2001)"));
		VERSIONS.add(ValuesItem.of("ADB1905", "Ang Dating Biblia (1905) (ADB1905)"));
		VERSIONS.add(ValuesItem.of("SND", "Ang Salita ng Diyos (SND)"));
		VERSIONS.add(ValuesItem.of("MBBTAG", "Magandang Balita Biblia (MBBTAG)"));
		VERSIONS.add(ValuesItem.of("MBBTAG-DC", "Magandang Balita Biblia (with Deuterocanon) (MBBTAG-DC)"));
		VERSIONS.add(ValuesItem.of("NA-TWI", "Nkwa Asem (NA-TWI)"));
		VERSIONS.add(ValuesItem.of("UKR", "Ukrainian Bible (UKR)"));
		VERSIONS.add(ValuesItem.of("ERV-UK", "Ukrainian New Testament: Easy-to-Read Version (ERV-UK)"));
		VERSIONS.add(ValuesItem.of("ERV-UR", "Urdu Bible: Easy-to-Read Version (ERV-UR)"));
		VERSIONS.add(ValuesItem.of("USP", "Uspanteco (USP)"));
		VERSIONS.add(ValuesItem.of("VIET", "1934 Vietnamese Bible (VIET)"));
		VERSIONS.add(ValuesItem.of("BD2011", "B?n D?ch 2011 (BD2011)"));
		VERSIONS.add(ValuesItem.of("NVB", "New Vietnamese Bible (NVB)"));
		VERSIONS.add(ValuesItem.of("BPT", "Vietnamese Bible: Easy-to-Read Version (BPT)"));
		VERSIONS.add(ValuesItem.of("CCB", "Chinese Contemporary Bible (Simplified) (CCB)"));
		VERSIONS.add(ValuesItem.of("CCBT", "Chinese Contemporary Bible (Traditional) (CCBT)"));
		VERSIONS.add(ValuesItem.of("ERV-ZH", "Chinese New Testament: Easy-to-Read Version (ERV-ZH)"));
		VERSIONS.add(ValuesItem.of("CNVS", "Chinese New Version (Simplified) (CNVS)"));
		VERSIONS.add(ValuesItem.of("CNVT", "Chinese New Version (Traditional) (CNVT)"));
		VERSIONS.add(ValuesItem.of("CSBS", "Chinese Standard Bible (Simplified) (CSBS)"));
		VERSIONS.add(ValuesItem.of("CSBT", "Chinese Standard Bible (Traditional) (CSBT)"));
		VERSIONS.add(ValuesItem.of("CUVS", "Chinese Union Version (Simplified) (CUVS)"));
		VERSIONS.add(ValuesItem.of("CUV", "Chinese Union Version (Traditional) (CUV)"));
		VERSIONS.add(ValuesItem.of("CUVMPS", "Chinese Union Version Modern Punctuation (Simplified) (CUVMPS)"));
		VERSIONS.add(ValuesItem.of("CUVMPT", "Chinese Union Version Modern Punctuation (Traditional) (CUVMPT)"));
		VERSIONS.add(ValuesItem.of("RCU17SS", "Revised Chinese Union Version (Simplified Script) Shen Edition (RCU17SS)"));
		VERSIONS.add(ValuesItem.of("RCU17TS", "Revised Chinese Union Version (Traditional Script) Shen Edition (RCU17TS)"));
	}
}
