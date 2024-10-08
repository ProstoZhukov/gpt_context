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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import ru.tensor.sbis.design.design_dialogs.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import ru.tensor.sbis.design.utils.viewdraghelper.ViewDragHelper;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

//TODO Найти более правильное решение проблемы https://online.sbis.ru/opendoc.html?guid=9f570ed4-c71d-45a9-a5c9-f2be8e0b571e

/**
 * Копия класса {@link BottomSheetBehavior}, использующая колбек
 * ViewDragHelper.Callback, отличающийся от стандартного тем, что не приводит к закрытию BottomSheet'а
 * в случае, когда скорость движения пальца при отрыве от экрана равна нулю
 */
@SuppressWarnings({"unused", "deprecation", "JavaDoc"})
public class CustomBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    /**
     * Callback for monitoring events about bottom sheets.
     */
    public abstract static class BottomSheetCallback {

        /**
         * Called when the bottom sheet changes its state.
         *
         * @param bottomSheet The bottom sheet view.
         * @param newState    The new state. This will be one of {@link #STATE_DRAGGING},
         *                    {@link #STATE_SETTLING}, {@link #STATE_EXPANDED},
         *                    {@link #STATE_COLLAPSED}, or {@link #STATE_HIDDEN}.
         */
        public abstract void onStateChanged(@NonNull View bottomSheet, @State int newState);

        /**
         * Called when the bottom sheet is being dragged.
         *
         * @param bottomSheet The bottom sheet view.
         * @param slideOffset The new offset of this bottom sheet within [-1,1] range. Offset
         *                    increases as this bottom sheet is moving upward. From 0 to 1 the sheet
         *                    is between collapsed and expanded states and from -1 to 0 it is
         *                    between hidden and collapsed states.
         */
        public abstract void onSlide(@NonNull View bottomSheet, float slideOffset);
    }

    /**
     * The bottom sheet is dragging.
     */
    public static final int STATE_DRAGGING = 1;

    /**
     * The bottom sheet is settling.
     */
    public static final int STATE_SETTLING = 2;

    /**
     * The bottom sheet is expanded.
     */
    public static final int STATE_EXPANDED = 3;

    /**
     * The bottom sheet is collapsed.
     */
    public static final int STATE_COLLAPSED = 4;

    /**
     * The bottom sheet is hidden.
     */
    public static final int STATE_HIDDEN = 5;

    /** @hide */
    @SuppressWarnings("JavaDoc")
    @RestrictTo(LIBRARY_GROUP)
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {}

    /**
     * Peek at the 16:9 ratio keyline of its parent.
     *
     * <p>This can be used as a parameter for {@link #setPeekHeight(int)}.
     * {@link #getPeekHeight()} will return this when the value is set.</p>
     */
    public static final int PEEK_HEIGHT_AUTO = -1;

    private static final float HIDE_SLIDE_OFFSET_THRESHOLD = 0.5f;

    private static final float HIDE_VELOCITY_FRACTION_THRESHOLD = 0.15f;

    private static final float HIDE_FRICTION = 0.1f;

    private float mMaximumVelocity;

    private int mPeekHeight;

    private boolean mPeekHeightAuto;

    private int mPeekHeightMin;

    int mMinOffset;

    int mMaxOffset;

    boolean mHideable;

    private boolean mSkipCollapsed;

    @State
    int mState = STATE_COLLAPSED;

    @State
    private int mLastTargetState = STATE_COLLAPSED;

    ViewDragHelper mViewDragHelper;

    private boolean mIgnoreEvents;

    private int mLastNestedScrollDy;

    private boolean mNestedScrolled;

    int mParentHeight;

    WeakReference<V> mViewRef;

    ArrayList<WeakReference<View>> mNestedScrollingChildRef = new ArrayList<>();

    private BottomSheetCallback mCallback;

    private VelocityTracker mVelocityTracker;

    int mActivePointerId;

    private int mInitialY;

    boolean mTouchingScrollingChild;

    private int mMaxSettleAnimationDuration;

    @Px
    private int hideScrollAmountThreshold;

    /**
     * Default constructor for instantiating CustomBottomSheetBehavior.
     */
    public CustomBottomSheetBehavior() {
    }

    /**
     * Default constructor for inflating CustomBottomSheetBehavior from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomBottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(R.styleable.CustomBottomSheetBehavior_Layout_behavior_peekHeight);
        if (value != null && value.data == PEEK_HEIGHT_AUTO) {
            setPeekHeight(value.data);
        } else {
            setPeekHeight(a.getDimensionPixelSize(
                    R.styleable.CustomBottomSheetBehavior_Layout_behavior_peekHeight, PEEK_HEIGHT_AUTO));
        }
        setHideable(a.getBoolean(R.styleable.CustomBottomSheetBehavior_Layout_behavior_hideable, false));
        setSkipCollapsed(a.getBoolean(R.styleable.CustomBottomSheetBehavior_Layout_behavior_skipCollapsed,
                false));
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        hideScrollAmountThreshold = context.getResources()
                .getDimensionPixelSize(R.dimen.custom_bottom_sheet_behavior_hide_scroll_amount_threshold);
    }

    /**
     * Задаёт максимальное время анимации показа/скрытия содержимого.
     *
     * @param maxDuration предельная длительность анимации (мс)
     */
    public void setMaxSettleAnimationDuration(int maxDuration) {
        mMaxSettleAnimationDuration = maxDuration;
        if (mViewDragHelper != null) {
            mViewDragHelper.setMaxSettleDuration(maxDuration);
        }
    }

    @Override
    public Parcelable onSaveInstanceState(@NotNull CoordinatorLayout parent, @NotNull V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), mState);
    }

    @Override
    public void onRestoreInstanceState(@NotNull CoordinatorLayout parent, @NotNull V child, @NotNull Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, Objects.requireNonNull(ss.getSuperState()));
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED;
        } else {
            mState = ss.state;
        }
    }

    @Override
    public boolean onLayoutChild(@NotNull CoordinatorLayout parent, @NotNull V child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }
        int savedTop = child.getTop();
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection);
        // Offset the bottom sheet
        mParentHeight = parent.getHeight();
        int peekHeight;
        if (mPeekHeightAuto) {
            if (mPeekHeightMin == 0) {
                mPeekHeightMin = parent.getResources().getDimensionPixelSize(
                        R.dimen.custom_bottom_sheet_peek_height_min);
            }
            peekHeight = Math.max(mPeekHeightMin, mParentHeight - parent.getWidth() * 9 / 16);
        } else {
            peekHeight = mPeekHeight;
        }
        int oldMinOffset = mMinOffset;
        mMinOffset = Math.max(0, mParentHeight - child.getHeight());
        mMaxOffset = Math.max(mParentHeight - peekHeight, mMinOffset);

        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback);
            if (mMaxSettleAnimationDuration > 0) {
                mViewDragHelper.setMaxSettleDuration(mMaxSettleAnimationDuration);
            }
        }

        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset);
        } else if (mHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, mParentHeight);
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset);
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            if (mMinOffset != oldMinOffset) {
                mViewDragHelper.abort();
                ViewCompat.offsetTopAndBottom(child, mMinOffset);
            } else {
                ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
            }
        }

        mViewRef = new WeakReference<>(child);
        mNestedScrollingChildRef.clear();
        findScrollingChild(child);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NotNull CoordinatorLayout parent, V child, @NotNull MotionEvent event) {
        if (!child.isShown()) {
            mIgnoreEvents = true;
            return false;
        }
        int action = event.getActionMasked();
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchingScrollingChild = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                int initialX = (int) event.getX();
                mInitialY = (int) event.getY();
                for (WeakReference<View> weak :
                        mNestedScrollingChildRef) {
                    View scroll = weak != null
                            ? weak.get() : null;
                    if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                        mActivePointerId = event.getPointerId(event.getActionIndex());
                        mTouchingScrollingChild = true;
                        break;
                    }
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, initialX, mInitialY);
                break;
        }
        if (!mIgnoreEvents && mViewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        for (WeakReference<View> weak :
                mNestedScrollingChildRef) {
            View scroll = weak != null
                    ? weak.get() : null;
            if(scroll != null && parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY())){
                return false;
            }
        }
        return action == MotionEvent.ACTION_MOVE &&
                !mIgnoreEvents && mState != STATE_DRAGGING &&
                Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop();
    }

    @Override
    public boolean onTouchEvent(@NotNull CoordinatorLayout parent, V child, @NotNull MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = event.getActionMasked();
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        if (mViewDragHelper != null) {
            mViewDragHelper.processTouchEvent(event);
        }
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
            if (Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop()) {
                mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            }
        }
        return !mIgnoreEvents;
    }

    @Override
    public boolean onStartNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child,
                                       @NotNull View directTargetChild, @NotNull View target, int nestedScrollAxes) {
        mLastNestedScrollDy = 0;
        mNestedScrolled = false;
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, int dx,
                                  int dy, int @NotNull [] consumed) {
        boolean flag = false;
        for (WeakReference<View> weak :
                mNestedScrollingChildRef) {
            View scroll = weak != null ? weak.get() : null;
            if(!flag && target == scroll) flag = true;
        }

        if (!flag) {
            return;
        }
        int currentTop = child.getTop();
        int newTop = currentTop - dy;
        if (dy > 0) { // Upward
            if (newTop < mMinOffset) {
                consumed[1] = currentTop - mMinOffset;
                ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                setStateInternal(STATE_EXPANDED);
            } else {
                consumed[1] = dy;
                ViewCompat.offsetTopAndBottom(child, -dy);
                setStateInternal(STATE_DRAGGING);
            }
        } else if (dy < 0) { // Downward
            if (!target.canScrollVertically(-1)) {
                if (newTop <= mMaxOffset || mHideable) {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                } else {
                    consumed[1] = currentTop - mMaxOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(STATE_COLLAPSED);
                }
            }
        }
        dispatchOnSlide(child.getTop());
        mLastNestedScrollDy = dy;
        mNestedScrolled = true;
    }

    @Override
    public void onStopNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, V child, @NotNull View target) {
        if (child.getTop() == mMinOffset) {
            setStateInternal(STATE_EXPANDED);
            return;
        }
        boolean flag = false;
        for (WeakReference<View> weak :
                mNestedScrollingChildRef) {
            View scroll = weak != null ? weak.get() : null;
            if(!flag && target == scroll) flag = true;
        }
        if (mNestedScrollingChildRef.size() == 0 || !flag
                || !mNestedScrolled) {
            return;
        }
        int top;
        if (mLastNestedScrollDy > 0) {
            top = mMinOffset;
            mLastTargetState = STATE_EXPANDED;
        } else if (mHideable) {
            if (shouldHide(child, getYVelocity())) {
                top = mParentHeight;
                mLastTargetState = STATE_HIDDEN;
            } else {
                top = mMinOffset;
                mLastTargetState = STATE_EXPANDED;
            }
        } else if (mLastNestedScrollDy == 0) {
            int currentTop = child.getTop();
            if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                top = mMinOffset;
                mLastTargetState = STATE_EXPANDED;
            } else {
                top = mMaxOffset;
                mLastTargetState = STATE_COLLAPSED;
            }
        } else {
            top = mMaxOffset;
            mLastTargetState = STATE_COLLAPSED;
        }
        if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, mLastTargetState));
        } else {
            setStateInternal(mLastTargetState);
        }
        mNestedScrolled = false;
    }

    @Override
    public boolean onNestedPreFling(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target,
                                    float velocityX, float velocityY) {

        for (WeakReference<View> weak :
                mNestedScrollingChildRef) {
            View scroll = weak != null ? weak.get() : null;
            if(target == scroll)
                return (mState != STATE_EXPANDED ||
                        super.onNestedPreFling(coordinatorLayout, child, target,
                                velocityX, velocityY));
        }
        return false;
    }

    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels, or
     *                   {@link #PEEK_HEIGHT_AUTO} to configure the sheet to peek automatically
     *                   at 16:9 ratio keyline.
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layout_behavior_peekHeight
     */
    public final void setPeekHeight(int peekHeight) {
        boolean layout = false;
        if (peekHeight == PEEK_HEIGHT_AUTO) {
            if (!mPeekHeightAuto) {
                mPeekHeightAuto = true;
                layout = true;
            }
        } else if (mPeekHeightAuto || mPeekHeight != peekHeight) {
            mPeekHeightAuto = false;
            mPeekHeight = Math.max(0, peekHeight);
            mMaxOffset = mParentHeight - peekHeight;
            layout = true;
        }
        if (layout && mState == STATE_COLLAPSED && mViewRef != null) {
            V view = mViewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    /**
     * Gets the height of the bottom sheet when it is collapsed.
     *
     * @return The height of the collapsed bottom sheet in pixels, or {@link #PEEK_HEIGHT_AUTO}
     *         if the sheet is configured to peek automatically at 16:9 ratio keyline
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layout_behavior_peekHeight
     */
    public final int getPeekHeight() {
        return mPeekHeightAuto ? PEEK_HEIGHT_AUTO : mPeekHeight;
    }

    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable {@code true} to make this bottom sheet hideable.
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layout_behavior_hideable
     */
    public void setHideable(boolean hideable) {
        mHideable = hideable;
    }

    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return {@code true} if this bottom sheet can hide.
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layoutt_behavior_hideable
     */
    public boolean isHideable() {
        return mHideable;
    }

    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once. Setting this to true has no effect unless the sheet is hideable.
     *
     * @param skipCollapsed True if the bottom sheet should skip the collapsed state.
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    public void setSkipCollapsed(boolean skipCollapsed) {
        mSkipCollapsed = skipCollapsed;
    }

    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once.
     *
     * @return Whether the bottom sheet should skip the collapsed state.
     * @attr ref ru.tensor.sbis.design.design_dialogs.R.styleable#CustomBottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    public boolean getSkipCollapsed() {
        return mSkipCollapsed;
    }

    /**
     * Sets a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    public void setBottomSheetCallback(BottomSheetCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of {@link #STATE_COLLAPSED}, {@link #STATE_EXPANDED}, or
     *              {@link #STATE_HIDDEN}.
     */
    public final void setState(final @State int state) {
        if (state == mState) {
            return;
        }
        if (mViewRef == null) {
            // The view is not laid out yet; modify mState and let onLayoutChild handle it later
            if (state == STATE_COLLAPSED || state == STATE_EXPANDED ||
                    (mHideable && state == STATE_HIDDEN)) {
                mState = state;
            }
            return;
        }
        final V child = mViewRef.get();
        if (child == null) {
            return;
        }
        // Start the animation; wait until a pending layout if there is one.
        ViewParent parent = child.getParent();
        if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
            //noinspection Convert2Lambda
            child.post(new Runnable() {
                @Override
                public void run() {
                    startSettlingAnimation(child, state);
                }
            });
        } else {
            startSettlingAnimation(child, state);
        }
    }

    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of {@link #STATE_EXPANDED}, {@link #STATE_COLLAPSED}, {@link #STATE_DRAGGING},
     * {@link #STATE_SETTLING}, and {@link #STATE_HIDDEN}.
     */
    @State
    public final int getState() {
        return mState;
    }

    /**
     * Возвращает актуальное состояние BottomSheet, либо состояние, в которое он перейдёт по
     * окончании анимации
     */
    @State
    public final int getLastTargetState() {
        return mLastTargetState;
    }

    void setStateInternal(@State int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        View bottomSheet = mViewRef.get();
        if (bottomSheet == null) {
            return;
        }
        if (mCallback != null) {
            mCallback.onStateChanged(bottomSheet, state);
        }
    }

    private void reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    boolean shouldHide(View child, float yvel) {
        if (getSlideOffset(child.getTop()) < HIDE_SLIDE_OFFSET_THRESHOLD) {
            return true;
        }
        return (mLastNestedScrollDy == 0 || -mLastNestedScrollDy > hideScrollAmountThreshold) &&
                yvel >= mMaximumVelocity * HIDE_VELOCITY_FRACTION_THRESHOLD;
    }

    @VisibleForTesting
    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            mNestedScrollingChildRef.add(new WeakReference<>(view));
            return null;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    public void setNestedScrollingChild(@NonNull View view) {
        mNestedScrollingChildRef.add(new WeakReference<>(view));
    }

    private float getYVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        return mVelocityTracker.getYVelocity(mActivePointerId);
    }

    void startSettlingAnimation(View child, int state) {
        int top;
        if (state == STATE_COLLAPSED) {
            top = mMaxOffset;
        } else if (state == STATE_EXPANDED) {
            top = mMinOffset;
        } else if (mHideable && state == STATE_HIDDEN) {
            top = mParentHeight;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        }
        if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
        } else {
            setStateInternal(state);
        }
    }

    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(@NotNull View child, int pointerId) {
            if (mState == STATE_DRAGGING) {
                return false;
            }
            if (mTouchingScrollingChild) {
                return false;
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                for (WeakReference<View> weak :
                        mNestedScrollingChildRef) {
                    View scroll = weak != null ? weak.get() : null;
                    if (scroll != null && scroll.canScrollVertically(-1)) {
                        // Let the content scroll up
                        return false;
                    }
                }

            }
            return mViewRef != null && mViewRef.get() == child;
        }

        @Override
        public void onViewPositionChanged(@NotNull View changedView, int left, int top, int dx, int dy) {
            dispatchOnSlide(top);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING);
            }
        }

        @Override
        public void onViewReleased(@NotNull View releasedChild, float xvel, float yvel) {
            int top;
            if (yvel < 0) { // Moving up or stopped motion
                top = mMinOffset;
                mLastTargetState = STATE_EXPANDED;
            } else if (mHideable) {
                if (shouldHide(releasedChild, yvel)) {
                    top = mParentHeight;
                    mLastTargetState = STATE_HIDDEN;
                } else {
                    top = mMinOffset;
                    mLastTargetState = STATE_EXPANDED;
                }
            } else if (yvel == 0.f) {
                int currentTop = releasedChild.getTop();
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mMinOffset;
                    mLastTargetState = STATE_EXPANDED;
                } else {
                    top = mMaxOffset;
                    mLastTargetState = STATE_COLLAPSED;
                }
            } else {
                top = mMaxOffset;
                mLastTargetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild,
                        new SettleRunnable(releasedChild, mLastTargetState));
            } else {
                setStateInternal(mLastTargetState);
            }
        }

        @Override
        public int clampViewPositionVertical(@NotNull View child, int top, int dy) {
            return MathUtils.clamp(top, mMinOffset, mHideable ? mParentHeight : mMaxOffset);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(@NotNull View child) {
            if (mHideable) {
                return mParentHeight - mMinOffset;
            } else {
                return mMaxOffset - mMinOffset;
            }
        }
    };

    void dispatchOnSlide(int top) {
        View bottomSheet = mViewRef.get();
        if (bottomSheet != null && mCallback != null) {
            mCallback.onSlide(bottomSheet, getSlideOffset(top));
        }
    }

    @VisibleForTesting
    int getPeekHeightMin() {
        return mPeekHeightMin;
    }

    @Px
    private float getSlideOffset(@Px int top) {
        if (top > mMaxOffset) {
            return (float) (mMaxOffset - top) / (mParentHeight - mMaxOffset);
        } else {
            return (float) (mMaxOffset - top) / ((mMaxOffset - mMinOffset));
        }
    }

    private class SettleRunnable implements Runnable {

        private final View mView;

        @State
        private final int mTargetState;

        SettleRunnable(View view, @State int targetState) {
            mView = view;
            mTargetState = targetState;
        }

        @Override
        public void run() {
            if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this);
            } else {
                setStateInternal(mTargetState);
            }
        }
    }

    protected static class SavedState extends AbsSavedState {
        @State
        final int state;

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            //noinspection ResourceType
            state = source.readInt();
        }

        public SavedState(Parcelable superState, @State int state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * A utility function to get the {@link CustomBottomSheetBehavior} associated with the {@code view}.
     *
     * @param view The {@link View} with {@link CustomBottomSheetBehavior}.
     * @return The {@link CustomBottomSheetBehavior} associated with the {@code view}.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> CustomBottomSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        @SuppressWarnings("rawtypes") CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof CustomBottomSheetBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with CustomBottomSheetBehavior");
        }
        return (CustomBottomSheetBehavior<V>) behavior;
    }

}