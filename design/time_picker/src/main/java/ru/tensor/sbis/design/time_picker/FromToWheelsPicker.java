package ru.tensor.sbis.design.time_picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.tensor.sbis.design.theme.global_variables.TextColor;

/**
 * Компонент для выбора периода времени
 * <p>
 * Легаси код
 *
 * @author aa.mironychev
 */
public class FromToWheelsPicker extends LinearLayout {

    private WheelsHourPicker mFromHourPicker;
    private WheelsHourPicker mToHourPicker;

    /** @SelfDocumented */
    public FromToWheelsPicker(Context context) {
        super(context);
        init();
    }

    /** @SelfDocumented */
    public FromToWheelsPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /** @SelfDocumented */
    public FromToWheelsPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init() {
        inflate(getContext(), R.layout.time_picker_from_to_hours_picker, this);
        setOrientation(HORIZONTAL);
        mFromHourPicker = findViewById(R.id.time_picker_from_hour_picker);
        mToHourPicker = findViewById(R.id.time_picker_to_hour_picker);
    }

    protected void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.time_picker_from_to_hours_picker, this);
        setOrientation(HORIZONTAL);

        mFromHourPicker = findViewById(R.id.time_picker_from_hour_picker);
        mToHourPicker = findViewById(R.id.time_picker_to_hour_picker);
        TextView divider = findViewById(R.id.time_picker_textView);

        TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.TimePickerFromToWheelsPicker, 0, 0);

        int dividerColor;
        int dividerBackgroundColor;
        try {
            dividerColor = attrArray.getColor(R.styleable.TimePickerFromToWheelsPicker_dividerColor, TextColor.DEFAULT.getValue(getContext()));
            dividerBackgroundColor = attrArray.getColor(R.styleable.TimePickerFromToWheelsPicker_dividerBackgroundColor, Color.TRANSPARENT);
        } finally {
            attrArray.recycle();
        }

        divider.setBackgroundColor(dividerBackgroundColor);
        divider.setTextColor(dividerColor);
    }

    /**
     * Метод для получения ограничения "от".
     *
     * @return значение "от".
     */
    public int getHoursFrom() {
        return mFromHourPicker.getValue();
    }

    /**
     * Метод для получения ограничения "от".
     *
     * @return значение "до".
     */
    public int getHoursTo() {
        return mToHourPicker.getValue();
    }

    /**
     * Метод для установки ограничения "от".
     *
     * @param value часы.
     */
    public void setHoursFrom(int value) {
        mFromHourPicker.setValue(value);
    }

    /**
     * Метод для установки ограничения "до".
     *
     * @param value часы.
     */
    public void setHoursTo(int value) {
        mToHourPicker.setValue(value);
    }

    /**
     * Установить слушатель изменения значения начала временного интервала
     */
    public void setHoursFromPickerOnValueChangedListener(@Nullable NumberPicker.OnValueChangeListener listener) {
        mFromHourPicker.setOnValueChangedListener(listener);
    }

    /**
     * Установить слушатель изменения значения конца временного интервала
     */
    public void setHoursToPickerOnValueChangedListener(@Nullable NumberPicker.OnValueChangeListener listener) {
        mToHourPicker.setOnValueChangedListener(listener);
    }
}
