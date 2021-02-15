package com.finalProject.com.SmartContactManger.Entity;

public class ErrorMessage {
	
	private String errorMessage;
	
	private String content;

	public ErrorMessage(String errorMessage, String content) {
		this.errorMessage = errorMessage;
		this.content = content;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
