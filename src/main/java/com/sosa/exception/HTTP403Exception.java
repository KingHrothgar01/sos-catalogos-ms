package com.sosa.exception;

/**
 * HTTP 403 Forbidden errors
 */
public final class HTTP403Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HTTP403Exception() {
		super();
	}

	public HTTP403Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTP403Exception(String message) {
		super(message);
	}

	public HTTP403Exception(Throwable cause) {
		super(cause);
	}
}
