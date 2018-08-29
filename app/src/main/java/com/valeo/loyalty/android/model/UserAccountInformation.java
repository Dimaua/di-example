package com.valeo.loyalty.android.model;

/**
 * Contains user account details.
 */
@SuppressWarnings("unused")
@DataModel
public class UserAccountInformation {

	private final int points;
	private final String firstName;
	private final String lastName;
	private final String language;

	public UserAccountInformation(int points, String firstName, String lastName, String language) {
		this.points = points;
		this.firstName = firstName;
		this.lastName = lastName;
		this.language = language;
	}

	public int getPoints() {
		return points;
	}

	public String getDisplayName() {
		return firstName + " " + lastName;
	}

	public String getLanguage() {
		return language;
	}
}
