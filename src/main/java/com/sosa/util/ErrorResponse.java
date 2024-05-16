package com.sosa.util;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@XmlRootElement
public class ErrorResponse {
	@JsonProperty("error-detail")
	@JsonSetter("error-detail")
	public final String errorDetail;
	
	@JsonProperty("error-message")
	@JsonSetter("error-message")
	public final String errorMessage;

	public ErrorResponse(Exception e, String message) {
		this.errorMessage = message;
		this.errorDetail = e.getMessage();
	}
	
	public ErrorResponse(String detail, String message) {
		this.errorMessage = message;
		this.errorDetail = detail;
	}
}