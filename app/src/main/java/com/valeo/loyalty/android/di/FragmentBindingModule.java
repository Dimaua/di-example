package com.valeo.loyalty.android.di;

import com.valeo.loyalty.android.ui.EditAuthcodeFragment;
import com.valeo.loyalty.android.ui.LoginFragment;
import com.valeo.loyalty.android.ui.ManualCodeFragment;
import com.valeo.loyalty.android.ui.PointsObtainedFragment;
import com.valeo.loyalty.android.ui.ScanAuthCodeFragment;
import com.valeo.loyalty.android.ui.ScanBarcodeFragment;
import com.valeo.loyalty.android.ui.ScanCodeNoticeFragment;
import com.valeo.loyalty.android.ui.ValidateAuthcodeFragment;
import com.valeo.loyalty.android.ui.WebviewFragment;
import com.valeo.loyalty.android.ui.WelcomeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds Fragments using Dagger.
 */
@Module
abstract class FragmentBindingModule {

	@ContributesAndroidInjector
	abstract LoginFragment bindLoginFragment();

	@ContributesAndroidInjector
	abstract WebviewFragment bindWebviewFragment();

	@ContributesAndroidInjector
	abstract ScanBarcodeFragment bindScanBarcodeFragment();

	@ContributesAndroidInjector
	abstract ScanAuthCodeFragment bindScanAuthCodeFragment();

	@ContributesAndroidInjector
	abstract PointsObtainedFragment bindPointsObtainedFragment();

	@ContributesAndroidInjector
	abstract ValidateAuthcodeFragment bindValidateAuthcodeFragment();

	@ContributesAndroidInjector
	abstract ScanCodeNoticeFragment bindScanAuthCodeNotice();

	@ContributesAndroidInjector
	abstract ManualCodeFragment bindManualCodeFragment();

	@ContributesAndroidInjector
	abstract EditAuthcodeFragment bindEditAuthcodeFragment();

	@ContributesAndroidInjector
	abstract WelcomeFragment bindWelcomeFragment();
}
