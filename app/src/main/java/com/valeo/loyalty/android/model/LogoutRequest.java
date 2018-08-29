package com.valeo.loyalty.android.model;

@DataModel
public class LogoutRequest {
    public String logoutToken;

    public LogoutRequest(String logoutToken) {
        this.logoutToken = logoutToken;
    }
}
