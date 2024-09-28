package ru.tensor.sbis.design.time_picker;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.theme.global_variables.TextColor;
import timber.log.Timber;

/**
 * Компонент выбора чисел
 *
 * Легаси код
 *
 * @author im.zheglov
 */
public class NumberPickerWithoutDividers extends NumberPicker {

    private static final int DEFAULT_TEXT_SIZE = 17;

    /**
     * Text size value.
     */
    private float mTextSize;

    /**
     * Text color value.
     */
    private int mTextColor;

    /**
     * Initialized fields flag.
     */
    private boolean mInitialized;

    /**
     * Override this method to change textSize property.
     */
    public float getTextSize() {
        return DEFAULT_TEXT_SIZE;
    }

    /**
     * Override this method to change textColor property.
     */
    public int getTextColor() {
        TypedValue textColorAttr = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.time_picker_wheel_text_color, textColorAttr, true)) {
            return textColorAttr.data;
        } else {
            return TextColor.DEFAULT.getValue(getContext());
        }
    }

    /**
     * Initialize class fields in this method.
     */
    @CallSuper
    protected void initFields() {
        mTextSize = getTextSize();
        mTextColor = getTextColor();
        mInitialized = true;
    }

    /** @SelfDocumented */
    public NumberPickerWithoutDividers(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setSelectionDividerHeight(0);
        } else {
            try {
                // Remove dividers above and below selected value.
                Field selectionDivider = FieldUtils.getField(NumberPicker.class, "mSelectionDivider", true);
                selectionDivider.set(this, null);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    /**
     * Allow to customize picker properties.
     */
    @Override
    public final void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (!mInitialized) {
            initFields();
        }
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            EditText editText = ((EditText) view);
            customizeView(editText);
        }
    }

    /**
     * Customize picker properties.
     */
    protected void customizeView(@NonNull EditText view) {
        view.setTextSize(mTextSize);
        view.setTextColor(mTextColor);
        view.setTypeface(TypefaceManager.getRobotoRegularFont(getContext()));
        view.setCustomSelectionActionModeCallback(new ActionModeCallback());
    }

    /**
     * Action mode callback stub implementation.
     */
    private static class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

}
