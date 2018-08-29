package com.valeo.loyalty.android.scanner;

import android.content.Context;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import javax.inject.Inject;

import java8.util.Optional;

/**
 * Scans auth codes from camera input.
 */
public class AuthCodeScanner implements Scanner {

	private static final Pattern DIGITS = Pattern.compile("\\d");
	private static final Pattern CAP_LETTERS = Pattern.compile("[A-Z]");
	private static final Pattern AUTH_CODE = Pattern.compile("([A-Z]|\\d){13}");

	private final AtomicBoolean detectionEnabled = new AtomicBoolean(true);
	private final AuthCodeAccumulatingDetector accumulator = new AuthCodeAccumulatingDetector();
	private final TextRecognizer recognizer;
	private Optional<AuthCodeDetectionListener> listenerOptional = Optional.empty();

	@Inject
	AuthCodeScanner(Context context) {
		this.recognizer = new TextRecognizer.Builder(context).build();
		recognizer.setProcessor(new AuthCodeProcessorImpl());
	}

	@Override
	public boolean isOperational() {
		return recognizer.isOperational();
	}

	void setDetectionListener(AuthCodeDetectionListener listener) {
		listenerOptional = Optional.ofNullable(listener);
	}

	@Override
	public void release() {
		recognizer.release();
	}

	@Override
	public Detector<?> getDetector() {
		return recognizer;
	}

	@Override
	public void setDetectionEnabled(boolean detectionEnabled) {
		this.detectionEnabled.set(detectionEnabled);
	}

	private boolean canBeAuthCode(String candidate) {
		return AUTH_CODE.matcher(candidate).matches()
			&& DIGITS.matcher(candidate).find()
			&& CAP_LETTERS.matcher(candidate).find();
	}

	private class AuthCodeProcessorImpl implements Detector.Processor<TextBlock> {

		@Override
		public void release() {  }

		@Override
		public void receiveDetections(Detector.Detections<TextBlock> detections) {
			SparseArray<TextBlock> items = detections.getDetectedItems();
			List<RecognizedCode> candidates = new ArrayList<>();

			if (items.size() > 0) {
				for (int i = 0; i < items.size(); i++) {
					List<RecognizedCode> preprocessedItems = preprocessAndUnwrapCandidates(items.valueAt(i));
					candidates.addAll(preprocessedItems);
				}
			}

			for (RecognizedCode candidate : candidates) {
				if (detectionEnabled.get() && processCandidate(candidate)) {
					break;
				}
			}
		}

		private List<RecognizedCode> preprocessAndUnwrapCandidates(TextBlock candidate) {
			String[] lines = candidate.getValue().split("\n");
			List<RecognizedCode> result = new ArrayList<>();

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.indexOf(' ') == line.lastIndexOf(' ')) {
					result.add(new RecognizedCode(line.replace(" ", ""), candidate.getBoundingBox()));
				}
			}

			return result;
		}

		private boolean processCandidate(RecognizedCode candidate) {
			String candidateText = candidate.getText();
			if (canBeAuthCode(candidateText) && accumulator.addScanResult(candidateText)) {
				listenerOptional.ifPresent(listener -> listener.onAuthCodeDetected(candidate));
				return true;
			}

			return false;
		}
	}

	/**
	 * Handles detected bar codes.
	 */
	public interface AuthCodeDetectionListener {

		/**
		 * Is called when a new auth code candidate is detected.
		 * @param code  auth code candidate
		 */
		void onAuthCodeDetected(RecognizedCode code);
	}

}
