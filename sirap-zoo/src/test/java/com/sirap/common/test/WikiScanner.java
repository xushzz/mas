package com.sirap.common.test;
//package com.pirate.common.test;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.pirate.basic.component.Konstants;
//import com.pirate.basic.domain.MexItem;
//import com.pirate.basic.domain.MexedFile;
//import com.pirate.basic.domain.MexedObject;
//import com.pirate.basic.thread.Master;
//import com.pirate.basic.thread.Worker;
//import com.pirate.basic.tool.C;
//import com.pirate.basic.tool.D;
//import com.pirate.basic.util.DateUtil;
//import com.pirate.basic.util.FileUtil;
//import com.pirate.basic.util.IOUtil;
//import com.pirate.basic.util.StringUtil;
//import com.pirate.common.extractor.Extractor;
//
//class NormalFileDestroyer extends Worker<MexedObject> {
//	
//	public void process(MexedObject obj) {
//		
//		String name = obj.getString();
////		String name = url.replace("http://en.wikipedia.org/wiki/", "E:/wiki/") + Konstants.SUFFIX_TXT;
//		int count = countOfTasks - tasks.size();
//		File file = new File(name);
////		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Deleting...", name);
//		String result = "Nonexist";
//		if(file.exists()) {
//			file.delete();
//			result = "Deleted";
//		}
////		D.pl(file);
//		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, result, name);
//	}
//}
//
//class FileContentReader extends Worker<MexedFile> {
//	
//	private List<String> grandList;
//	
//	public FileContentReader(List<String> grandList) {
//		this.grandList = grandList;
//	}
//	
//	public void process(MexedFile obj) {
//		
//		String path = obj.getPath();
//		int count = countOfTasks - tasks.size();
//		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Reading...", path);
//		File file = obj.getFile();
//		String fileName = file.getAbsolutePath();
//		List<String> records = IOUtil.readFileIntoList(fileName);
//		grandList.addAll(records);
//		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Done.", path);
//	}
//}
//
//public class WikiScanner {
//	
//	public static final String HOMEPAGE = "http://en.wikipedia.org/wiki/";
//	private Set<String> GRAND_SET = new HashSet<String>();
//	
//	private int maxDepth;
//	
//	private WikiScanner(int maxDepth) {
//		this.maxDepth = maxDepth;
//		
////		scan(url, 0);
//	}
//	
//	public static void main(String[] args) {
//		String url = "http://en.wikipedia.org/wiki/Great_Britain";
//		WikiScanner lee = new WikiScanner(5);
////		lee.writeTemp();
////		lee.readCache();
////		lee.cleanEmptyWiki();
////		lee.cleanWikiByNames();
////		lee.consolidate();
////		lee.scan(url, 0);
//	}
//	
//	private void consolidate() {
//		//E:\MasPro\exp\wiki_britain
//		String filePath = "E:/MasPro/exp/wiki_britain/";
//		File folder = FileUtil.getIfNormalFolder(filePath);
//		C.pl("Calculating, " + DateUtil.displayNow());
//		List<MexedFile> sourcefiles = new ArrayList<MexedFile>();
//		File[] files = folder.listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				sourcefiles.add(new MexedFile(pathname));
//				return false;
//			}
//		});
//		
//		C.pl("[before] " + files.length);
//		C.pl(DateUtil.displayNow());
//		List<String> grandList = new ArrayList<String>();
//		Master<MexedFile> george = new Master<MexedFile>(sourcefiles, new FileContentReader(grandList));
//		george.sitAndWait();
//		C.pl("Size " + grandList.size());
//		IOUtil.saveAsTxt(grandList, "E:/MasPro/exp/bri_" + DateUtil.timestamp() + Konstants.SUFFIX_TXT);
//		C.pl(DateUtil.displayNow());
//	}
//	
//	private void cleanWikiByNames() {
//		String filePath = "E:/MasPro/exp/bri2.txt";
//		List<String> urls = IOUtil.readFileIntoList(filePath);
//		List<MexedObject> list = new ArrayList<MexedObject>();
//		for(String url:urls) {
//			list.add(new MexedObject(url));
//		}
//		
//		Master<MexedObject> george = new Master<MexedObject>(list, new NormalFileDestroyer()) {
//			protected int countOfThread() {
//				return 1000;
//			}
//		};
//		george.sitAndWait();
//	}
//	
//	private void writeTemp() {
//		String filePath = "E:/wiki/S.T.A.L.K.E.R.";
//		List<String> list = new ArrayList<String>();
//		list.add("1917");
//		IOUtil.saveAsTxt(list, filePath);
//	}
//	
//	private void cleanEmptyWiki() {
//		String filePath = "E:/MasPro/wiki";
//		filePath = "E:/wiki";
//		File folder = FileUtil.getIfNormalFolder(filePath);
//		C.pl("Calculating, " + DateUtil.displayNow());
//		List<MexedObject> sourcefiles = new ArrayList<MexedObject>();
//		File[] files = folder.listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File file) {
//				sourcefiles.add(new MexedObject(file.getAbsolutePath()));
//				return false;
//			}
//		});
//		
//		C.pl("[before] " + files.length);
//		C.pl(DateUtil.displayNow());
//		
//		Master<MexedObject> george = new Master<MexedObject>(sourcefiles, new NormalFileDestroyer()) {
//			protected int countOfThread() {
//				return 1000;
//			}
//		};
//		george.sitAndWait();
//		C.pl(DateUtil.displayNow());
//	}
//	
//	private void readCache() {
//		long start = System.currentTimeMillis();
//		String filePath = "E:/MasPro/wikis";
//		filePath = "E:/wiki";
//		C.pl("Reading cache " + filePath);
//		File folder = FileUtil.getIfNormalFolder(filePath);
//		String[] files = folder.list();
//		C.pl(files.length);
//		for(int i = 0; i < files.length; i++) {
//			String fileName = files[i];
//			GRAND_SET.add(HOMEPAGE + fileName.replace(Konstants.SUFFIX_TXT, ""));
//		}
//		long end = System.currentTimeMillis();
//		C.time2(start, end);
//	}
//	
//	private void scan(String url, int depth) {
//		List<String> links = parseLinksInPage(url);
//		if(depth >= maxDepth) {
//			save(url, links);
//			return;
//		}
//		
//		for(String subUrl:links) {
//			scan(subUrl, depth + 1);
//		}
//	}
//	
//	private void save(String url, List<String> links) {
//		String name = url.substring(url.lastIndexOf("/") + 1);
//		String fullFileName = "E:/MasPro/exp/wiki_britain/" + name + "_"+ DateUtil.timestamp() + ".txt";
//		IOUtil.saveAsTxt(links, fullFileName);
//		C.list(links);
//		
//		C.pl("Saved " + fullFileName);
//	}
//	
//	private List<String> parseLinksInPage(final String url) {
//		List<String> keyWords = StringUtil.splitIntoList("Main_Page");
//		List<String> links = new ArrayList<String>();
//		Extractor<MexItem> frank = new Extractor<MexItem>() {
//			
//			@Override
//			public String getUrl() {
//				printFetching = true;
//				return url;
//			}
//			
//			@Override
//			protected void parseContent() {
//				String regex = "href=\"/wiki/([^:<>=\"]+?)\" title=\"([^<>=\"]+?)\"";
//				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
//				while(m.find()) {
//					String name = m.group(1);
//					if(StringUtil.contains(name, keyWords)) {
//						continue;
//					}
//					String link = HOMEPAGE + m.group(1);
//					String title = m.group(2);
//					if(StringUtil.contains(title, "page does not exist")) {
//						continue;
//					}
//					
//					boolean isExist = GRAND_SET.contains(link);
//					if(isExist) {
//						continue;
//					}
//					C.pl(link);
//					GRAND_SET.add(link);
//					links.add(link);
//				}
//			}
//		};
//		
//		frank.process();
//		
//		return links;
//	}
//}
