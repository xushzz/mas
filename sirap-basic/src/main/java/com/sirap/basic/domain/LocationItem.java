package com.sirap.basic.domain;

import com.sirap.basic.util.LonglatUtil;
import com.sirap.basic.util.StrUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class LocationItem extends MexItem {
	private LongOrLat lon;
	private LongOrLat lat;
	
	public String longCommaLat() {
		String value = lon.getSignedDecimal() + "," + lat.getSignedDecimal();
		return value;
	}
	
	@Override
	public boolean parse(String jackCommaJones) {
		String regex = "(.+?)\\s*,\\s*(.+?)";
		String[] params = StrUtil.parseParams(regex, jackCommaJones);
		if(params == null) {
			return false;
		}

		LongOrLat itemA = LonglatUtil.longOrLatOfDMS(params[0]);
		LongOrLat itemB = LonglatUtil.longOrLatOfDMS(params[1]);
		
		if(itemA == null || itemB == null) {
			return false;
		}
		
		if(itemA.isLongitude()) {
			lon = itemA;
			lat = itemB;
		} else if(itemA.isLatitude()) {
			lat = itemA;
			lon = itemB;
		} else {
			lon = itemA;
			lat = itemB;
		}
		
		return true;
	}
}
