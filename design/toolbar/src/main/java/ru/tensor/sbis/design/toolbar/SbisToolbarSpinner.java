package ru.tensor.sbis.design.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSpinner;

/**
 * Спиннер для тулбара
 * Легаси код
 */
@SuppressWarnings("unused")
public class SbisToolbarSpinner extends AppCompatSpinner {

    private static final long SPINNER_POPUP_SHOWING_DELAY = 300;
    private boolean mSpinnerEnabled;

    public SbisToolbarSpinner(Context context) {
        super(context);
        init();
    }

    public SbisToolbarSpinner(Context context, int mode) {
        super(context, mode);
        init();
    }

    public SbisToolbarSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SbisToolbarSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SbisToolbarSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init();
    }

    private void init() {
        mSpinnerEnabled = true;
        TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{com.google.android.material.R.attr.actionBarSize});
        try {
            int positiveOffset = (int) styledAttributes.getDimension(0, 0);
            setDropDownVerticalOffset(positiveOffset);
        } finally {
            styledAttributes.recycle();
        }
    }

    @Override
    public boolean performClick() {
        boolean handled = false;
        if (mSpinnerEnabled) {
            mSpinnerEnabled = false;
            handled = super.performClick();
            // eliminates second popup showing while previous is called but not shown yet
            new Handler(Looper.getMainLooper()).postDelayed(() -> mSpinnerEnabled = true, SPINNER_POPUP_SHOWING_DELAY);
        }

        return handled;
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        OnItemSelectedListener selectedListener = getOnItemSelectedListener();
        if (selectedListener != null) {
            selectedListener.onItemSelected(this, null, position, getItemIdAtPosition(position));
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        OnItemSelectedListener selectedListener = getOnItemSelectedListener();
        if (selectedListener != null) {
            selectedListener.onItemSelected(this, null, position, getItemIdAtPosition(position));
        }
    }
}
