package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class ZhihuRecord extends MexItem {
	private String questionNumber;
	private String question;
	private String answer;
	
	public ZhihuRecord() {
		
	}
		
	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
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
		String link = "https://www.zhihu.com/question/" + questionNumber;
		sb.append(" ").append(link);
		sb.append("\n");
		sb.append(answer);
		sb.append("\n");
		
		return sb.toString();
	}
}
