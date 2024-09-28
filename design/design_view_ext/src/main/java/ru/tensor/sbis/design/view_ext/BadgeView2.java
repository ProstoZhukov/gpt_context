package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by vitalydemidov on 24/02/16.
 */
@SuppressWarnings({"FieldCanBeLocal", "JavaDoc"})
public class BadgeView2 extends AppCompatTextView {

    private int mValue;
    private final int mMinValue = 1;
    private final int mMaxValue = 1000;
    private final int mMaxValueForDisplay = 99;

    public BadgeView2(Context context) {
        this(context, null);
    }

    public BadgeView2(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setValue(0);
    }

    /** @SelfDocumented */
    @SuppressWarnings("unused")
    public int getValue() {
        return mValue;
    }

    /** @SelfDocumented */
    public void setValue(int value) {
        mValue = value;
        if (value < mMinValue) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
        setText(getBadgeString());
    }

    private String getBadgeString() {
        return mValue >= mMaxValue ? String.valueOf(mMaxValueForDisplay).concat("+") : String.valueOf(mValue);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE && mValue < mMinValue) {
            return;
        }
        super.setVisibility(visibility);
    }
}
