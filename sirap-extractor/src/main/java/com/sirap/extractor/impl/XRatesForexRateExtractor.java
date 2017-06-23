package com.sirap.extractor.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.ForexRateRecord;
import com.sirap.common.extractor.Extractor;

public class XRatesForexRateExtractor extends Extractor<ForexRateRecord> {

	public static final String URL_X_RATES = "http://www.x-rates.com/table";
	public static final String URL_TEMPLATE = URL_X_RATES + "/?from={0}&amount={1}";

	public XRatesForexRateExtractor(String ccyCode, String amount) {
		String url = StrUtil.occupy(URL_TEMPLATE, ccyCode, amount);
		setUrl(url);
		printFetching = true;
	}
	
	private ForexRateRecord baseRecord;
	
	public ForexRateRecord getBaseRecord() {
		return baseRecord;
	}

	public static void main(String[] args) {
		XRatesForexRateExtractor frank = new XRatesForexRateExtractor("HKD", "100.5689");
		frank.process();
		frank.list();
	}

	@Override
	protected void parseContent() {
		String regex = "<table class=\"tablesorter ratesTable\".*?</table>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		String ratesTable = null;
		if(m.find()) {
			ratesTable = m.group();
			retrieveBaseCurrency(ratesTable);
			retrievePrices(ratesTable);		
		} else {
			isAllBeingWell = false;
		}
	}
	
	protected void retrieveBaseCurrency(String table) {
		StringBuffer sb = new StringBuffer();
		sb.append("<th class='rtHeader rtHeaderCurr'>(.*?)</th>\\s*?");
		sb.append("<th class='rtHeader rtHeaderValues'>(.*?) (.*?)</th>");
		Matcher m = Pattern.compile(sb.toString()).matcher(table);
		if(m.find()) {
			String displayName = m.group(1);
			String value = m.group(2);
			String name = m.group(3);
			baseRecord = new ForexRateRecord(name, displayName, value);
		}
	}
	
	protected void retrievePrices(String table) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr>");
		sb.append("\\s*<td>(.*?)</td>");
		sb.append("\\s*<td.*?to=([a-zA-Z]{3})'>(.*?)</a></td>.*?");
		sb.append("</tr>");
		Matcher m = Pattern.compile(sb.toString()).matcher(table);
		while(m.find()) {
			String displayName = m.group(1);
			String name = m.group(2);
			String value = m.group(3);
			ForexRateRecord fp = new ForexRateRecord(name, displayName, value);
			mexItems.add(fp);
		}
	}
}
