package com.sirap.common.command.explicit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.comparator.MexFileComparator;
import com.sirap.basic.component.media.MediaFileAnalyzer;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.search.FileSizeCriteria;
import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.thirdparty.msoffice.MsExcelHelper;
import com.sirap.basic.thirdparty.msoffice.MsWordHelper;
import com.sirap.basic.thirdparty.pdf.PdfHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.FileDeeper;
import com.sirap.basic.tool.MexFactory;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.MemoryRecord;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetEmail;
import com.sirap.common.framework.command.target.TargetFolder;
import com.sirap.common.manager.MemorableDayManager;
import com.sirap.common.manager.VFileManager;

public class CommandFile extends CommandBase {

	private static final String KEY_MIX = "x";
	private static final String KEY_VERY_IMPORTANT_FOLDER = "v";
	private static final String KEY_OPEN_EXPLORER = "<";
	private static final String KEY_ALL_DISKS_SINGLE_COLON = ":";
	private static final String KEY_ALL_DISKS_DOUBLE_COLON = "::";
	private static final String KEY_PRINT_TXT_ONELINE = "&";
	private static final String KEY_PRINT_TXT = "#";
	private static final String KEY_SHOW_DETAIL = "-";
	private static final String KEY_FOLDER_DEPTH = "#";
	private static final String KEY_PDF = "pdf";
	private static final String KEY_DAY_CHECK = "dc";
	private static final String KEY_MEMORABLE = "mm";
	private static final String KEY_FIX_IMAGE = "fix";
	private static final String KEY_KICK_OFF = "ko";
	private static final String KEY_KICK_PRINT = "kp";
	private static final String KEY_FILE_REMOVE = "remove";
	
	public static final String DEFAULT_TEXT_MAX_SIZE = "2M";

	{
		helpMeanings.put("text.max", DEFAULT_TEXT_MAX_SIZE);
	}
	
