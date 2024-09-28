package ru.tensor.sbis.design.view_ext.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import timber.log.Timber;

/**
 * Created by vitalydemidov on 02/06/16.
 * <p>
 * This class fixes some bug in androidx.viewpager.widget.ViewPager (see android's report: https://code.google.com/p/android/issues/detail?id=18990)
 */
public class ViewPagerFixed extends ViewPager {

    private boolean isSwipeEnabled = true;

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return isSwipeEnabled && super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            Timber.d(ex);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return isSwipeEnabled && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            Timber.d(ex);
        }
        return false;
    }

    /**
     * Включает/отключает реакцию на свайп панелей (например, на экране с вкладками). 
     * По умолчанию свайп включен
     */
    public void setSwipeEnabled(boolean swipeEnabled) {
        isSwipeEnabled = swipeEnabled;
    }
}