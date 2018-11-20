package com.sirap.extractor.images;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Album;

public class WeixinFetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String weburl) {
		return weixinLinks(weburl);
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
}
