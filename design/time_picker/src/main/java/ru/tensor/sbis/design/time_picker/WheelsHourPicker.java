package ru.tensor.sbis.design.time_picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import java.util.Locale;


/**
 *  Тайм пикер (часы) в виде колеса
 *
 *  Легаси код
 *
 * @author aa.mironychev
 */
public class WheelsHourPicker extends NumberPickerWithoutDividers {

    private static final int HOURS_COUNT = 24;

    /**
     * Формат полночи: если true - то 0 часов, если false - то 24 часа
     */
    private boolean mMidnightAsZero = false;

    private String mLabelPattern;

    /** @SelfDocumented */
    public WheelsHourPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@NonNull AttributeSet attrs) {
        TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimePickerWheelsHourPicker, 0, 0);
        mMidnightAsZero = attrArray.getBoolean(R.styleable.TimePickerWheelsHourPicker_midnightHourAsZero, false);
        mLabelPattern = attrArray.getString(R.styleable.TimePickerWheelsHourPicker_labelPattern);
        if (TextUtils.isEmpty(mLabelPattern)) {
            mLabelPattern = "%02d";
        }
        initValues();
    }

    private void initValues() {
        String[] labels = new String[HOURS_COUNT];
        int start = mMidnightAsZero ? 0 : 1;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < HOURS_COUNT; i++) {
            labels[i] = String.format(locale, mLabelPattern, start + i);
        }
        setSaveFromParentEnabled(false);
        setSaveEnabled(true);
        setMinValue(start);
        setMaxValue(start + HOURS_COUNT - 1);
        setDisplayedValues(labels);
        setWrapSelectorWheel(true);
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setValue(start);
    }

}
