package com.sirap.geek.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

@SuppressWarnings("serial")
public class DistrictItem extends MexItem implements Comparable<DistrictItem> {
	
	public static final List<String> LEVELS = StrUtil.split("country,province,city,district,street");
	private String adcode;
	private String name;
	private String center;
	private String level;
	private List<DistrictItem> kids;
	
	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getLevel() {
		return level;
	}

	public boolean isTopLevel() {
		int index = LEVELS.indexOf(level);
		return index == 0;
	}
	
	public boolean sameLevelAs(DistrictItem item) {
		return StrUtil.equals(level, item.level);
	}
	
	public List<String> nextKLevels(int k) {
		int index = LEVELS.indexOf(level);
		if(index < 0) {
			XXXUtil.alert("Not a valid level: {0}, should be one of {1}", level, LEVELS);
		}
		
		List<String> levels = Lists.newArrayList();
		if(isBottomLevel()) {
			return levels;
		}
		
		for(int i = 0; i < k; i++) {
			if(index + i + 1 < LEVELS.size()) {
				levels.add(LEVELS.get(index + i + 1));
			}
		}
		
		return levels;
	}
	
	public String previousLevel() {
		if(isTopLevel()) {
			return null;
		}
		int index = LEVELS.indexOf(level);
		if(index < 0) {
			XXXUtil.alert("Not a valid level: {0}, should be one of {1}", level, LEVELS);
		}
		
		return LEVELS.get(index - 1);
	}
	
	public String nexLevel() {
		if(isBottomLevel()) {
			return null;
		}
		int index = LEVELS.indexOf(level);
		if(index < 0) {
			XXXUtil.alert("Not a valid level: {0}, should be one of {1}", level, LEVELS);
		}
		
		return LEVELS.get(index + 1);
	}
	
	public boolean isBottomLevel() {
		int index = LEVELS.indexOf(level);
		return index == LEVELS.size() - 1;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<DistrictItem> getKids() {
		return kids;
	}

	public void setKids(List<DistrictItem> kids) {
		this.kids = kids;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		if(isRegexMatched(adcode, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(adcode, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(center, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(center, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(level, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(level, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parse(String record) {
		//110101 和平里街道 116.423882,39.956395 street
		String[] items = record.split(" ");
		if(items.length < 4) {
			return false;
		} 

		adcode = items[0];
		if(!StrUtil.isRegexMatched("\\d+", adcode)) {
			return false;
		}
		name = items[1];
		center = items[2];
		level = items[3];
		
		return true;
	}
	
	@Override
	public String toString() {
		ValuesItem item = new ValuesItem();
		int index = LEVELS.indexOf(level);
		if(index < 0) {
			index = 0;
		}
		item.add(StrUtil.spaces(index * 2) + adcode);
		item.add(name);
		item.add(center);
		item.add(level);
//		item.add(kids);
		
		return item.toPrint("c= ");
	}
	
	@Override
	public String toPrint(String options) {
		ValuesItem item = new ValuesItem();
		int index = LEVELS.indexOf(level);
		if(index < 0) {
			index = 0;
		}
		String dent = "";
		if(OptionUtil.readBooleanPRI(options, "dent", true)) {
			dent = StrUtil.spaces(index * 2);
		}
		item.add(dent + adcode);
		item.add(name);
		item.add(center);
		item.add(level);
//		item.add(kids);
		
		return item.toPrint("c= ");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (adcode + name).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistrictItem other = (DistrictItem) obj;
		
		return (adcode + name).equals(other.adcode + other.name);
	}

	@Override
	public int compareTo(DistrictItem item) {
		return (adcode + name).compareTo(item.adcode + item.name);
	}
}