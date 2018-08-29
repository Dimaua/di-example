package com.valeo.loyalty.android.app.analitycs;

import android.app.Activity;

public interface AnalyticsTracker {

    /**
     * Submits event to the Analytics Tracker
     * @param eventName event name to track
     */
    void submitEvent(String eventName);

    /**
     * Tracks current screen name
     * @param activity activity to track
     * @param screenName screen name to track
     */
    void setScreenName(Activity activity, String screenName);

    /**
     * Tracks unique UserId
     * @param userId userId to track
     */
    void setUserId(String userId);

    /**
     * Clears userId after logout
     */
    void clearUserId();
}
