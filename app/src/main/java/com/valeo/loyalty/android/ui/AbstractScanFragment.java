package com.valeo.loyalty.android.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.Size;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.valeo.loyalty.android.BuildConfig;
import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.network.ApiClient;
import com.valeo.loyalty.android.scanner.ScanningCamera;
import com.valeo.loyalty.android.ui.dialog.DialogUtils;
import com.valeo.loyalty.android.ui.dialog.ProgressDialogFragment;
import com.valeo.loyalty.android.ui.event.MenuRequestedEvent;
import com.valeo.loyalty.android.ui.utility.UiUtility;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import java8.util.Optional;

/**
 * Base class for scanning fragments.
 */
public abstract class AbstractScanFragment extends BaseFragment {

    protected static final int SERVER_CHECK_DELAY = 500;
    private static final int MIN_BARCODE_FRAME_SIZE = 2;
    private static final long ERROR_CAPSULE_SHOW_DELAY = TimeUnit.SECONDS.toMillis(9);

    private final PermissionListener permissionListener = new CameraPermissionListener();
    private final AtomicBoolean surfaceCreated = new AtomicBoolean(false);
    protected final Handler uiHandler = new Handler(Looper.getMainLooper());
    protected ProgressDialogFragment progressDialogFragment;
    private Optional<ScanningCamera> cameraOptional = Optional.empty();
    private SurfaceHolder.Callback surfaceCallback;
    private Size surfaceSize;

    @Inject ApiClient apiClient;

    @BindView(R.id.camera_preview_surface)
    SurfaceView surfaceView;

    @BindView(R.id.detection_frame)
    ImageView detectionFrame;

    @BindView(R.id.barcode_frame)
    View barcodeFrame;

    @BindView(R.id.button_scan_help)
    View buttonHelp;

    @BindView(R.id.text_scan_hint)
    TextView hint;

    @BindView(R.id.button_back)
    ImageView backButton;

    @BindView(R.id.button_menu)
    ImageView menuButton;

    @BindView(R.id.scanner_capsule_error)
    LinearLayout capsule;

    @BindView(R.id.scanner_capsule_error_text)
    TextView capsuleText;

    @BindView(R.id.button_manual_code)
    Button manualButton;

    private Runnable showCapsuleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isDetached()) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(capsule, View.ALPHA, 1f);
                animator.setDuration(500);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        capsule.setVisibility(View.VISIBLE);
                    }
                });
                animator.start();
            }
        }
    };

    /**
     * Is called when a new instance of {@link ScanningCamera} is required.
     *
     * @return {@link ScanningCamera} instance.
     */
    protected abstract ScanningCamera onCreateCamera();

    @OnClick(R.id.button_scan_help)
    protected abstract void onHelpButtonClick();

    @OnClick({R.id.button_manual_code, R.id.scanner_capsule_error})
    protected abstract void onManualEnterClick();

    protected abstract void setDetectionFrameActive(boolean active);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        surfaceCreated.set(false);
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(permissionListener)
                .onSameThread()
                .check();

        return inflater.inflate(R.layout.fragment_code_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        surfaceCallback = new SurfaceHolderCallback();
        surfaceView.getHolder().addCallback(surfaceCallback);
        if (BuildConfig.DEBUG) {
            barcodeFrame.setVisibility(View.VISIBLE);
        }

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            backButton.setVisibility(View.GONE);
            menuButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.VISIBLE);
            menuButton.setVisibility(View.GONE);
        }
        UiUtility.hideKeyboard(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraOptional.ifPresent(ScanningCamera::stop);
        uiHandler.removeCallbacks(showCapsuleRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (capsule.getAlpha() == 1f) return;
        scheduleCapsuleAnimation();
    }

    @Override
    public void onDestroyView() {
        surfaceView.getHolder().removeCallback(surfaceCallback);
        super.onDestroyView();
        cameraOptional.ifPresent(ScanningCamera::release);
    }

    @OnClick(R.id.button_menu)
    void onMenuIconClick() {
        EventBus.getDefault().post(new MenuRequestedEvent());
    }

    @OnClick(R.id.button_back)
    void onBackIconClick() {
        navigateBack();
    }

    protected void updateBarcodeBox(Rect barcodeBox, double widthMultiplier, double heightMultiplier) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                Math.max((int) (barcodeBox.width() * widthMultiplier), MIN_BARCODE_FRAME_SIZE),
                Math.max((int) (barcodeBox.height() * heightMultiplier), MIN_BARCODE_FRAME_SIZE)
        );
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftMargin = (int) (barcodeBox.left * widthMultiplier);
        params.topMargin = (int) (barcodeBox.top * heightMultiplier);
        barcodeFrame.setLayoutParams(params);
    }

    protected void showSuccessfulResult(int pointsObtained) {
        navigateTo(R.id.fragment_container, PointsObtainedFragment.build(pointsObtained));
    }

    protected void setDetectionEnabled(boolean enabled) {
        cameraOptional.ifPresent(camera -> camera.setDetectionEnabled(enabled));
    }

    protected Optional<Size> getPreviewSize() {
        return cameraOptional.map(ScanningCamera::getPreviewSize);
    }

    protected Size getSurfaceSize() {
        return surfaceSize;
    }

    @Override
    protected void showErrorDialog(@StringRes int title, @StringRes int message, @StringRes int buttonText) {
        showErrorDialog(title, message, buttonText, () -> setDetectionEnabled(true));
    }

    protected void scheduleCapsuleAnimation() {
        uiHandler.postDelayed(showCapsuleRunnable, ERROR_CAPSULE_SHOW_DELAY);
    }

    private class CameraPermissionListener extends BasePermissionListener {

        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
            cameraOptional = Optional.of(onCreateCamera());
            if (surfaceCreated.get()) {
                cameraOptional.get().start(surfaceView.getHolder());
            }
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
            Optional.ofNullable(getActivity())
                    .ifPresentOrElse(DialogUtils::showPermissionDeniedDialog, () -> {
                    });
        }
    }

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceCreated.set(true);
            cameraOptional.ifPresent(cs -> cs.start(holder));
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            surfaceSize = new Size(width, height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceCreated.set(false);
        }
    }
}
