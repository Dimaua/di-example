package com.valeo.loyalty.android.model;

/**
 * Universal scan response for barcodes and auth codes.
 */
@SuppressWarnings("unused")
@DataModel
public class ScanResponse {

	private final String info;
	private final String message;
	private final int points;

	public ScanResponse(String info, String message, int points) {
		this.info = info;
		this.message = message;
		this.points = points;
	}

	public ResponseType getResponseType() {
		return ResponseType.parseFromInfo(info);
	}

	public String getMessage() {
		return message;
	}

	public int getPoints() {
		return points;
	}

	@Override
	public String toString() {
		return "type=" + getResponseType() + ", points=" + points;
	}

	public enum ResponseType {

		UNKNOWN_PRODUCT("invalid_barcode"),
		NOT_ELIGIBLE("not_eligible_product"),
		AUTH_CODE_REQUIRED("require_auth_code"),
		INVALID_AUTH_CODE("invalid_auth_code"),
		SCAN_LIMIT_REACHED("scan_limit_reached_for_product"),
		PRODUCT_ALREADY_SCANNED("barcode_auth_code_eligible_once"),
		SUCCESS("ok"),
		UNKNOWN("");

		private final String serverCode;

		ResponseType(String serverCode) {
			this.serverCode = serverCode;
		}

		static ResponseType parseFromInfo(String info) {
			for (ResponseType type : values()) {
				if (type.serverCode.equals(info)) {
					return type;
				}
			}

			return UNKNOWN;
		}
	}
}
