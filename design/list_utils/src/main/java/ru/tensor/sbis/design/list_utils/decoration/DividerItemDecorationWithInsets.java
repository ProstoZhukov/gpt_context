package ru.tensor.sbis.design.list_utils.decoration;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author am.boldinov
 */
@SuppressWarnings("JavaDoc")
public abstract class DividerItemDecorationWithInsets extends RecyclerView.ItemDecoration {

    private static final int START_INSET_DEFAULT_VALUE = 0;
    private static final int END_INSET_DEFAULT_VALUE = 0;

    private int mStartInset = START_INSET_DEFAULT_VALUE;
    private int mEndInset = END_INSET_DEFAULT_VALUE;

    /** @SelfDocumented */
    public int getStartInset() {
        return mStartInset;
    }

    /** @SelfDocumented */
    public void setStartInset(int startInset) {
        mStartInset = startInset;
        assertNegativeStartInsetValue();
    }

    /** @SelfDocumented */
    public int getEndInset() {
        return mEndInset;
    }

    /** @SelfDocumented */
    public void setEndInset(int endInset) {
        mEndInset = endInset;
        assertNegativeEndInsetValue();
    }

    public DividerItemDecorationWithInsets() {}

    public DividerItemDecorationWithInsets(int startInset, int endInset) {
        setStartInset(startInset);
        setEndInset(endInset);
    }

    /** @SelfDocumented */
    protected void assertNegativeStartInsetValue() {
        if (mStartInset < 0) {
            throw new RuntimeException("Start inset value is negative!");
        }
    }

    /** @SelfDocumented */
    protected void assertNegativeEndInsetValue() {
        if (mEndInset < 0) {
            throw new RuntimeException("End inset value is negative!");
        }
    }

    /** @SelfDocumented */
    protected void assertNegativeInsetValue() {
        assertNegativeStartInsetValue();
        assertNegativeEndInsetValue();
    }

}
