package com.valeo.loyalty.android.network.exception;

/**
 * General exception that happened during data request.
 */
public class DataRequestException extends Exception {

	public DataRequestException(String message) {
		super(message);
	}

	public DataRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataRequestException(Throwable cause) {
		super(cause);
	}
}
