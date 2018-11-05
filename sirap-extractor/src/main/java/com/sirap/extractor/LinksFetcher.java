package com.sirap.extractor;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;

public class LinksFetcher {

	public static final String QIHU360_HTML_QUERY = "http://image.so.com/i?q={0}";
	public static final String QIHU360_JSON_QUERY = "http://image.so.com/j?q={0}&pn={1}&sn={2}";
	public static final String HOMEPAGE_QIHU360 = "http://image.so.com";
	public static final String HOMEPAGE_SOGOU = "http://pic.sogou.com";
	public static final String HOMEPAGE_WEIBO = "https://s.weibo.com/ajax_pic/list?q={0}&page={1}";
	
	public static Album fetchAlbum(final String albumUrl) {
		if(StrUtil.contains(albumUrl, "163.com")) {
			return netease163Links(albumUrl);
		} else if(StrUtil.contains(albumUrl, "sina.com")) {
			return sinaLinks(albumUrl);
		} else if(StrUtil.contains(albumUrl, "sina.cn")) {
			return sinaLinksBySrc(albumUrl);
		} else if(StrUtil.contains(albumUrl, "weixin.qq.com")) {
			return weixinLinks(albumUrl);
		} else if(StrUtil.contains(albumUrl, "superiorpics.com")) {
			return superAlbumLinks(albumUrl);
		} else {
			XXXUtil.alerto("Not yet supported website: ", albumUrl);
		}
		
		return null;
	}
	
	public static Album sinaLinks(final String albumUrl) {
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return albumUrl;
			}
			
			@Override
			protected void parse() {
				item = sinaLinksByImageUrl(source);
				if(item == null) {
					item = sinaLinksByDataSrc(source);
				}
				if(item != null) {
					item.setTag("sina");
				}
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}
	
	//http://slide.sports.sina.com.cn/cba/slide_2_792_193598.html
	private static Album sinaLinksByImageUrl(String htmlContent) {
		D.at();
		String regex = JsonUtil.createRegexKey("image_url");
		Matcher ma = StrUtil.createMatcher(regex, htmlContent);
		List<String> links = Lists.newArrayList();
		while(ma.find()) {
			String temp = ma.group(1);
			temp = temp.replace("\\", "");
			links.add(temp);
		}
		
		if(!links.isEmpty()) {
			regex = "<meta itemprop=\"name\" content=\"([^\"]+)\"\\s+/>";
			String title = StrUtil.findFirstMatchedItem(regex, htmlContent);
			
			return new Album(title, links);
		}
		
		return null;
	}
	
	//http://slide.news.sina.com.cn/z/slide_1_64237_325800.html#p=3
	private static Album sinaLinksByDataSrc(String htmlContent) {
		D.at();
		String regex = "data-src=\"([^\"]+)\"";
		Matcher ma = StrUtil.createMatcher(regex, htmlContent);
		List<String> links = Lists.newArrayList();
		while(ma.find()) {
			String temp = ma.group(1);
			temp = temp.replace("\\", "");
			links.add(temp);
		}
		
		if(!links.isEmpty()) {
			regex = "<h1 class=\"name\">([^<>]+)</h1>";
			String title = StrUtil.findFirstMatchedItem(regex, htmlContent);
			
			return new Album(title, links);
		}
		
		return null;
	}

	//https://photo.sina.cn/album_2_786_197690.htm?vt=4&hd=1
	private static Album sinaLinksBySrc(String albumUrl) {
		D.at();
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return useHttps(albumUrl);
			}
			
			@Override
			protected void parse() {
				String regex = "__webURL:'([^']+)'";
				String webUrl = StrUtil.findFirstMatchedItem(regex, source);
				item = sinaLinks(webUrl);
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}

	//http://news.163.com/photoview/00AP0001/2297233.html
	public static Album netease163Links(String albumUrl) {
		D.at();
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return albumUrl;
			}
			
			@Override
			protected void parse() {
				String regex = JsonUtil.createRegexKey("img");
				Matcher ma = createMatcher(regex);
				List<String> links = Lists.newArrayList();
				while(ma.find()) {
					String temp = ma.group(1);
					links.add(temp);
				}
				
				if(!links.isEmpty()) {
					regex = "<h1>([^<>]+)</h1>";
					String title = StrUtil.findFirstMatchedItem(regex, source);
					
					item = new Album(title, links);
					item.setTag("ease");
				}
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}
	
	//https://mp.weixin.qq.com/s?src=11&timestamp=1540985436&ver=1216&signature=4VyTKR49-OkfwFQbwNqoDnwzo4CndiPw-6qJEoGLlSzXDGrhMZAJL3slvfNik7OrSp61KSMS6zbNhY7mVP1y-BDLTaq825UOocx5ipZa8FFoG*EmiRlRIlcdhPg3zire&new=1
	public static Album weixinLinks(String albumUrl) {
		D.at();
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return useHttps(albumUrl);
			}
			
