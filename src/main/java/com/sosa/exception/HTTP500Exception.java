package com.sosa.exception;

/**
 * HTTP 500 Server errors
 */
public final class HTTP500Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HTTP500Exception() {
		super();
	}

	public HTTP500Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTP500Exception(String message) {
		super(message);
	}

	public HTTP500Exception(Throwable cause) {
		super(cause);
	}
}
