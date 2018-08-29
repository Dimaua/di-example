package com.valeo.loyalty.android.model;

/**
 * Holds server id of the user.
 */
@DataModel
public class UserId {

	private final String uid;

	@SuppressWarnings("unused")
	public UserId(String uid) {
		this.uid = uid;
	}

	String getUid() {
		return uid;
	}
}
