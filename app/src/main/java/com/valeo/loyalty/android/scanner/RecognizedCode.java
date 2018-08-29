package com.valeo.loyalty.android.scanner;

import android.graphics.Rect;

/**
 * Recognized authenticity code.
 */
public class RecognizedCode {

	private final String text;
	private final Rect rect;

	RecognizedCode(String text, Rect rect) {
		this.text = text;
		this.rect = rect;
	}

	public String getText() {
		return text;
	}

	public Rect getRect() {
		return rect;
	}

	@Override
	public String toString() {
		return text;
	}
}
