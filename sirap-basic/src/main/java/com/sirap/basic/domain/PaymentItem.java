package com.sirap.basic.domain;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class PaymentItem extends MexItem  {

	public static final String TYPE_WEIXIN = "Weixin";
	public static final String TYPE_ALIPAY = "Alipay";
	
	private String url;
	private String remark;
	
	public PaymentItem() {}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getType() {
		if(StrUtil.startsWith(url, "wxp")) {
			return TYPE_WEIXIN;
		} else if(StrUtil.contains(url, "alipay")) {
			return TYPE_ALIPAY;
		} else {
			return "Unknown";
		}
	}

	@Override
	public String toString() {
		return "PaymentItem [url=" + url + ", remark=" + remark + "]";
	}
}
