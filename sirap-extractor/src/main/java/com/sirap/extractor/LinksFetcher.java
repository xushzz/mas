package com.sirap.extractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;

public class LinksFetcher {
	
	public static final String HOMEPAGE_QIHU360 = "http://image.so.com";
	public static final String HOMEPAGE_SOGOU = "http://pic.sogou.com";
	
	public static Album fetchAlbum(final String albumUrl) {
		if(StrUtil.contains(albumUrl, "163.com")) {
			return netease163Links(albumUrl);
		} else if(StrUtil.contains(albumUrl, "sina.com")) {
			return sinaSlides(albumUrl);
		} else if(StrUtil.contains(albumUrl, "weixin.qq.com")) {
			return weixinLinks(albumUrl);
		} else {
			XXXUtil.alerto("Not yet supported website: ", albumUrl);
		}
		
		return null;
	}
	
	public static Album sinaSlides(final String albumUrl) {
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
				String temp = albumUrl.replaceAll("^http:", "https:");
				return temp;
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
			
			public static final String URL = HOMEPAGE_SOGOU + "/pics?query=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"pic_url\":\"(.*?)\"";
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(m.find()) {
					String imageUrl = m.group(1);
					mexItems.add(imageUrl);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static List<String> qihu360ImageLinks(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = HOMEPAGE_QIHU360 + "/i?q=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"img\":\"(.*?)\"";
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
}
