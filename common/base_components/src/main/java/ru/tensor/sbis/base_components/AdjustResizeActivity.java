package ru.tensor.sbis.base_components;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import ru.tensor.sbis.base_components.keyboard.KeyboardAware;
import ru.tensor.sbis.base_components.keyboard.KeyboardAwareExtension;
import ru.tensor.sbis.base_components.keyboard.KeyboardDetector;
import ru.tensor.sbis.base_components.keyboard.KeyboardDetectorExtension;

/**
 * Legacy-код
 * Created by ss.buvaylink on 02.11.2016.
 */
public abstract class AdjustResizeActivity extends TrackingActivity
        implements KeyboardAware {

    private View mContentView;

    /** SelfDocumented */
    @IdRes
    protected abstract int getContentViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupKeyboardMonitoring();
    }

    /** SelfDocumented */
    @NonNull
    protected View getContentView() {
        if (mContentView == null) {
            mContentView = findViewById(getContentViewId());
        }
        return mContentView;
    }

    private void setupKeyboardMonitoring() {
        KeyboardDetector keyboardDetector = KeyboardAwareExtension
                .keyboardDetectorFromViewProvider(
                        this,
                        this::getContentView,
                        KeyboardAwareExtension
                                .createDispatcherToNestedFragment(
                                        this,
                                        getContentViewId(),
                                        KeyboardAwareExtension
                                                .createViewHeightResizerFromViewProvider(
                                                        this,
                                                        this::getContentView
                                                )
                                )
                );
        KeyboardDetectorExtension.manageBy(keyboardDetector, getLifecycle());
    }

}