			@Override
			protected void parse() {
				String regex = "data-src=\"([^\"]+)\"";
				Matcher ma = createMatcher(regex);
				List<String> links = Lists.newArrayList();
				while(ma.find()) {
					String temp = ma.group(1);
					links.add(temp);
				}
				
				if(!links.isEmpty()) {
					regex = "<h2 class=\"rich_media_title\" id=\"activity-name\">([^<>]+)</h2>";
					String title = StrUtil.findFirstMatchedItem(regex, source);
					if(title != null) {
						title = title.trim();
					} else {
						title = "Title-" + RandomUtil.name();
					}
					
					item = new Album(title, links);
					item.setTag("wxin");
					item.setUseUnique(true);
				}
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}

	public static List<String> sogouImageLinks(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = "http://pic.sogou.com/pics?query=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"pic_url\":\"(.*?)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String imageUrl = m.group(1);
					mexItems.add(imageUrl);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static List<String> qihu360ImageLinksx(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = "http://image.so.com/i?q=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"img\":\"(http[^\"]+)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add(item);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	private static Extractor<String> createQihuExtractor(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "\"img\":\"(http[^\"]+)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add(item);
				}
				
				if(url.contains("/i?")) {
					item = StrUtil.findFirstMatchedItem("\"lastindex\"\\s*:\\s*(\\d+)", source);
					D.pl("Contains no lastindex from " + url);
				}
			}
		};
		
		return frank;
	}
	
	public static List<String> qihu360ImageLinks(String keyword, int maxPage) {
		String query = XCodeUtil.urlEncodeUTF8(keyword);
		int lastindex = 60;
		int pagesize = 60;
		List<String> links = Lists.newArrayList();
		for(int k = 0; k < maxPage; k++) {
			String url;
			List<String> current;
			if(k == 0) {
				url = StrUtil.occupy(QIHU360_HTML_QUERY, query);
				Extractor<String> frank = createQihuExtractor(url).process();
				String lastindexInfo = frank.getItem();
				if(lastindexInfo != null) {
					lastindex = Integer.parseInt(lastindexInfo);
				}
				current = frank.getItems();
			} else {
				url = StrUtil.occupy(QIHU360_JSON_QUERY, query, pagesize, lastindex);
				lastindex += pagesize;
				current = createQihuExtractor(url).process().getItems();
			}
			
			if(current.isEmpty()) {
				C.pl2("Break when it comes to page {0} with {1}", k, url);
				break;
			}
			
			links.addAll(current);
		}
		
		return Colls.distinctOf(links);
	}
	
	public static Album superAlbumLinksOfIga() {
		String url = "https://www.superiorpics.com/c/Iga_Wyrwal/";
		return superAlbumLinks(url);
	}
	
	//https://www.superiorpics.com/c/Iga_Wyrwal/
	//https://www.superiorpics.com/c/Iga_Wyrwal/index1.html
	public static Album superAlbumLinks(String personUrl) {
		List<String> linksA = Lists.newArrayList();
		int page = 1;
		while(page < 99) {
			String pageUrl = personUrl;
			if(page > 1) {
				String indexInfo = StrUtil.occupy("index{0}.html", page);
				pageUrl = StrUtil.useDelimiter("/", personUrl, indexInfo);
			}
			page++;
			List<String> current = superAlbumLinksByPage(pageUrl);
			if(current.isEmpty()) {
				break;
			}
			linksA.addAll(current);
		}
		
//		D.list(linksA);
		List<String> linksB = superImageLinks(linksA);
//		D.list(linksB);
		
		Album al = new Album("Iga Wyrwal", linksB);
		al.setTag("super");
		
		return al;
	}
	
	private static List<String> superAlbumLinksByPage(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				//<div class="box135-thumb-wrapper"><a href="https://www.hotflick.net/u/g/?d=3194107"
				String regex = "<div class=\"box135-thumb-wrapper\"><a href=\"(https://www.hotflick[^\"]+)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					mexItems.add(temp);
				}
				
			}
		};
		
		return frank.process().getItems();
	}
	
	//https://www.hotflick.net/u/g/index.php?d=2732862&p=2.page
	private static Extractor<String> createSuperImageLinksExtractor(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "src=\"([^\"]+)\" class=\"img-100pr\">";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String href = temp.replace("/tn/", "/");
					mexItems.add(href);
				}
			}
		};
		
		return frank;
	}
	
	//https://www.hotflick.net/u/g/?d=3194386
	public static List<String> superImageLinks(List<String> albums) {
		List<String> links = Lists.newArrayList();
		for(String album : albums) {
			String href = album.replace("/?", "/index.php?");
			int page = 1;
			while(page < 99) {
				String url = StrUtil.occupy("{0}&p={1}.page", href, page++);
				Extractor<String> frank = createSuperImageLinksExtractor(url);
				List<String> current = frank.process().getItems();
				if(current.isEmpty()) {
					break;
				}
				links.addAll(current);
			}
//			break;
		}
		
		return Colls.distinctOf(links);
	}
	
	public static List<String> weiboImageLinks(String keyword) {
		return weiboImageLinks(keyword, 1);
	}
	
	public static List<String> weiboImageLinks(String keyword, int page) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				String query = encodeURLParam(keyword);
				return StrUtil.occupy(HOMEPAGE_WEIBO, query, page);
			}
			
			@Override
			protected void parse() {
				//"original_pic": "\/\/wx4.sinaimg.cn\/large\/9fdf5b9bly1fwuwn0a6qoj20fa0aw400.jpg",
				String regex = JsonUtil.createRegexKey("original_pic");
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add("https:" + item);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
}
