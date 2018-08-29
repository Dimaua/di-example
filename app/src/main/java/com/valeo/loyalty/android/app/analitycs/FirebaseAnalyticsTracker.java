package com.valeo.loyalty.android.app.analitycs;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

public class FirebaseAnalyticsTracker implements AnalyticsTracker {

    private FirebaseAnalytics tracker;

    @Inject
    public FirebaseAnalyticsTracker(Context context) {
        tracker = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void submitEvent(String eventName) {
        tracker.logEvent(eventName, null);
    }

    @Override
    public void setScreenName(Activity activity, String screenName) {
        tracker.setCurrentScreen(activity, screenName, null);
    }

    @Override
    public void setUserId(String userId) {
        tracker.setUserId(userId);
    }

    @Override
    public void clearUserId() {
        tracker.setUserId(null);
    }
}
