package ru.tensor.sbis.design.time_picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Locale;

/**
 * Тайм пикер в виде колеса
 *
 * @author im.zheglov
 */
@SuppressWarnings("unused")
@SuppressLint("ClickableViewAccessibility")
public class WheelsTimePicker extends LinearLayout {

    protected NumberPickerWithoutDividers mHoursPicker;
    protected NumberPickerWithoutDividers mMinutesPicker;

    protected int mWheelsBackground;
    /**
     * Если true, то барабан минут становится неактивным
     */
    protected boolean mDisableMinutes;

    /**
     * Формат полночи: если true - то 0 часов, если false - то 24 часа
     */
    protected boolean mMidnightAsZero = false;

    /**
     * @SelfDocumented
     */
    public WheelsTimePicker(Context context) {
        super(context);
        init(null);
    }

    /**
     * @SelfDocumented
     */
    public WheelsTimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * @SelfDocumented
     */
    public WheelsTimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(@Nullable AttributeSet attrs) {
        View root = inflate(getContext(), R.layout.time_picker_layout, this);
        mHoursPicker = root.findViewById(R.id.time_picker_hours_picker);
        mMinutesPicker = root.findViewById(R.id.time_picker_minutes_picker);

        TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimePickerWheelsTimePicker, 0, 0);
        mWheelsBackground = attrArray.getColor(R.styleable.TimePickerWheelsTimePicker_wheelBackground, ContextCompat.getColor(getContext(), android.R.color.white));
        mMidnightAsZero = attrArray.getBoolean(R.styleable.TimePickerWheelsTimePicker_midnightAsZero, false);
        mDisableMinutes = attrArray.getBoolean(R.styleable.TimePickerWheelsTimePicker_disableMinutes, false);
        root.setBackgroundColor(mWheelsBackground);
        initializePickers();
    }

    /**
     * Метод для установки часов.
     *
     * @param hours часы.
     */
    public void setHours(int hours) {
        mHoursPicker.setValue(hours);
    }

    /**
     * Метод для установки минут.
     *
     * @param minutes минуты.
     */
    public void setMinutes(int minutes) {
        mMinutesPicker.setValue(minutes);
    }

    /**
     * Метод для получения часов.
     *
     * @return значение времени (часы).
     */
    public int getHours() {
        return mHoursPicker.getValue();
    }

    /**
     * Метод для получения минут.
     *
     * @return значение времени (минуты).
     */
    public int getMinutes() {
        return mMinutesPicker.getValue();
    }

    protected void initializePickers() {
        String[] hoursStrings = new String[24];
        String[] minutesStrings = new String[60];

        int hoursStart = mMidnightAsZero ? 0 : 1;
        int hoursEnd = mMidnightAsZero ? 23 : 24;

        Locale locale = Locale.getDefault();
        for (int i = 0; i < 24; i++) {
            hoursStrings[i] = String.format(locale, "%02d", i + hoursStart);
        }
        for (int i = 0; i < 60; i++) {
            minutesStrings[i] = String.format(locale, "%02d", i);
        }

        mHoursPicker.setSaveFromParentEnabled(false);
        mHoursPicker.setSaveEnabled(true);
        mHoursPicker.setMinValue(hoursStart);
        mHoursPicker.setMaxValue(hoursEnd);
        mHoursPicker.setDisplayedValues(hoursStrings);
        mHoursPicker.setWrapSelectorWheel(true);
        mHoursPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mHoursPicker.setValue(hoursStart);

        mMinutesPicker.setSaveFromParentEnabled(false);
        mMinutesPicker.setSaveEnabled(true);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(59);
        mMinutesPicker.setDisplayedValues(minutesStrings);
        mMinutesPicker.setWrapSelectorWheel(true);
        mMinutesPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinutesPicker.setEnabled(!mDisableMinutes);
    }

    /**
     * Установить TouchListener на барабаны
     */
    public void setPickersTouchListener(OnTouchListener listener) {
        mHoursPicker.setOnTouchListener(listener);
        mMinutesPicker.setOnTouchListener(listener);
    }

    /**
     * Установить TouchListener на барабан часов
     */
    public void setHoursPickerTouchListener(OnTouchListener listener) {
        mHoursPicker.setOnTouchListener(listener);
    }

    /**
     * Установить TouchListener на барабан минут
     */
    public void setMinutesPickerTouchListener(OnTouchListener listener) {
        mMinutesPicker.setOnTouchListener(listener);
    }

    /**
     * Установить Listener на изменение значений обоих барабанов
     */
    public void setPickersValueChangeListener(NumberPicker.OnValueChangeListener listener) {
        setHoursValueChangeListener(listener);
        setMinutesValueChangeListener(listener);
    }

    /**
     * Сделать барабан минут активным/неактивным
     *
     * @param state состояние
     */
    public void setMinutesEnabled(boolean state) {
        mMinutesPicker.setEnabled(state);
    }

    /**
     * Установить Listener на изменение значения барабана часов
     */
    public void setHoursValueChangeListener(NumberPicker.OnValueChangeListener listener) {
        mHoursPicker.setOnValueChangedListener(listener);
    }

    /**
     * Установить Listener на изменение значения барабана минут
     */
    public void setMinutesValueChangeListener(NumberPicker.OnValueChangeListener listener) {
        mMinutesPicker.setOnValueChangedListener(listener);
    }
}
