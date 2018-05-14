package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.media.MediaFileAnalyzer;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.thirdparty.msoffice.MsExcelHelper;
import com.sirap.basic.thirdparty.msoffice.MsWordHelper;
import com.sirap.basic.thirdparty.pdf.PdfHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.MexFactory;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MiscUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.MemoryRecord;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.manager.MemorableDayManager;

public class CommandFile extends CommandBase {

	private static final String KEY_PRINT_TXT_ONELINE = "&";
	private static final String KEY_PRINT_TXT = "#";
	private static final String KEY_PDF = "pdf";
	private static final String KEY_DAY_CHECK = "dc";
	private static final String KEY_MEMORABLE = "mm";
	private static final String KEY_KICK_OFF = "ko";
	private static final String KEY_HEX = "hex";
	private static final String KEY_XEH = "xeh";
	
	public static final String DEFAULT_TEXT_MAX_SIZE = "2M";

	{
		helpMeanings.put("text.max", DEFAULT_TEXT_MAX_SIZE);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean handle() {
		
		// deals with normal file, open it or list out
		File file = parseFile(command);
		if(file != null) {
			String filePath = file.getAbsolutePath();
			
			boolean toOpen = false;
			if(OptionUtil.readBooleanPRI(options, "O", false)) {
				toOpen = true;
			} else if(OptionUtil.readBooleanPRI(options, FileOpener.KEY_TXT_NOTEPAD, false)) {
				toOpen = true;
			} else if(OptionUtil.readBooleanPRI(options, FileOpener.KEY_TXT_ULTRA_EDITOR, false)) {
				toOpen = true;
			}
			
			if(toOpen) {
				FileOpener.open(filePath, options);
				return true;
			}
			
			if(target.isFileRelated()) {
				export(file);
				return true;
			}
			
			if(FileOpener.isZipFile(filePath)) {
				List<MexZipEntry> items = ArisUtil.parseZipEntries(filePath);
				export(items);
				return true;
			}
			
			if(StrUtil.endsWith(filePath, Konstants.DOT_CLASS)) {
				Class glass = IOUtil.loadClassFile(filePath);
				export(ArisUtil.getClassDetail(glass, filePath, isDebug()));
				return true;
			}
			
			if(FileOpener.isTextFile(filePath)) {
				String cat = IOUtil.charsetOfTextFile(file.getAbsolutePath());
				if(OptionUtil.readBooleanPRI(options, "x", false)) {
					cat = switchChartset(cat);
				}
				
				List<String> records = FileOpener.readTextContent(filePath, true, cat);
				if(OptionUtil.readBooleanPRI(options, "one", false)) {
					String temp = IOUtil.readString(filePath, cat, "");
					String result = StrUtil.reduceMultipleSpacesToOne(temp);
					export(result);
				} else {
					boolean sensitive = isCaseSensitive();
					if(OptionUtil.readBooleanPRI(options, "sort", false)) {
						CollUtil.sort(records, sensitive);
					}

					if(OptionUtil.readBooleanPRI(options, "mark", false)) {
						records = CollUtil.sortAndMarkOccurrence(records, sensitive);
					}

					if(OptionUtil.readBooleanPRI(options, "uniq", false)) {
						if(sensitive) {
							Set<String> taiwan = new LinkedHashSet<>(records);
							records = new ArrayList<>(taiwan);
						} else {
							Set<String> taiwan = new HashSet<>();
							List<String> uniq = new ArrayList<>();
							for(String origin : records) {
								String item = origin.toLowerCase();
								if(taiwan.contains(item)) {
									continue;
								}
								taiwan.add(item);
								uniq.add(origin);
							}
							records = new ArrayList<>(uniq);
						}
					}

					if(OptionUtil.readBooleanPRI(options, "line", false)) {
						records = CollUtil.lineNumber(records, true);
					}

					export(records);	
				}
				
				return true;
			}
			
			FileOpener.open(filePath, options);
			
			return true;
		}
		
		//deals with single file, show detail, print if text, show in one line
		params = parseParams(StrUtil.occupy("({0}|{1}|{2})(.+?)", KEY_SHOW_DETAIL, KEY_PRINT_TXT, KEY_PRINT_TXT_ONELINE));
		if(params != null) {
			file = parseFile(params[1]);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				String type = params[0];
				if(StrUtil.equals(KEY_SHOW_DETAIL, type)) {
					List<String> items = FileUtil.detail(filePath);
					if(!StrUtil.equals(params[1], filePath)) {
						items.add(0, filePath);
					}
					if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_PDF)) {
						int lines = PdfHelper.pagesOf(filePath);
						items.add("pages: " + lines);
					} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
						List<String> sheets = MsExcelHelper.readSheetNames(filePath);
						String msg = StrUtil.occupy("sheets({0}): {1}", sheets.size(), StrUtil.connect(sheets, ", "));
						items.add(msg);
					} else if(FileUtil.isAnyTypeOf(filePath, "docx")) {
						int lines = MsWordHelper.pagesOf(filePath);
						items.add("pages: " + lines);
					} else if(FileOpener.isTextFile(filePath)) {
						int[] count = IOUtil.countOfLinesChars(filePath);
						items.add("lines: " + count[0]);
						items.add("chars: " + count[1]);
						items.add("coding: " + IOUtil.charsetOfTextFile(filePath));
					} else if(FileOpener.isImageFile(filePath)) {
						items.add("area: " + ImageUtil.readImageWidthHeight(filePath, " x "));
						String format = ImageUtil.getRealFormat(filePath);
						if(!StrUtil.endsWith(filePath, format)) {
							items.add("format: " + format);
						}
						
					} else {
						MediaFileAnalyzer mario = MexFactory.getMediaFileAnalyzer(filePath);
						if(mario != null) {
							List<String> detail = mario.getDetail();
							items.addAll(detail);
						}
					}
					
					export(items);

					return true;
				}
				
