package com.valeo.loyalty.android.model;

/**
 * Request to scan bar codes and auth codes.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
@DataModel
public class BarcodeScanRequest {

	private final String barcode;
	private final String authCode;

	public BarcodeScanRequest(String barcode) {
		this(barcode, null);
	}

	public BarcodeScanRequest(String barcode, String authCode) {
		this.barcode = barcode.trim();
		this.authCode = authCode;
	}
}
