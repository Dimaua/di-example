package com.valeo.loyalty.android.network.exception;

/**
 * General server API exception.
 */
public class ApiException extends DataRequestException {

	public static final int HTTP_UNAUTHORIZED = 401;

	private final String errorType;
	private final int httpStatus;

	public ApiException(String message, String errorType, int httpStatus) {
		super(message);
		this.errorType = errorType;
		this.httpStatus = httpStatus;
	}

	public ApiException(String message) {
		this(message, "");
	}

	public ApiException(String message, int httpStatus) {
		this(message, "", httpStatus);
	}

	public ApiException(String message, String errorType) {
		this(message, errorType, 0);
	}

	public ApiException(Throwable cause) {
		super(cause);
		this.errorType = "";
		this.httpStatus = 0;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getErrorType() {
		return errorType;
	}
}