				checkTooBigToHandle(file, g().getUserValueOf("text.max", DEFAULT_TEXT_MAX_SIZE));
				String cat = IOUtil.charsetOfTextFile(file.getAbsolutePath());
				if(OptionUtil.readBooleanPRI(options, "x", false)) {
					cat = switchChartset(cat);
				}
				if(StrUtil.equals(KEY_PRINT_TXT_ONELINE, type)) {
					String temp = IOUtil.readString(filePath, cat, "");
					String result = StrUtil.reduceMultipleSpacesToOne(temp);
					export(result);
				} else if(StrUtil.equals(KEY_PRINT_TXT, type)) {
					List<String> records = FileOpener.readTextContent(filePath, true, cat);
					if(OptionUtil.readBooleanPRI(options, "line", false)) {
						export(CollUtil.lineNumber(records, true));
					} else {
						export(records);
					}
				}
				
				return true;
			}
		}
		
		//list regex matched items in given website
		params = parseParams("(.+)@" + KEY_HTTP_WWW);
		if(params != null) {
			String regex = params[0].trim();
			String pageUrl = equiHttpProtoclIfNeeded(params[1].trim());
			String source = IOUtil.readString(pageUrl, g().getCharsetInUse());
			if(source != null) {
				boolean showOrder = OptionUtil.readBooleanPRI(options, "order", false);
				String itemConnector = OptionUtil.readString(options, "icon", ", ").replace("\\s", " ");
				List<List<String>> allItems = StrUtil.findAllMatchedListedItems(regex, source, isCaseSensitive());
				List tempList = new ArrayList();
				int count = 0;
				for(List<String> list : allItems) {
					count++;
					String order = showOrder ? StrUtil.occupy("#{0} ", count) : "";
					tempList.add(order + StrUtil.connect(list, itemConnector));
				}
				
				export(tempList);
				
				return true;
			}
		}
		
		//list regex matched items in local text file
		params = parseParams("(.+)@(.+)");
		if(params != null) {
			String regex = params[0].trim();
			String value = params[1].trim();
			File tempFile = parseFile(value);
			if(tempFile != null) {
				String filePath = tempFile.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					boolean showLineNumber = OptionUtil.readBooleanPRI(options, "line", false);
					String groupConnector = OptionUtil.readString(options, "gcon", "; ").replace("\\s", " ");
					String itemConnector = OptionUtil.readString(options, "icon", ", ").replace("\\s", " ");
					List<String> items = new ArrayList<String>();
					String cat = IOUtil.charsetOfTextFile(filePath);
					if(OptionUtil.readBooleanPRI(options, "x", false)) {
						cat = switchChartset(cat);
					}

					List<String> txtContent = FileOpener.readTextContent(filePath, true, cat);
					int line = 0;
					int maxLen = (txtContent.size() + "").length();
					for(String record : txtContent) {
						line++;
						List<List<String>> allLists = StrUtil.findAllMatchedListedItems(regex, record, isCaseSensitive());

						if(EmptyUtil.isNullOrEmpty(allLists)) {
							continue;
						}
						
						if(OptionUtil.readBooleanPRI(options, "one", false)) {
							for(List<String> list : allLists) {
								items.addAll(list);
							}
						} else if (OptionUtil.readBooleanPRI(options, "group", false)) {
							for(List<String> list : allLists) {
								items.add(StrUtil.connect(list, itemConnector));
							}
						} else {
							StringBuffer sb = StrUtil.sb();
							boolean toConnect = false;
							for(List<String> list : allLists) {
								if(toConnect) {
									sb.append(groupConnector);
								}
								toConnect = true;
								sb.append(StrUtil.connect(list, itemConnector));
							}
							String lineNumber = showLineNumber ? StrUtil.occupy("L{0} ", StrUtil.padRight(line + "", maxLen)) : "";
							items.add(lineNumber + sb.toString());
						}
					}
					
					export(items);
					return true;
				}
			}
		}
		
		params = parseParams(KEY_PDF + "([\\d,\\-]+)\\s+(.+\\.pdf)");
		if(params != null) {
			String pageInfo = params[0];
			
			String nameSuffix = "";
			File pdfFile = parseFile(params[1]);
			if(pdfFile != null) {
				String pdfFilepath = pdfFile.getAbsolutePath();
				String shortFilename = FileUtil.extractFilenameWithoutExtension(pdfFilepath);
				nameSuffix = shortFilename + "_" + pageInfo.replace(',', '_');
				
				String dir = getExportLocation();
				String newPdfFilepath = dir + nameSuffix + Konstants.DOT_PDF;
				if(SimpleKonfig.g().isExportWithTimestampEnabled(options)) {
					newPdfFilepath = dir + DateUtil.timestamp() + "_" + nameSuffix + Konstants.DOT_PDF;
				}

				PdfHelper.selectPages(pageInfo, pdfFilepath, newPdfFilepath);
				C.pl2("Exported => " + newPdfFilepath);
				if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
					FileOpener.open(newPdfFilepath);
				}
			}
			
			return true;
		}
		
		solo = parseParam(KEY_PDF + "\\s+(.+?\\.pdf)");
		if(solo != null) {
			List<String> items = StrUtil.split(solo);
			List<String> pdfFiles = new ArrayList<String>();
			String nameSuffix = "merge";
			for(String item : items) {
				if(!StrUtil.endsWith(item, Konstants.DOT_PDF)) {
					continue;
				}
				
				File pdfFile = parseFile(item);
				if(pdfFile != null) {
					String pdfFilepath = pdfFile.getAbsolutePath();
					String shortFilename = FileUtil.extractFilenameWithoutExtension(pdfFilepath);
					nameSuffix += "_" + shortFilename;
					pdfFiles.add(pdfFilepath);
				}
			}
			
			if(pdfFiles.size() > 1) {
				String dir = getExportLocation();
				String newPdfFilepath = dir + nameSuffix + Konstants.DOT_PDF;
				if(SimpleKonfig.g().isExportWithTimestampEnabled(options)) {
					newPdfFilepath = dir + DateUtil.timestamp() + "_" + nameSuffix + Konstants.DOT_PDF;
				}
				
				PdfHelper.merge(pdfFiles, newPdfFilepath);
				C.pl2("Merged => " + newPdfFilepath);
				if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
					FileOpener.open(newPdfFilepath);
				}
			}
			
			return true;
		}
		
		if(is(KEY_DAY_CHECK)) {
			MemoryKeeper lucy = new MemoryKeeper() {
				@Override
				public List<MemoryRecord> readRecords() {
					return MemorableDayManager.g(filePath).searchWithNDays(30);
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
		}
		
		params = parseParams(KEY_MEMORABLE + "(|\\d{1,4})");
		if(params != null) {
			final Integer count = MathUtil.toInteger(params[0], 20);
			MemoryKeeper lucy = new MemoryKeeper() {
				@Override
				public List<MemoryRecord> readRecords() {
					List<MemoryRecord> records = MemorableDayManager.g(filePath).getMemoryRecords(count);
					return records;
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
			
			return true;
		}
		
		if(is(KEY_MEMORABLE + KEY_2DOTS)) {
			MemoryKeeper lucy = new MemoryKeeper() {
				@Override
				public List<MemoryRecord> readRecords() {
					List<MemoryRecord> records = MemorableDayManager.g(filePath).getAllRecords();
					return records;
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
		}
		
		solo = parseParam(KEY_KICK_OFF + "(|\\s+.+)");
		if(solo != null) {
			List<String> lines = Lists.newArrayList();
			lines.add("#" + DateUtil.displayNow(DateUtil.HOUR_Min_Sec_AM_WEEK_DATE));
			if(!EmptyUtil.isNullOrEmpty(solo)) {
				lines.add(solo);
			}
			
			String location = "KO";
			String temp = g().getUserValueOf("ko.location");
			if(!EmptyUtil.isNullOrEmpty(temp)) {
				File folder = FileUtil.getIfNormalFolder(temp);
				if(folder != null) {
					location = StrUtil.useSeparator(folder.getAbsolutePath(), location);
				} else {
					XXXUtil.info("Non-existing location: {0}, use default location: {1}", temp, location);
				}
			}
			String fakeInput = "fake $+ts>" + location;
	    	InputAnalyzer fara = new InputAnalyzer(fakeInput);
	    	options = fara.getOptions();
	    	target = fara.getTarget();

			export(lines);
			
			return true;
		}
		

		
		solo = parseParam("rename (.+?)");
		if(solo != null) {
			List<String> items = StrUtil.split(solo);
			boolean isGood = isValidFolderAndTextFile(items);
			if(!isGood) {
				items = StrUtil.split(solo, " ");
				isGood = isValidFolderAndTextFile(items);
			}
			
			if(isGood) {
				List<String> tasks = Lists.newArrayList();
				List<String> epis = IOUtil.readLines(items.get(1));
				File folder = new File(items.get(0));
				File[] files = folder.listFiles();
				if(files != null) {
					for(File fileItem : files) {
						String originShortName = fileItem.getName();
						String epi = MiscUtil.seasonAndEpisode(originShortName);
						if(epi == null) {
							C.pl("Not found sAAeBB for: " + originShortName);
							continue;
						}
						String mexCriteria = folder.getName() + "." + epi;
						Object title = CollUtil.findFirst(epis, mexCriteria, false);
						if(title == null) {
							C.pl("Not found niceName for: " + mexCriteria);
							continue;
						}
						String extension = StrUtil.findFirstMatchedItem("(\\.[^\\.]+$)", originShortName);
						String nicename = title + "" + extension;
						boolean same = StrUtil.equals(nicename, originShortName);
						if(same) {
							C.pl("Already niceName: " + originShortName);
							continue;
						}
						String cmd = StrUtil.occupy("c.rename \"{0}\" \"{1}\"", fileItem.getAbsolutePath(), nicename);
						tasks.add(cmd);
					}
				}
				
				export(tasks);
			}
			
			return true;
		}
		
		solo = parseParam(KEY_HEX + "\\s+(.+)");
		if(solo != null) {
			File ball = parseFile(solo);
			List<String> items = null;
			boolean toHex = OptionUtil.readBooleanPRI(options, "h", true);
			if(ball != null) {
				items = XCodeUtil.bytesOfFile(ball.getAbsolutePath(), toHex);
			} else {
				items = XCodeUtil.bytesOfString(solo, charset(), toHex);
			}
			
			boolean toSplit = OptionUtil.readBooleanPRI(options, "k", true);
			if(toSplit) {
				int kPerLine = OptionUtil.readIntegerPRI(options, "k", 16);
				String finalOptions = OptionUtil.mergeOptions(options, "c=#s");
				export(XCodeUtil.group(items, kPerLine), finalOptions);
			} else {
				export(items);
			}
			
			return true;
		}
		
		solo = parseParam(KEY_XEH + "\\s+(.+)");
		if(solo != null) {
			String temp = XCodeUtil.bytesToString(solo, charset());
			export(temp);
			
			return true;
		}
		
		return false;
	}
	
	public boolean isValidFolderAndTextFile(List<String> items) {
		if(items.size() != 2) {
			return false;
		}
		
		if(FileUtil.getIfNormalFolder(items.get(0)) == null) {
			return false;
		}
		
		if(FileUtil.getIfNormalFile(items.get(1)) == null) {
			return false;
		}
		
		return true;
	}
	
	abstract class MemoryKeeper {
		protected String filePath;
		
		public MemoryKeeper() {
			init();
		}
		
		private void init() {
			File file = parseFile(g().getUserValueOf("file.memory"));
			if(file == null) {
				throw new MexException("Memory file missing, pleace check user config [file.memory].");
			}
			
			filePath = file.getAbsolutePath();
		}
		
		protected boolean handle() {
			if(filePath == null) {
				return false;
			}
			
			List<MemoryRecord> records = readRecords();
			export(CollUtil.items2PrintRecords(records));
			
			return true;
		}
		
		public abstract List<MemoryRecord> readRecords();
	}
}

class SearchComponent {
	private List<String> paths;
	private String criteria;
	private boolean showDetail;
	
	public SearchComponent(List<String> paths, String criteria) {
		this.paths = paths;
		this.criteria = criteria;
	}
	
	public List<String> getPaths() {
		return paths;
	}
	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public boolean isShowDetail() {
		return showDetail;
	}

	public void setShowDetail(boolean showDetail) {
		this.showDetail = showDetail;
	}
	
}
