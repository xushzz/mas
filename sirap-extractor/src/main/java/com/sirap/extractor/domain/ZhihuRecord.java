package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class ZhihuRecord extends MexItem {
	private String link;
	private String question;
	private String answer;
	
	public ZhihuRecord() {
		
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(pseudoOrder).append(") ").append(question);
		sb.append(" ").append(link);
		sb.append("\n");
		sb.append(answer);
		sb.append("\n");
		
		return sb.toString();
	}
}
