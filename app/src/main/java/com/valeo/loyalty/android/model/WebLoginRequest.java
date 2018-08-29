package com.valeo.loyalty.android.model;

/**
 * Request data for web login.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
@DataModel
public class WebLoginRequest {

	private final String name;
	private final String pass;

	public WebLoginRequest(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}
}