	@SuppressWarnings("all")
	@Override
	public boolean handle() {
		solo = parseSoloParam(KEY_MIX + "\\s(.*?)");
		if(solo != null) {
			if(target instanceof TargetConsole) {
				if(isEmailEnabled()) {
					target = TargetAnalyzer.createTargetEmail(EmailCenter.DEF_RECEIVER, command);
				} else {
					C.pl2("Email currently disabled.");
					return true;
				}
			}
			
			final List<Object> objs = new ArrayList<Object>();
			List<String> items = StrUtil.split(solo, ';');
			for(String item:items) {
				if(EmptyUtil.isNullOrEmptyOrBlank(item)) {
					continue;
				}
				
				item = item.trim();
				
				String[] fileParams = StrUtil.parseParams("(#{1,2}|)(.+?)", item);
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
						String cat = IOUtil.charsetOfTextFile(filePath);
						if(OptionUtil.readBooleanPRI(options, "x", false)) {
							cat = switchChartset(cat);
						}
						List<String> txtContent = FileOpener.readTextContent(filePath, true, cat);
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
		
		solo = command;
		File file = parseFile(solo);
		if(file != null) {
			String filePath = file.getAbsolutePath();
			
			if(!target.isFileRelated() && FileOpener.isZipFile(filePath)) {
				List<MexZipEntry> items = ArisUtil.parseZipEntries(filePath);
				export(items);
				return true;
			}
			
			if(!target.isFileRelated() && StrUtil.endsWith(filePath, Konstants.DOT_CLASS)) {
				Class glass = IOUtil.loadClassFile(filePath);
				export(ArisUtil.getClassDetail(glass, filePath, isDebug()));
				return true;
			}
			
			if(!target.isFileRelated() && FileOpener.isTextFile(filePath) && options != null) {
				String cat = IOUtil.charsetOfTextFile(file.getAbsolutePath());
				if(OptionUtil.readBooleanPRI(options, "x", false)) {
					cat = switchChartset(cat);
				}
				
				List<String> records = FileOpener.readTextContent(filePath, true, cat);
				if(OptionUtil.readBooleanPRI(options, "one", false)) {
					String temp = IOUtil.readFileWithLineSeparator(filePath, "", cat);
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
			
			if(target instanceof TargetConsole) {
				if(FileUtil.exists(filePath)) {
					if(FileOpener.isImageFile(filePath) && OptionUtil.readBooleanPRI(options, "p", false)) {
						openWithMspaint(filePath);
					} else {
						FileOpener.open(filePath);
					}
				} else {
					XXXUtil.info("Non-existing file: {0}", filePath);
				}
				return true;
			}
			
			export(file);

			return true;
		}
				
		InputAnalyzer sean = new FileSizeInputAnalyzer(input);
		regex = "(" + KEY_SHOW_DETAIL + "|" + KEY_FOLDER_DEPTH + "|)([^<]*)(<|)";
		params = StrUtil.parseParams(regex, sean.getCommand());
		if(params != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			
			String does = params[0];
			String path = params[1];
			String type = params[2];
			if(KEY_OPEN_EXPLORER.equals(type)) {
				String targetFolder = null;
				if(EmptyUtil.isNullOrEmpty(path)) {
					targetFolder = storage();
				} else {
					targetFolder = parseFolderPath(path);
				}
				if(targetFolder != null) {
					if(PanaceaBox.isMac()) {
						PanaceaBox.openFile(targetFolder);
						C.pl2("Open Mac Finder at [" + targetFolder + "].");
					} else {
						PanaceaBox.execute("explorer " + targetFolder);
						C.pl2("Open Windows resource manager at [" + targetFolder + "].");
					}
					
					return true;
				} else {
					return false;
				}
			}
			
			if(StrUtil.equals(KEY_FOLDER_DEPTH, does)) {
				String cleverPath = parseFolderPath(path);
				if(FileUtil.getIfNormalFolder(cleverPath) != null) {
					FileDeeper dima = new FileDeeper(cleverPath);
					
					int maxLevel = dima.howDeep();
					List<MexFile> files = dima.getMaxLevelFiles();
					
					if(target.isFileRelated()) {
						export(files);
					} else {
						List<String> items = new ArrayList<>();
						for(MexFile what: files) {
							items.add(maxLevel + " " + what.getUnixPath());
						}
						
						export(items);
					}
					
					return true;
				}
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
				//an area as restive as IRAQ, fuck no.
				String cleverPath = parseFolderPath(path);
				if(cleverPath != null && FileUtil.getIfNormalFolder(cleverPath) != null) {
					paths.add(cleverPath);
				}
			}
			
			if(!EmptyUtil.isNullOrEmpty(paths)) {
				List<MexFile> allFiles = new ArrayList<>();
				for(String pathItem: paths) {
					List<MexFile> records = FileUtil.listDirectory(pathItem);
					if(!EmptyUtil.isNullOrEmpty(records)) {
						allFiles.addAll(records);
					}
				}
				
				if(!EmptyUtil.isNullOrEmpty(allFiles)) {
					if(target.isFileRelated()) {
						export(CollUtil.toFileList(allFiles));
					} else {
						boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
						MexFileComparator cesc = new MexFileComparator(orderByNameAsc);
						boolean orderByTypeDirAtTop = OptionUtil.readBooleanPRI(options, "bytype", true);
						cesc.setByTypeAsc(orderByTypeDirAtTop);
						cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
						cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
						Collections.sort(allFiles, cesc);
						String tempOptions = "+kids";
						if(StrUtil.equals(KEY_SHOW_DETAIL, does)) {
							tempOptions += ",+size";
						}
						String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
						export(allFiles, finalOptions);
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
		
		String[] regexArr = {"(.*?)\\s+(.+)", "(.*?)\\s*,\\s*(.+)"};
		String[] filepathAndCriteria = parseFilepathAndCriterias(input, regexArr);
		if(filepathAndCriteria != null) {
			String filePath = filepathAndCriteria[0];
			String criteria = filepathAndCriteria[1];
			
			if(FileOpener.isTextFile(filePath)) {
				List<MexObject> all = readFileIntoList(filePath);
				List<MexObject> items = CollUtil.filter(all, criteria, isCaseSensitive(), isStayCriteria());
				export(CollUtil.items2PrintRecords(items, options));
			} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
				Integer index = MathUtil.toInteger(criteria);
				if(index == null) {
					C.pl("try index like 0, 1, 2, 3... with " + filePath);
				} else {
					List<List<Object>> data = MsExcelHelper.readSheetByIndex(filePath, index); 
					export(data);
				}
			}
			
			return true;
		}
		
		sean = new FileSizeInputAnalyzer(input);
		SearchComponent jack = parseFolderPathAndCriterias(sean.getCommand());
		if(jack != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			List<MexFile> allMexedFiles = new ArrayList<>();
			List<String> pathList = jack.getPaths();
			for(String path : pathList) {
				String criterias = jack.getCriteria().trim();
				
				int idxOfSeparator = criterias.indexOf(' ');
				String nameCriteria = null;
				Integer depth = -1;
				
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
				
				if(depth == -1) {
					String negate = StrUtil.parseParam("!(.+)", nameCriteria);
					if(negate != null) {
						nameCriteria = negate;
					}
					depth = 0;
				}
				
				if(depth > 0) {
					depth--;
				}
				
				List<MexFile> allFiles = FileUtil.scanSingleFolder(path, depth, true);
				if(EmptyUtil.isNullOrEmpty(nameCriteria)) {
					allMexedFiles.addAll(allFiles);
				} else {
					List<MexFile> items = CollUtil.filter(allFiles, nameCriteria, isCaseSensitive(), isStayCriteria());
					allMexedFiles.addAll(items);
				}
			}
			
			if(EmptyUtil.isNullOrEmpty(allMexedFiles)) {
				exportEmptyMsg();
			} else {
				if(target.isFileRelated()) {
					List<File> files = new ArrayList<File>();
					for(MexFile mf:allMexedFiles) {
						File fileItem = mf.getFile();
						if(fileItem.isFile()) {
							files.add(fileItem);
						}
					}
					
					export(files);
				} else {
					boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
					MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
					cesc.setByTypeAsc(OptionUtil.readBoolean(options, "bytype"));
					cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
					cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
					Collections.sort(allMexedFiles, cesc);
					String tempOptions = jack.isShowDetail() ? "+size" : "";
					String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
					export(allMexedFiles, finalOptions);
				}
			}
			
			return true;
		}
		
		sean = new FileSizeInputAnalyzer(input);
		String vRegex = "(" + KEY_SHOW_DETAIL + "|)" + KEY_VERY_IMPORTANT_FOLDER + "(\\s(.*?)|)";
		params = StrUtil.parseParams(vRegex, sean.getCommand());
		if(params != null && !EmptyUtil.isNullOrEmpty(params[1])) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			boolean detail = !params[0].isEmpty();
			String criteria = params[1].trim();
			List<MexFile> records = VFileManager.g().getFileRecordsByName(criteria, isCaseSensitive());
			if(target.isFileRelated()) {
				Collections.sort(records);
				export(CollUtil.toFileList(records));
			} else {
				boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
				MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
				cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
				cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
				Collections.sort(records, cesc);
				String tempOptions = detail ? "+size" : "";
				String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
				export(records, finalOptions);
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_2DOTS)) {
			List<MexFile> records = VFileManager.g().getAllFileRecords();
			boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
			MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
			cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
			cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
			Collections.sort(records, cesc);

			if(target.isFileRelated()) {
				export(CollUtil.toFileList(records));
			} else {
				export(records);
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_VERY_IMPORTANT_FOLDER)) {  
			List<String> records = VFileManager.g().getAllFolders();
			export(records);
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_REFRESH)) {
			int[] size = VFileManager.g().refresh();
			C.pl2("Refreshed, " + size[1] + " records, before " + size[0] + ".");
			
			return true;
		}
		
		//list regex matched items in given website
		params = parseParams("(.+)@" + KEY_HTTP_WWW);
		if(params != null) {
			String regex = params[0].trim();
			String pageUrl = equiHttpProtoclIfNeeded(params[1].trim());
			String source = IOUtil.readURL(pageUrl, g().getCharsetInUse(), true);
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
		
		params = parseParams("(-|#|&|=)(.+?)");
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
					String temp = IOUtil.readFileWithLineSeparator(filePath, "", cat);
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
		
		solo = parseSoloParam(KEY_PDF + "\\s+(.+?\\.pdf)");
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
		
		solo = parseSoloParam(KEY_FIX_IMAGE + "\\s+(.+)");
		if(solo != null) {
			String whereToSave = "";
			String fileSizeWithUnit = OptionUtil.readString(options, "size");
			XXXUtil.nullCheck(fileSizeWithUnit, ":You must specify the file size you want to compress to.");
			if(FileOpener.isImageFile(solo)) {
				fixSingleImage(solo, fileSizeWithUnit);
				return true;
			}
			
			File folder = parseFolder(solo);
			if(folder != null) {
				//handle all those image files in this folder
				folder.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						String filePath = StrUtil.useSeparator(dir.getAbsolutePath(), name);
						if(FileOpener.isImageFile(filePath)) {
							fixSingleImage(filePath, fileSizeWithUnit);
						}
						return false;
					}
				});
				
				return true;
			}
		}
		
		solo = parseSoloParam(KEY_KICK_OFF + "(|\\s+.+)");
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
		
		solo = parseSoloParam(KEY_KICK_PRINT + "(|\\s+.+)");
		if(solo != null) {
			if(!solo.isEmpty()) {
				String path = solo;
				if(FileUtil.exists(path)) {
					if(FileOpener.isImageFile(path)) {
						openWithMspaint(path);
						return true;
					}
				}
			}

			String location = getExportLocation();
			String temp = g().getUserValueOf("ko.location");
			if(!EmptyUtil.isNullOrEmpty(temp)) {
				File folder = FileUtil.getIfNormalFolder(temp);
				if(folder != null) {
					location = folder.getAbsolutePath();
				} else {
					XXXUtil.info("Non-existing location: {0}, use default location: {1}", temp, location);
				}
			}
			
			String text = solo.isEmpty() ? "https://en.wikipedia.org/wiki/Live_Free_or_Die" : solo;
			String filename = DateUtil.timestamp() + "_KP.jpg";
			String filepath = StrUtil.useSeparator(location, filename);
			ImageUtil.createImage(filepath, text, 1200, 400);
			openWithMspaint(filepath);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_FILE_REMOVE + "\\s+(.+)");
		if(solo != null) {
			String filepath = null;

			File ball = parseFile(solo);
			if(ball != null) {
				filepath = ball.getAbsolutePath();
			} else {
				ball = parseFolder(solo);
				if(ball != null) {
					filepath = ball.getAbsolutePath();
				}
			}
			
			if(filepath != null) {
				String alert = "<5M";
				long filesize = FileUtil.sizeOf(filepath);
				SizeCriteria carol = new FileSizeCriteria(alert);
				if(carol.isGood(filesize) || OptionUtil.readBooleanPRI(options, "sure", false)) {
					boolean printLog = OptionUtil.readBooleanPRI(options, "p", true);
					FileUtil.remove(filepath, printLog);

					Object startObj = Stash.g().readAndRemove(Stash.KEY_START_IN_MILLIS);
					if(startObj instanceof Long) {
						long start = (Long)startObj;
						long end = System.currentTimeMillis();
						C.time2(start, end);
					}
				} else {
					String temp = "The size {0} of {1} is greater than {2}, please confirm with option $+sure";
					XXXUtil.info(temp, FileUtil.formatSize(filesize), filepath, alert.replace("<", ""));
					C.pl();
				}
			} else {
				C.pl2("Neither file nor folder: " + solo);
			}
			
			return true;
		}
		
		return false;
	}
	
	private void openWithMspaint(String filepath) {
		PanaceaBox.openFile("mspaint", filepath.replace('/', '\\'));
		C.pl2("Open by mspaint: " + filepath);
	}
	
	private void fixSingleImage(String filePath, String fileSizeWithUnit) {
		String whereToSave = null;
		boolean toSameFolder = OptionUtil.readBooleanPRI(options, "same", false);
		if(!toSameFolder) {
			if(target instanceof TargetFolder) {
				whereToSave = ((TargetFolder)target).getPath();
			} else {
				whereToSave = getExportLocation();
			}
		}
		boolean withTimestamp = g().isExportWithTimestampEnabled(options);
		String finalFilename = null;
		//handle single image file.
		if(toSameFolder) {
			finalFilename = ImageUtil.compressImageToSameFolder(filePath, fileSizeWithUnit, withTimestamp);
		} else {
			finalFilename = ImageUtil.compressImageToTargetFolder(filePath, whereToSave, fileSizeWithUnit, withTimestamp);
		}
		
		if(finalFilename != null) {
			String info = "";
			if(OptionUtil.readBooleanPRI(options, "d", true)) {
				info += " " + FileUtil.formatSize(finalFilename);
				info += " " + ImageUtil.readImageWidthHeight(finalFilename, "*");
			}

			C.pl("Saved => " + finalFilename + info);
			tryToOpenGeneratedImage(finalFilename);
			
			if(target instanceof TargetEmail) {
				export(FileUtil.getIfNormalFile(finalFilename));
			}
		}
	}
	
	public static List<MexObject> readFileIntoList(String fileName) {
		String cat = IOUtil.charsetOfTextFile(fileName);
		List<MexObject> list = new ArrayList<>();
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), cat);
			BufferedReader br = new BufferedReader(isr);
			String record = br.readLine();
			int line = 0;
			while (record != null) {
				line++;
				MexObject mo = new MexObject(record);
				mo.setPseudoOrder(line);
				list.add(mo);
				record = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	private SearchComponent parseFolderPathAndCriterias(String input) {
		String[] expArr = {"(-|)(.*?)\\s(.+)", "(-|)(.*?),\\s?(.+)"};
		List<String> allDisks = null;
		List<String> paths = new ArrayList<>();
		for(int i = 0; i < expArr.length; i++) {
			String[] params = StrUtil.parseParams(expArr[i], input);
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
	
	private String[] parseFilepathAndCriterias(String input, String[] regexArr) {
		for(int i = 0; i < regexArr.length; i++) {
			String[] params = parseParams(regexArr[i]);
			if(params != null) {
				String sourceName = params[0];
				String criteria = params[1];
				
				File file = parseFile(sourceName);
				if(file == null) {
					continue;
				}
				
				String filePath = file.getAbsolutePath();
				return new String[]{filePath, criteria};
			}
		}
		
		return null;
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
//你好老总把那个