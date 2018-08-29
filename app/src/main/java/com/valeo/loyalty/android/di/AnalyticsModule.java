package com.valeo.loyalty.android.di;

import com.valeo.loyalty.android.app.analitycs.AnalyticsTracker;
import com.valeo.loyalty.android.app.analitycs.FirebaseAnalyticsTracker;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract AnalyticsTracker provideAnalyticsTracker(FirebaseAnalyticsTracker tracker);
}
