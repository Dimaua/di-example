package com.valeo.loyalty.android.storage;

public class ProductScanSequenceHelper {

    private ProductScanSequenceDetails currentSequence;

    public void initiateNewSequence(ProductScanSequenceDetails currentSequence) {
        this.currentSequence = currentSequence;
    }

    public void updateSequenceBarcode(String newBarcode) {
        this.currentSequence.barcode = newBarcode;
    }

    public void updateSequenceAuthCode(String newAuthCode) {
        this.currentSequence.authcode = newAuthCode;
    }

    public ProductScanSequenceDetails getCurrentSequence() {
        return currentSequence;
    }
}
