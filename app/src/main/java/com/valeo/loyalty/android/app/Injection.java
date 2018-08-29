package com.valeo.loyalty.android.app;

import com.valeo.loyalty.android.di.MainInjector;

/**
 * Facade for dependency injection.
 */
public final class Injection {

	private static MainInjector injector;

	private Injection() {  }

	/**
	 * Gets main dependency injector.
	 * @return  {@link MainInjector} instance.
	 */
	public static MainInjector mainInjector() {
		return injector;
	}

	static void setInjector(MainInjector injector) {
		Injection.injector = injector;
	}
}
