package com.valeo.loyalty.android.scanner;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import timber.log.Timber;

/**
 * Accumulates raw scan results and indicates when the scan result is the same over a number
 * of tries (which means it's stable and can be shown to the user).
 */
class AuthCodeAccumulatingDetector {

	private static final int STACK_SIZE = 2;

	private final List<String> items = new ArrayList<>();

	/**
	 * Adds a raw scan result.
	 * @param   scanResult    raw scan result
	 * @return  true if the result is stable, false otherwise.
	 */
	synchronized boolean addScanResult(String scanResult) {
		items.add(0, scanResult);

		if (items.size() > STACK_SIZE) {
			items.remove(items.size() - 1);
		} else {
			return false;
		}

		Timber.i(">> " + (StreamSupport.stream(items).collect(Collectors.joining(" | "))));

		for (String oldItem : items) {
			if (oldItem.hashCode() != scanResult.hashCode()) {
				return false;
			}
		}

		items.clear();

		return true;
	}
}
