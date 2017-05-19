package com.sirap.common.command.explicit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.media.MediaFileAnalyzer;
import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.thirdparty.pdf.PdfHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.FileDeeper;
import com.sirap.basic.tool.MexFactory;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.MemoryRecord;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.manager.FileManager;
import com.sirap.common.manager.MemorableDayManager;

public class CommandFile extends CommandBase {

	private static final String KEY_EXTENSIBLE = "x";
	private static final String KEY_VERY_IMPORTANT_FOLDER = "v";
	private static final String KEY_OPEN_EXPLORER = "<";
	private static final String KEY_ALL_DISKS_SINGLE_COLON = ":";
	private static final String KEY_ALL_DISKS_DOUBLE_COLON = "::";
	private static final String KEY_PRINT_TXT = "txt,cat,=";
	private static final String KEY_ONELINE_TXT = "one";
	private static final String KEY_SHOW_DETAIL = "-";
	private static final String KEY_DIVE_DEEP = "#";
	private static final String KEY_PDF = "pdf";
	private static final String KEY_DAY_CHECK = "dc";
	private static final String KEY_MEMORABLE = "mm";
	
	@Override
	public boolean handle() {
		singleParam = parseParam(KEY_EXTENSIBLE + "\\s(.*?)");
		if(singleParam != null) {
			if(target instanceof TargetConsole) {
				if(isEmailEnabled()) {
					target = TargetAnalyzer.createTargetEmail(EmailCenter.DEF_RECEIVER, command);
				} else {
					C.pl2("Email currently disabled.");
					return true;
				}
			}
			
			final List<Object> objs = new ArrayList<Object>();
			List<String> items = StrUtil.split(singleParam, ';');
			for(String item:items) {
				if(EmptyUtil.isNullOrEmptyOrBlank(item)) {
					continue;
				}
				
				item = item.trim();
				
				String[] fileParams = StrUtil.parseParams("(\\${1,2}|)(.+?)", item);
				if(fileParams == null) {
					continue;
				}
				
				String type = fileParams[0];
				if(type.isEmpty()) {
					objs.add(item);
					continue;
				}
				
				String value = fileParams[1];
				File file = parseFile(value);
				if(file != null) {
					String filePath = file.getAbsolutePath();
					if(FileOpener.isTextFile(filePath)) {
						List<String> txtContent = readRecordsFromFile(filePath);
						if(type.length() == 1) {
							objs.add(file);
						} else if(type.length() == 2) {
							for(String line:txtContent) {
								File lineFile = parseFile(line.trim());
								if(lineFile != null) {
									objs.add(lineFile);
								} else {
									objs.add(line);
								}
							}
						}
					} else {
						objs.add(file);
					}
					continue;
				}
				
				File folder = parseFolder(value);
				if(folder != null) {
					folder.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							File subFile = FileUtil.getIfNormalFile(dir + File.separator + name);
							if(subFile != null) {
								objs.add(subFile);
							}
							return false;
						}
					});
					continue;
				}
				
