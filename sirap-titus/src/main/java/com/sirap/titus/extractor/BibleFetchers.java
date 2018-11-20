package com.sirap.titus.extractor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.Mist;
import com.sirap.basic.json.MistUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.titus.BibleBook;

public class BibleFetchers {

	public static final String DEFAULT_BIBLE_CHINESE_VERSION = "CNVS";
	public static final String DEFAULT_BIBLE_ENGLISH_VERSION = "MEV";
	public static final Map<String, String> LANG_BIBLES = Amaps.newLinkHashMap();
	static {
		LANG_BIBLES.put("cn", DEFAULT_BIBLE_CHINESE_VERSION);
		LANG_BIBLES.put("en", DEFAULT_BIBLE_ENGLISH_VERSION);
		LANG_BIBLES.put("es", "JBS");
	}
	
	public static List<ValuesItem> getVersions() {
		Extractor<ValuesItem> frank = new Extractor<ValuesItem>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return "https://www.biblegateway.com/";
			}
			
			@Override
			protected void parse() {
				String regex = "<option value=\"([^\"]+)\"\\s*>([^<>]+)</option>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String code = ma.group(1).trim();
					String full = ma.group(2).trim();
//					String va = "VERSIONS.add(ValuesItem.of(\"{0}\", \"{1}\"));";
//					C.pl(StrUtil.occupy(va, code, full));
					mexItems.add(ValuesItem.of(code, full));
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}

	public static List<BibleBook> getBooks(String version) {
		Extractor<BibleBook> frank = new Extractor<BibleBook>() {
			
			@Override
			public String getUrl() {
				showFetching();
				String url = "https://www.biblegateway.com/passage/bbl/?version=" + version;
				return url;
			}
			
			protected void fetchx() {
				source = IOUtil.readString("E:/KDB/tasks/1108_Bible/nkjv.txt");
			}
			
			@Override
			protected void parse() {
				Object testaments = MistUtil.ofJsonText(source).findBy("testaments");
				List toms = (List)testaments;
				for(Object tom : toms) {
					String type = MistUtil.ofMapOrList(tom).valueOf("name") + "";
					Object columns = MistUtil.ofMapOrList(tom).valueOf("columns");
					for(Object cat : (List)columns) {
						List dogs = (List)cat;
						for(Object dog : dogs) {
							Mist mix = MistUtil.ofMapOrList(dog);
//							D.pjsp(cat);
							int id = Integer.parseInt(mix.findStringBy("id"));
							String name = mix.findStringBy("name");
							Object chapters = mix.findBy("chapters");
//							D.pl((List)chapters);
							int max = ((List)chapters).size();
							mexItems.add(new BibleBook(id, type, name, max));
							String va = "BOOKS.add(new BibleBook({0}, \"{1}\", \"{2}\", {3}));";
//							C.pl(StrUtil.occupy(va, id, type, name, max));
						}
//						break;
					}
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static void main(String[] args) {
		getVersesFromBibleGateway();
//		C.list(getVersesFromBibleGateway());
//		D.pjsp(getSpecificChapter("C:/mastorage/bible", "CUV", "5", "7"));
	}
	
	public static List<String> getVersesFromBibleGateway() {
		return getVersesFromBibleGateway(null, 23, "");
	}

	public static List<String> getVersesFromBibleGateway(String bookName, int chapterId, String version) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching().useUTF8();
				String search = encodeURLParam(bookName + " " + chapterId);
				String url = "https://www.biblegateway.com/passage/?search={0}&version={1}";
//				url = "https://www.biblegateway.com/passage/?search=Jeremiah+39&version=NIVUK";
				return StrUtil.occupy(url, search, version);
			}
			
			protected void fetchx() {
				String va = "gen2";
				va = "jer38";
				va = "lec1";
				va = "lec4";
				va = "qi22";
				source = IOUtil.readString(StrUtil.occupy("E:/KDB/tasks/1108_Bible/{0}.txt", va));
			}
			
			@Override
			protected void parse() {
				//<h1 class="passage-display">(.+)</span></p>
				String regex = "(<h1 class=\"passage-display\".+?</h1>)(.+</span>\\s*</p>)";
				Matcher ma = createMatcher(regex);
				if(ma.find()) {
					Object spans = MistUtil.ofXmlText(ma.group(1)).valueOf(".span");
					String head = StrUtil.connectWithCommaSpace((List)spans);
					mexItems.add(head);
					mexItems.add(getUrl());
					mexItems.add("");
					
					String body = ma.group(2);
					String temp = body;
					temp = temp.replaceAll("<[^<>]*h3[^<>]*>", "#my#& ");
					temp = temp.replaceAll("<[^<>]*chapternum[^<>]*>", "#my#C#");
					temp = temp.replaceAll("<[^<>]*versenum[^<>]*>", "#my#K#");
					temp = getPrettyText(temp);
					List<String> lines = StrUtil.splitByRegex(temp, "#my#");
//					D.list(lines);
					for(String line : lines) {
						if(line.isEmpty()) {
							continue;
						}
//						D.pl(line);
						String regexC = "^C#\\d{1,3}";
						if(StrUtil.isRegexFound(regexC, line)) {
							mexItems.add(line.replaceAll(regexC, "#1"));
						} else if(StrUtil.isRegexFound("^K", line)) {
							mexItems.add(line.replaceAll("^K", ""));
						} else if(StrUtil.isRegexFound("^&.+", line)) {
							mexItems.add(line);
						} else {
//							D.sink(line);
						}
					}
				} else {
					D.err(getUrl());
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
}
