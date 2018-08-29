package com.valeo.loyalty.android.network;

import android.annotation.SuppressLint;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

/**
 * Naive X509 trust manager that trusts all SSL certificates it receives.
 */
@SuppressLint("TrustAllX509TrustManager")
class NaiveTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
		throws CertificateException {  }

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
		throws CertificateException {  }

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return new java.security.cert.X509Certificate[] {};
	}
}
