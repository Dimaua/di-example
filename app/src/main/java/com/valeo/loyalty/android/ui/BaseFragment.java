package com.valeo.loyalty.android.ui;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.valeo.loyalty.android.R;
import com.valeo.loyalty.android.ui.dialog.AlertDialogFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == R.anim.new_fragment_enter_to_left
                || nextAnim == R.anim.new_fragment_enter_from_bottom
                || nextAnim == R.anim.new_fragment_enter_from_top) {
            Animation nextAnimation = AnimationUtils.loadAnimation(getContext(), nextAnim);
            nextAnimation.setAnimationListener(new Animation.AnimationListener() {
                private float startZ = 0f;

                @Override
                public void onAnimationStart(Animation animation) {
                    if (getView() != null) {
                        startZ = getView().getTranslationZ();
                        ViewCompat.setTranslationZ(getView(), 1f);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (getView() != null) {
                        getView().postDelayed(() -> ViewCompat.setTranslationZ(getView(), startZ), 100);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            return nextAnimation;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    protected void navigateTo(@IdRes int container, Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.new_fragment_enter_to_left, R.anim.old_fragment_out_static, R.anim.old_fragment_enter_static, R.anim.new_fragment_exit_from_left)
                    .replace(container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    protected void slideFromBottom(@IdRes int container, Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.new_fragment_enter_from_bottom, R.anim.old_fragment_out_static, R.anim.old_fragment_enter_static, R.anim.new_fragment_exit_to_bottom)
                    .replace(container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    protected void slideFromTop(@IdRes int container, Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.new_fragment_enter_from_top, R.anim.old_fragment_out_static, R.anim.old_fragment_enter_static, R.anim.new_fragment_exit_to_top)
                    .replace(container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    protected void navigateTo(@IdRes int container, Fragment fragment, boolean addToBackStack) {
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.new_fragment_enter_to_left, R.anim.old_fragment_out_static, R.anim.old_fragment_enter_static, R.anim.new_fragment_exit_from_left)
                    .replace(container, fragment);
            if (addToBackStack) transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    protected void navigateImmediatelyTo(@IdRes int container, Fragment fragment, boolean addToBackStack) {
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction()
                    .replace(container, fragment);
            if (addToBackStack) transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    protected void navigateBack() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    protected void navigateToWithBackAnimation(@IdRes int container, Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.old_fragment_out_static, R.anim.new_fragment_enter_to_left, R.anim.new_fragment_exit_from_left, R.anim.old_fragment_enter_static)
                    .replace(container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    protected void clearBackStack() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    protected void openWebView(@IdRes int container, String url, @StringRes int titleResId) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .add(container, WebviewFragment.build(url, getString(titleResId)), "webview")
                    .addToBackStack(LoginActivity.WEBVIEW_FRAGMENT_TAG)
                    .commit();
        }
    }

    protected void openWebView(@IdRes int container, String url, @StringRes int titleResId, String tag) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .add(container, WebviewFragment.build(url, getString(titleResId)), tag)
                    .addToBackStack(LoginActivity.WEBVIEW_FRAGMENT_TAG)
                    .commit();
        }
    }

    protected void showFailedDialog() {
        showErrorDialog(R.string.operation_failed_title, R.string.operation_failed, R.string.operation_failed_button);
    }

    protected void showErrorDialog(@StringRes int title, @StringRes int message, @StringRes int buttonText) {
        showErrorDialog(title, message, buttonText, () -> {
        });
    }

    protected void showErrorDialog(@StringRes int title, @StringRes int message, @StringRes int buttonText, Runnable dismissAction) {
        if (!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Timber.w("tried to show error in detached UI state");
            return;
        }
        new AlertDialogFragment.Builder()
                .setTitle(title)
                .setMessage(message)
                .setButtonText(buttonText)
                .setOnDismissAction(dismissAction)
                .build()
                .show(getFragmentManager(), "error");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
