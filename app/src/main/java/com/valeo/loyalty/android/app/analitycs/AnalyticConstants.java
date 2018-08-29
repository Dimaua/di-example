package com.valeo.loyalty.android.app.analitycs;

public class AnalyticConstants {
    //region Analytic event names
    public static final String EVENT_LOGIN_SCREEN_LOG_IN = "android_log_in";
    public static final String EVENT_LOGIN_SCREEN_CREATE_ACCOUNT = "android_create_an_account";
    public static final String EVENT_LOGIN_SCREEN_LOGIN_ERROR_MESSAGE = "android_log_in_error_message";

    public static final String EVENT_SCAN_SCREEN_SCAN_PRODUCT = "android_scan_product_barcode";
    public static final String EVENT_SCAN_SCREEN_SCAN_AUTH_CODE = "android_scan_authenticity_code";
    public static final String EVENT_SCAN_SCREEN_PRODUCT_ERROR_MESSAGE = "android_product_error_message";
    public static final String EVENT_SCAN_SCREEN_BARCODE_ERROR_MESSAGE = "android_barcode_error_message";
    public static final String EVENT_SCAN_SCREEN_LOYALTY_ERROR_MESSAGE = "android_loyalty_program_error_message";
    public static final String EVENT_SCAN_SCREEN_MAXIMUM_QUANTITY_ERROR_MESSAGE = "android_maximum_quantity_error_message";
    public static final String EVENT_SCAN_SCREEN_SCAN_ERROR_MESSAGE = "android_scan_error_message";
    public static final String EVENT_SCAN_SCREEN_AUTH_ERROR_MESSAGE = "android_authenticity_code_error_message";
    public static final String EVENT_SCAN_SCREEN_SHOW_HELP = "android_help_message";
    public static final String EVENT_SCAN_SCREEN_INVALID_CODE_ERROR = "android_authenticity_code_invalid";

    public static final String EVENT_VALIDATION_SCREEN_AUTH_VALIDATION = "android_validation_authenticity";

    public static final String EVENT_POINTS_SCREEN_CONFIRM_PRODUCT = "android_confirmation_scan_product";
    public static final String EVENT_POINTS_SCREEN_GIFT_SHOP = "android_gift_shop";
    public static final String EVENT_POINTS_SCREEN_MY_ACCOUNT = "android_my_account";
    public static final String EVENT_POINTS_SCREEN_SCAN_ANOTHER = "android_scan_another_product";

    public static final String EVENT_NAVIGATION_SCAN = "android_my_account_scan";
    public static final String EVENT_NAVIGATION_LOYALTY = "android_my_account_loyalty_program";
    public static final String EVENT_NAVIGATION_GIFT_SHOP = "android_my_account_gift_shop";
    public static final String EVENT_NAVIGATION_PROMOTIONS = "android_my_account_promotions";
    public static final String EVENT_NAVIGATION_ACCOUNT = "android_my_account_my_account";
    public static final String EVENT_NAVIGATION_LEGAL_NOTICE = "android_my_account_legal_notice";
    public static final String EVENT_NAVIGATION_LOG_OUT = "android_my_account_log_out";

    public static final String EVENT_LOG_OUT_CONFIRM = "android_log_out";
    //endregion

    //region Screen names
    public static final String WELCOME_SCREEN_NAME = "welcome";
    public static final String LOGIN_SCREEN_NAME = "login";
    public static final String POINTS_SCREEN_NAME = "scan_confirmation";
    public static final String SCAN_BARCODE_SCREEN_NAME = "scan_barcode";
    public static final String SCAN_AUTHCODE_SCREEN_NAME = "scan_authenticity";
    public static final String VALIDATE_AUTHCODE_SCREEN_NAME = "validation_authenticity";

    public static final String WEBVIEW_PROGRAM_SCREEN = "webview_program";
    public static final String WEBVIEW_SHOP_SCREEN = "webview_shop";
    public static final String WEBVIEW_PROMOTIONS_SCREEN = "webview_promotions";
    public static final String WEBVIEW_ACCOUNT_SCREEN = "webview_account";
    public static final String WEBVIEW_LEGAL_SCREEN = "webview_legal";
    //endregion
}
