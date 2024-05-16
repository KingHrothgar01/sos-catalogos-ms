package com.sosa.exception;

/**
 * HTTP 401 Unauthorized
 */
public final class HTTP401Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HTTP401Exception() {
		super();
	}

	public HTTP401Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTP401Exception(String message) {
		super(message);
	}

	public HTTP401Exception(Throwable cause) {
		super(cause);
	}
}
