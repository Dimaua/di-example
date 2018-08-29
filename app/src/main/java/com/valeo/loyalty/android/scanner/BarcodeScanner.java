package com.valeo.loyalty.android.scanner;

import android.content.Context;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import java8.util.Optional;

/**
 * Scans barcodes from camera input.
 */
public class BarcodeScanner implements Scanner {

	private final BarcodeDetector detector;
	private Optional<BarcodeDetectionListener> listenerOptional = Optional.empty();
	private boolean detectionEnabled = true;

	@Inject
	BarcodeScanner(Context context) {
		this.detector = new BarcodeDetector.Builder(context).build();
		detector.setProcessor(new BarcodeProcessorImpl());
	}

	@Override
	public boolean isOperational() {
		return detector.isOperational();
	}

	void setDetectionListener(BarcodeDetectionListener listener) {
		listenerOptional = Optional.ofNullable(listener);
	}

	@Override
	public void release() {
		detector.release();
	}

	@Override
	public Detector<Barcode> getDetector() {
		return detector;
	}

	@Override
	public void setDetectionEnabled(boolean detectionEnabled) {
		this.detectionEnabled = detectionEnabled;
	}

	private class BarcodeProcessorImpl implements Detector.Processor<Barcode> {

		@Override
		public void release() {  }

		@Override
		public void receiveDetections(Detector.Detections<Barcode> detections) {
			SparseArray<Barcode> detectedItems = detections.getDetectedItems();
			List<Barcode> result = new ArrayList<>();
			if (detectionEnabled && detectedItems.size() > 0) {
				for (int i = 0; i < detectedItems.size(); i++) {
					result.add(detectedItems.valueAt(i));
				}

				listenerOptional.ifPresent(listener -> listener.onBarcodesDetected(result));
			}
		}
	}

	/**
	 * Handles detected bar codes.
	 */
	public interface BarcodeDetectionListener {

		/**
		 * Is called when new bar codes are detected.
		 * @param barcodes  detected barcodes
		 */
		void onBarcodesDetected(List<Barcode> barcodes);
	}
}
