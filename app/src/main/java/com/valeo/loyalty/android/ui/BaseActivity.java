package com.valeo.loyalty.android.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.valeo.loyalty.android.locale.LocaleManager;

/**
 * Base class for activities with support of forced localization.
 */
public abstract class BaseActivity extends AppCompatActivity {

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(LocaleManager.setLocale(newBase));
	}
}
