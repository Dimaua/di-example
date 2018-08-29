package com.valeo.loyalty.android.di;

import com.valeo.loyalty.android.ui.LoginActivity;
import com.valeo.loyalty.android.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds Activities using Dagger.
 */
@Module
abstract class ActivityBindingModule {

	@ContributesAndroidInjector
	abstract LoginActivity bindLoginActivity();

	@ContributesAndroidInjector
	abstract MainActivity bindMainActivity();
}
