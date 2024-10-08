/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet;

import static ru.tensor.sbis.design_dialogs.dialogs.container.util.Utils.slideContentDownAndThenRun;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import ru.tensor.sbis.design.design_dialogs.R;

/**
 * Копия класса {@link BottomSheetDialog}, отличающаяся от стандартной
 * использованием {@link CustomBottomSheetBehavior} вместо {@link BottomSheetBehavior}
 */
@SuppressWarnings("unused")
public class CustomBottomSheetDialog extends AppCompatDialog {

    private static final float BACKGROUND_DIM_AMOUNT = 0.4f;

    private CustomBottomSheetBehavior<FrameLayout> mBehavior;

    boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;
    private boolean mCanceledOnTouchOutsideSet;
    private boolean mFitsSystemWindows = true;

    public CustomBottomSheetDialog(@NonNull Context context) {
        this(context, 0, true);
    }

    public CustomBottomSheetDialog(
            @NonNull Context context,
            @StyleRes int theme,
            boolean fitsSystemWindows) {
        super(context, getThemeResId(context, theme));
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mFitsSystemWindows = fitsSystemWindows;
    }

    protected CustomBottomSheetDialog(@NonNull Context context, boolean cancelable,
                                      OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancelable = cancelable;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params));
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (mCancelable != cancelable) {
            mCancelable = cancelable;
            if (mBehavior != null) {
                mBehavior.setHideable(cancelable);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBehavior != null) {
            if (mBehavior.getState() != CustomBottomSheetBehavior.STATE_EXPANDED) {
                mBehavior.setState(CustomBottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    @Override
    public void show() {
        if (isShowing()) {
            useDrawableInsteadOfSystemBackgroundDim();
        }
        super.show();
    }

    @Override
    public void cancel() {
        if (mBehavior.getState() == CustomBottomSheetBehavior.STATE_HIDDEN || !isShowing()) {
            super.cancel();
        } else {
            slideContentDownAndThenRun(this, super::cancel);
        }
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        mCanceledOnTouchOutside = cancel;
        mCanceledOnTouchOutsideSet = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        final FrameLayout container = (FrameLayout) View.inflate(getContext(),
                R.layout.design_dialogs_custom_design_bottom_sheet_dialog, null);
        final CoordinatorLayout coordinator =
                container.findViewById(R.id.design_dialogs_coordinator);
        if (layoutResId != 0 && view == null) {
            view = getLayoutInflater().inflate(layoutResId, coordinator, false);
        }

        container.setFitsSystemWindows(mFitsSystemWindows);
        coordinator.setFitsSystemWindows(mFitsSystemWindows);

        FrameLayout bottomSheet = coordinator.findViewById(R.id.design_bottom_sheet);
        mBehavior = CustomBottomSheetBehavior.from(bottomSheet);
        mBehavior.setBottomSheetCallback(mBottomSheetCallback);
        mBehavior.setHideable(mCancelable);
        if (params == null) {
            bottomSheet.addView(view);
        } else {
            bottomSheet.addView(view, params);
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        //noinspection Convert2Lambda
        coordinator.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
                    cancel();
                }
            }
        });
        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(bottomSheet, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host,
                                                          AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (mCancelable) {
                    info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS);
                    info.setDismissable(true);
                } else {
                    info.setDismissable(false);
                }
            }

            @Override
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                    cancel();
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        });
        //noinspection Convert2Lambda
        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // Consume the event and prevent it from falling through
                return true;
            }
        });
        return container;
    }

    @SuppressLint("ObsoleteSdkInt")
    boolean shouldWindowCloseOnTouchOutside() {
        if (!mCanceledOnTouchOutsideSet) {
            if (Build.VERSION.SDK_INT < 11) {
                mCanceledOnTouchOutside = true;
            } else {
                TypedArray a = getContext().obtainStyledAttributes(
                        new int[]{android.R.attr.windowCloseOnTouchOutside});
                mCanceledOnTouchOutside = a.getBoolean(0, true);
                a.recycle();
            }
            mCanceledOnTouchOutsideSet = true;
        }
        return mCanceledOnTouchOutside;
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
            TypedValue outValue = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    com.google.android.material.R.attr.bottomSheetDialogTheme,
                    outValue,
                    true)) {
                themeId = outValue.resourceId;
            } else {
                // bottomSheetDialogTheme is not provided; we default to our light theme
                themeId = com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog;
            }
        }
        return themeId;
    }

    private final CustomBottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
            = new CustomBottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet,
                                   @CustomBottomSheetBehavior.State int newState) {
            if (newState == CustomBottomSheetBehavior.STATE_HIDDEN) {
                cancel();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private void useDrawableInsteadOfSystemBackgroundDim() {
        Window window = getWindow();
        if (window == null ||
                (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_DIM_BEHIND) == 0) {
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Drawable dimBackgroundDrawable = new ColorDrawable(Color.BLACK);
        dimBackgroundDrawable.setAlpha((int) (255 * BACKGROUND_DIM_AMOUNT));
        getWindow().setBackgroundDrawable(dimBackgroundDrawable);
    }
}
