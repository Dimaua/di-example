package com.valeo.loyalty.android.storage;

public class ProductScanSequenceDetails {
    public String barcode;
    public String authcode;

    public ProductScanSequenceDetails(String barcode, String authcode) {
        this.barcode = barcode;
        this.authcode = authcode;
    }

    public ProductScanSequenceDetails(String barcode) {
        this.barcode = barcode;
    }
}