				objs.add(item);
			}
			
			export(objs);
			
			return true;
		}
		
		singleParam = command;
		File file = parseFile(singleParam);
		if(file != null) {
			String filePath = file.getAbsolutePath();
			
			if(target instanceof TargetConsole) {
				FileOpener.open(filePath);
				return true;
			}
			
			if(!target.isFileRelated() && FileOpener.isTextFile(filePath)) {
				export(readRecordsFromFile(filePath));
			} else {
				export(file);
			}

			return true;
		}
				
		params = parseParams("(" + KEY_SHOW_DETAIL + "|" + KEY_DIVE_DEEP + "|)([^<]*)(<|)");
		if(params != null) {
			String does = params[0];
			String path = params[1];
			String type = params[2];
			if(KEY_OPEN_EXPLORER.equals(type)) {
				String targetPath = null;
				if(EmptyUtil.isNullOrEmpty(path)) {
					targetPath = storage();
				} else {
					targetPath = parseFolderPath(path);
				}
				
				if(targetPath != null) {
					String temp = targetPath.replaceFirst("\\\\$", "");
					if(PanaceaBox.isMac()) {
						PanaceaBox.openFile(temp);
						C.pl2("Open Mac Finder at [" + temp + "].");
					} else {
						PanaceaBox.execute("explorer " + temp);
						C.pl2("Open Windows resource manager at [" + temp + "].");
					}
					
					return true;
				}
			}
			
			if(StrUtil.equals(KEY_DIVE_DEEP, does)) {
				String cleverPath = parseFolderPath(path);
				if(FileUtil.getIfNormalFolder(cleverPath) != null) {
					FileDeeper dima = new FileDeeper(cleverPath);
					
					int maxLevel = dima.howDeep();
					List<File> files = dima.getMaxLevelFiles();
					
					if(target.isFileRelated()) {
						export(files);
					} else {
						List<String> items = new ArrayList<>();
						for(File what: files) {
							items.add("#" + maxLevel + " " + what.getAbsolutePath());
						}
						
						export(items);
					}
				}
				
				return true;
			}
			
			List<String> paths = new ArrayList<>();
			if(path.startsWith(KEY_ALL_DISKS_DOUBLE_COLON)) {
				List<String> allDisks = FileUtil.availableDiskNames();
				
				for(String disk : allDisks) {
					String newSourceName = path.replaceFirst("::", disk);
					String cleverPath = parseFolderPath(newSourceName);
					if(cleverPath != null) {
						paths.add(cleverPath);
					}
				}
			} else {
				String cleverPath = parseFolderPath(path);
				if(cleverPath != null && FileUtil.getIfNormalFolder(cleverPath) != null) {
					paths.add(cleverPath);
				}
			}
			
			if(!EmptyUtil.isNullOrEmpty(paths)) {
				List<String> allRecords = new ArrayList<>();
				for(String pathItem: paths) {
					List<String> records = listDirectory(pathItem);
					if(!EmptyUtil.isNullOrEmpty(records)) {
						allRecords.addAll(records);
					}
				}
				
				if(!EmptyUtil.isNullOrEmpty(allRecords)) {
					if(target.isFileRelated()) {
						export(CollectionUtil.toFileList(allRecords));
					} else {
						if(StrUtil.equals(KEY_SHOW_DETAIL, does)) {
							List<String> items = new ArrayList<>();
							for(String record : allRecords) {
								String temp = detailFileInfo(record);
								items.add(temp);
							}
							export(items);
						} else {
							export(allRecords);
						}
					}
					
					return true;
				}
			}
		}
		
		if(is(KEY_ALL_DISKS_SINGLE_COLON)) {
			List<String> records = FileUtil.availableDiskDetails();
			export(records);
			
			return true;
		}
		
		InputAnalyzer sean = new FileSizeInputAnalyzer(input);
		this.command = sean.getCommand();
		this.target = sean.getTarget();
		SearchComponent jack = parseFolderPathAndCriterias(command);
		if(jack != null) {
			List<MexedFile> allMexedFiles = new ArrayList<>();
			List<String> pathList = jack.getPaths();
			for(String path : pathList) {
				String criterias = jack.getCriteria().trim();
				
				int idxOfSeparator = criterias.indexOf(' ');
				String nameCriteria = null;
				Integer depth = 0;
				
				String regexDepth = "\\d{1,2}";
				if(idxOfSeparator < 0) {
					if(StrUtil.isRegexMatched(regexDepth, criterias)) {
						depth = MathUtil.toInteger(criterias);
					} else {
						nameCriteria = criterias;
					}
				} else {
					String depthString = criterias.substring(0, idxOfSeparator);
					if(StrUtil.isRegexMatched(regexDepth, depthString)) {
						depth = MathUtil.toInteger(depthString);
						nameCriteria = criterias.substring(idxOfSeparator + 1).trim();
					} else {
						nameCriteria = criterias;
					}
				}
				
				if(depth > 0) {
					depth--;
				}
				
				List<MexedFile> items = scanMexedFiles(path, depth, nameCriteria);
				if(!EmptyUtil.isNullOrEmpty(items)) {
					allMexedFiles.addAll(items);
				}
			}
			
			if(EmptyUtil.isNullOrEmpty(allMexedFiles)) {
				exportEmptyMsg();
			} else {
				if(target.isFileRelated()) {
					List<File> files = new ArrayList<File>();
					for(MexedFile mf:allMexedFiles) {
						File fileItem = mf.getFile();
						if(fileItem.isFile()) {
							files.add(fileItem);
						}
					}
					
					export(files);
				} else {
					List<String> allRecords = CollectionUtil.items2PrintRecords(allMexedFiles);
					Collections.sort(allRecords);
					
					if(jack.isShowDetail()) {
						List<String> items = new ArrayList<>();
						for(String record : allRecords) {
							String temp = detailFileInfo(record);
							items.add(temp);
						}
						export(items);
					} else {
						export(allRecords);
					}
				}
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER)) {
			List<String> records = FileManager.g().getAllFolders();
			export(records);
			
			return true;
		}
		
		sean = new FileSizeInputAnalyzer(input);
		this.command = sean.getCommand();
		this.target = sean.getTarget();
		String vRegex = "(" + KEY_SHOW_DETAIL + "|)" + KEY_VERY_IMPORTANT_FOLDER + "(\\s(.*?)|)";
		params = parseParams(vRegex);
		if(params != null) {
			boolean detail = !params[0].isEmpty();
			String criteria = params[1].trim();
			List<MexedFile> records = FileManager.g().getFileRecordsByName(criteria);
			if(target.isFileRelated()) {
				export(CollectionUtil.toFileList(records));
			} else {
				if(detail) {
					List<String> items = new ArrayList<>();
					for(MexedFile mFile : records) {
						String record = mFile.toString();
						String temp = detailFileInfo(record);
						items.add(temp);
					}
					export(items);
				} else {
					export(records);
				}
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_2DOTS)) {
			List<MexedFile> records = FileManager.g().getAllFileRecords();
			if(target.isFileRelated()) {
				export(CollectionUtil.toFileList(records));
			} else {
				export(CollectionUtil.items2PrintRecords(records));
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_REFRESH)) {
			int[] size = FileManager.g().refresh();
			C.pl2("Refreshed, " + size[1] + " records, before " + size[0] + ".");
			
			return true;
		}
		
		params = parseParams("(.+)@" + KEY_HTTP_WWW);
		if(params != null) {
			String regex = params[0].trim();
			String pageUrl = equiHttpProtoclIfNeeded(params[1].trim());
			String source = IOUtil.readURL(pageUrl, g().getCharsetInUse(), true);
			if(source != null) {
				List<List<String>> items = StrUtil.findAllMatchedListedItems(regex, source);
				export(items);
				
				return true;
			}
		}
		
		params = parseParams("(.+)@(.+)");
		if(params != null) {
			String regex = params[0].trim();
			String value = params[1].trim();
			File tempFile = parseFile(value);
			if(tempFile != null) {
				String filePath = tempFile.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					List<String> items = new ArrayList<String>();
					List<String> txtContent = readRecordsFromFile(filePath);
					int line = 0;
					for(String record:txtContent) {
						line++;
						List<List<String>> allItems = StrUtil.findAllMatchedListedItems(regex, record);

						if(EmptyUtil.isNullOrEmpty(allItems)) {
							continue;
						}
						
						items.add(line + ") "+ StrUtil.connect(allItems, ", "));
					}
					
					export(items);
					return true;
				}
			}
		}
		
		params = parseParams("(-|#|one |txt |cat |=)(.+?)");
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
						int lines = PdfHelper.howManyPages(filePath);
						items.add("pages: " + lines);
					} else if(FileOpener.isTextFile(filePath)) {
						int lines = IOUtil.totalLines(filePath);
						items.add("lines: " + lines);
					} else if(FileOpener.isImageFile(filePath)) {
						int[] widthHeight = ImageUtil.readImageWidthHeight(filePath);
						items.add("resolution: " + widthHeight[0] + "x" + widthHeight[1]);
						
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
				
				if(tooBigToHanlde(file, "2M")) {
					return true;
				}
				
				if(StrUtil.equals(KEY_ONELINE_TXT, type)) {
					String temp = IOUtil.readFileWithLineSeparator(filePath, " ");
					String result = StrUtil.reduceMultipleSpacesToOne(temp);
					export(result);
				} else if(StrUtil.existsIgnoreCase(KEY_PRINT_TXT.split(","), type)) {
					List<String> records = FileOpener.readTextContent(filePath, true);
					export(records);
				}
				
				return true;
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
				String newPdfFilepath = dir + nameSuffix + Konstants.SUFFIX_PDF;
				if(SimpleKonfig.g().isExportWithTimestampEnabled()) {
					newPdfFilepath = dir + DateUtil.timestamp() + "_" + nameSuffix + Konstants.SUFFIX_PDF;
				}

				PdfHelper.selectPages(pageInfo, pdfFilepath, newPdfFilepath);
				C.pl2("Exported => " + newPdfFilepath);
				if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
					FileOpener.open(newPdfFilepath);
				}
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_PDF + "\\s+(.+?\\.pdf)");
		if(singleParam != null) {
			List<String> items = StrUtil.split(singleParam);
			List<String> pdfFiles = new ArrayList<String>();
			String nameSuffix = "merge";
			for(String item : items) {
				if(!StrUtil.endsWith(item, Konstants.SUFFIX_PDF)) {
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
				String newPdfFilepath = dir + nameSuffix + Konstants.SUFFIX_PDF;
				if(SimpleKonfig.g().isExportWithTimestampEnabled()) {
					newPdfFilepath = dir + DateUtil.timestamp() + "_" + nameSuffix + Konstants.SUFFIX_PDF;
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
		
		return false;
	}
	
	private String detailFileInfo(String filepath) {
		File file = parseFile(filepath);
		if(file != null) {
			String size = FileUtil.formatFileSize(file.length());
			return filepath + " " + size;
		} else {
			return filepath;
		}
	}
	
	private SearchComponent parseFolderPathAndCriterias(String input) {
		String[] expArr = {"(-|)(.*?)\\s(.+)", "(-|)(.*?),\\s?(.+)"};
		List<String> allDisks = null;
		List<String> paths = new ArrayList<>();
		for(int i = 0; i < expArr.length; i++) {
			String[] params = parseParams(expArr[i]);
			if(params != null) {
				boolean detail = !params[0].isEmpty();
				String sourceName = params[1];
				String criteria = params[2];
				if(sourceName.startsWith(KEY_ALL_DISKS_DOUBLE_COLON)) {
					if(allDisks == null) {
						allDisks = FileUtil.availableDiskNames();
					}

					for(String disk : allDisks) {
						String newSourceName = sourceName.replaceFirst("::", disk);
						String cleverPath = parseFolderPath(newSourceName);
						if(cleverPath != null) {
							paths.add(cleverPath);
						}
					}
				} else {
					String cleverPath = parseFolderPath(sourceName);
					if(cleverPath != null && FileUtil.getIfNormalFolder(cleverPath) != null) {
						paths.add(cleverPath);
					}
				}
				
				if(!EmptyUtil.isNullOrEmpty(paths)) {
					SearchComponent jack = new SearchComponent(paths, criteria);
					jack.setShowDetail(detail);
					return jack;
				}
			}
		}
		
		return null;
	}
	
	private List<MexedFile> scanMexedFiles(String path, int depth, String criteria) {
		List<File> allFiles = FileUtil.scanFolder(path, depth);
		
		List<MexedFile> allItems = new ArrayList<MexedFile>();
		for(File file:allFiles) {
			allItems.add(new MexedFile(file));
		}
		
		List<MexedFile> items = null;
		if(EmptyUtil.isNullOrEmpty(criteria)) {
			items = allItems;
		} else {
			MexFilter<MexedFile> filter = new MexFilter<MexedFile>(criteria, allItems);
			items = filter.process();
		}
		
		return items;
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
			export(CollectionUtil.items2PrintRecords(records));
			
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
