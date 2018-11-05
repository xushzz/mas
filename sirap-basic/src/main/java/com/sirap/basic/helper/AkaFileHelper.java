package com.sirap.basic.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.search.FileSizeCriteria;
import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.StrUtil;

public class AkaFileHelper {
	
	public static List<Map<String, Object>> getImageSubFoldersFrom(String dir) {
		List<File> files = FileUtil.getSubFilesByLastModifiedDescend(dir);
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return valueOf(f1).compareTo(valueOf(f2));
			}
			
			private Integer valueOf(File file) {
				if(StrUtil.equals(file.getName(), "upload")) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		List<Map<String, Object>> items = Lists.newArrayList();
		for(File file : files) {
			if(!file.isDirectory()) {
				continue;
			}
			
			String[] kids = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String extension = FileUtil.extensionOf(name);
					boolean isImage = StrUtil.isIn(extension, FileUtil.EXTENSIONS_IMAGE);
					return isImage;
				}
			});
			
			if(EmptyUtil.isNullOrEmpty(kids)) {
				continue;
			}
			
			Map<String, Object> props = Amaps.createMap("size", kids.length);
			props.put("name", file.getName());
			items.add(props);
		}
		
		return items;
	}
	
	public static Map<String, Object> imageInfoOf(File file, String hrefPrefix) {
		String area = ImageUtil.readImageWidthHeight(file, " wh ");
		if(area == null) {
			return null;
		}

		String name = file.getName();
		String href = StrUtil.useSeparator(hrefPrefix, name);
		Map<String, Object> image = Maps.newConcurrentMap();
		image.put("href", href);
		image.put("name", name);
		image.put("size", FileUtil.formatSize(file.length()));
		image.put("area", area);
		SizeCriteria so = new FileSizeCriteria(">100k");
		boolean flag = so.isGood(file.length());
		image.put("isover100k", flag);
		
		return image;
	}
	
	public static List<Map<String, Object>> getNormalFilesFrom(String dir) {
		List<File> files = FileUtil.getSubFilesByLastModifiedDescend(dir);
		final List<Map<String, Object>> holder = Lists.newArrayList();

		int count = 0, next, previous;
		for(File current : files) {
			if(!current.isFile()) {
				continue;
			}
			next = count + 1;
			if(next >= files.size()) {
				next = 0;
			}

			previous = count - 1;
			if(previous < 0) {
				previous = files.size() - 1;
			}
			
			count++;
			
			String path = current.getAbsolutePath();
			String extension = FileUtil.extensionOf(path);
			Map<String, Object> fileitem = Maps.newConcurrentMap();
			fileitem.put("istxt", StrUtil.isIn(extension, FileUtil.EXTENSIONS_TEXT));
			fileitem.put("isimage", StrUtil.isIn(extension, FileUtil.EXTENSIONS_IMAGE));
			fileitem.put("name", current.getName());
			fileitem.put("next", files.get(next).getName());
			fileitem.put("previous", files.get(previous).getName());
			fileitem.put("size", FileUtil.formatSize(current.length()));
			holder.add(fileitem);
		}
		
		return holder;
	}
}
