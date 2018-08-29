package com.valeo.loyalty.android.scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;

/**
 * Creates prepared scanning cameras with detectors.
 */
@SuppressWarnings("WeakerAccess")
public class CameraFactory {

	private static final int CAMERA_FPS = 15;
	private static final int PREVIEW_WIDTH = 1280;
	private static final int PREVIEW_HEIGHT = 1024;

	@Inject
	Provider<BarcodeScanner> barcodeScannerProvider;

	@Inject
	Provider<AuthCodeScanner> authCodeScannerProvider;

	@Inject
	Context context;

	@Inject
	CameraFactory() {  }

	/**
	 * Creates scanning cameras that detect barcodes.
	 * @param   listener  result listener
	 * @return  {@link ScanningCamera} instance.
	 */
	public ScanningCamera createBarcodeScanningCamera(
		BarcodeScanner.BarcodeDetectionListener listener) {

		BarcodeScanner scanner = barcodeScannerProvider.get();
		scanner.setDetectionListener(listener);
		return createScanningCamera(scanner);
	}

	/**
	 * Creates scanning cameras that detect auth codes.
	 * @param   listener  result listener
	 * @return  {@link ScanningCamera} instance.
	 */
	public ScanningCamera createAuthCodeScanningCamera(
		AuthCodeScanner.AuthCodeDetectionListener listener) {

		AuthCodeScanner scanner = authCodeScannerProvider.get();
		scanner.setDetectionListener(listener);
		return createScanningCamera(scanner);
	}

	private ScanningCamera createScanningCamera(Scanner scanner) {

		CameraSource cameraSource = new CameraSource.Builder(context, scanner.getDetector())
			.setAutoFocusEnabled(true)
			.setFacing(CameraSource.CAMERA_FACING_BACK)
			.setRequestedFps(CAMERA_FPS)
			.setRequestedPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT)
			.build();

		return new ScanningCameraImpl(cameraSource, scanner);
	}

	private class ScanningCameraImpl implements ScanningCamera {

		private final AtomicBoolean cameraStarted = new AtomicBoolean(false);
		private final CameraSource cameraSource;
		private final Scanner scanner;

		ScanningCameraImpl(CameraSource cameraSource, Scanner scanner) {
			this.cameraSource = cameraSource;
			this.scanner = scanner;
		}

		@Override
		public void release() {
			cameraSource.release();
			scanner.release();
		}

		@Override
		public void setDetectionEnabled(boolean enabled) {
			scanner.setDetectionEnabled(enabled);
		}

		@SuppressLint("MissingPermission")
		@Override
		public void start(SurfaceHolder surfaceHolder) {
			try {
				cameraSource.start(surfaceHolder);
				cameraStarted.set(true);
			} catch (IOException e) {
				Timber.e(e);
			}
		}

		@Override
		public void stop() {
			cameraSource.stop();
			cameraStarted.set(false);
		}

		@Override
		public @Nullable Size getPreviewSize() {
			return cameraSource.getPreviewSize();
		}

		@Override
		public boolean isOperational() {
			return scanner.isOperational();
		}

		@Override
		public boolean isStarted() {
			return cameraStarted.get();
		}
	}
}
