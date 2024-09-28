package ru.tensor.sbis.design.swipeback;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.tensor.sbis.design.swipeback.UtilsKt.tryFindViewAt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.EnumSet;

import ru.tensor.sbis.design.utils.DragDirection;
import ru.tensor.sbis.design.utils.DraggableView;


/**
 * Swipe or Pull to finish a Activity or pop the top state off of the Fragment back stack.
 * <p/>
 * This layout must be a root layout and contains only one direct child view.
 * <p/>
 * The activity must use a theme that with translucent style.
 * <style name="Theme.Swipe.Back" parent="AppTheme">
 * <item name="android:windowIsTranslucent">true</item>
 * <item name="android:windowBackground">@android:color/transparent</item>
 * </style>
 * <p/>
 */
@SuppressWarnings({"JavaDoc", "FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class SwipeBackLayout extends ViewGroup implements DraggableView {

    private static final double AUTO_FINISHED_SPEED_LIMIT = 800.0;

    private static final float BACK_FACTOR = 0.5f;

    private static final int TOUCH_SLOP_FACTOR = 8;

    private final ViewDragHelper mViewDragHelper;

    private DragDirectMode mDragDirectMode = DragDirectMode.DIRECTION_FROM_EDGE;

    private DragEdge mDragEdge = DragEdge.NONE;

    /**
     * @SelfDocumented
     */
    boolean mIsSwipeOnlyOnePointerEnabled;

    @Nullable
    private View mTarget;

    @Nullable
    private View mScrollChild;

    private boolean mWasChildScrollableInSwipeDirection = false;

    private int mVerticalDragRange;

    private int mHorizontalDragRange;

    private int mDraggingState = ViewDragHelper.STATE_IDLE;

    private int mDraggingOffset;

    /**
     * Whether allow to pull this layout.
     */
    private boolean mEnablePullToBack = true;

    /**
     * the anchor of calling finish.
     */
    private float mFinishAnchor;

    private boolean mEnableFlingBack = true;

    private boolean mIsSwipeBackAvailable = true;

    private final int mTouchSlop;

    @Nullable
    private SwipeBackListener mSwipeBackListener;

    private float mDownX, mDownY;
    private final Rect localRect = new Rect();

    private boolean mIsLayoutPostponed;

    private boolean mIsNestedSwipeBackSupported;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * TOUCH_SLOP_FACTOR;
        checkDraggable();
    }

    /**
     * @SelfDocumented
     */
    public void setOnSwipeBackListener(@Nullable SwipeBackListener listener) {
        mSwipeBackListener = listener;
    }

    /**
     * @SelfDocumented
     */
    public void setDragEdge(@NonNull DragEdge dragEdge) {
        mDragEdge = dragEdge;
    }

    /**
     * Установка типа свайпа
     */
    public void setDragDirectMode(@NonNull DragDirectMode dragDirectMode) {
        mDragDirectMode = dragDirectMode;
        if (dragDirectMode == DragDirectMode.VERTICAL) {
            mDragEdge = DragEdge.TOP;
        } else if (dragDirectMode == DragDirectMode.HORIZONTAL) {
            mDragEdge = DragEdge.LEFT;
        }
    }

    /**
     * Включить swipe back, только если жест смахивания происходит одним указателем (пальцем)
     * Требуется, если на экране есть жесты по 2-м указателем, например, "Pinch open"
     */
    public void setSwipeOnlyOnePointerEnabled(boolean isSwipeOnlyOnePointerEnabled) {
        mIsSwipeOnlyOnePointerEnabled = isSwipeOnlyOnePointerEnabled;
    }

    /**
     * Set the anchor of calling finish.
     *
     * @param offset
     */
    public void setFinishAnchor(float offset) {
        mFinishAnchor = offset;
    }

    /**
     * Whether allow to finish activity by fling the layout.
     *
     * @param enabled
     */
    public void setEnableFlingBack(boolean enabled) {
        mEnableFlingBack = enabled;
    }

    /**
     * Изменить доступность свайп-бэка.
     * По-умолчанию доступен.
     * Метод необходим для регулирования возможности свайпа во время отображения view.
     *
     * @param isAvailable true, если свайп-бэк доступен для пользователя.
     */
    public void changeSwipeBackAvailability(boolean isAvailable) {
        mIsSwipeBackAvailable = isAvailable;
    }

    /**
     * Должен ли данный SwipeBackLayout, будучи вложенным в другой SwipeBackLayout, обрабатывать
     * жесты свайпа, имея приоритет над родительским экраном.
     */
    public void setSupportNestedSwipeBack(boolean isNestedSwipeBackSupported) {
        mIsNestedSwipeBackSupported = isNestedSwipeBackSupported;
    }

    private void checkDraggable() {
        setOnTouchListener(new OnTouchListener() {

            private float lastY;
            private float newY;
            private float offsetY;

            private float lastX;
            private float newX;
            private float offsetX;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = motionEvent.getY();
                        lastX = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newY = motionEvent.getY();
                        newX = motionEvent.getX();

                        offsetY = Math.abs(newY - lastY);
                        //lastY = newY;

                        offsetX = Math.abs(newX - lastX);
                        //lastX = newX;

                        switch (mDragEdge) {
                            case TOP:
                            case BOTTOM:
                                setEnablePullToBack(offsetY > mTouchSlop && offsetY > offsetX);
                            case LEFT:
                            case RIGHT:
                                setEnablePullToBack(offsetX > mTouchSlop && offsetX > offsetY);
                                break;
                        }
                        break;
                }
                return false;
            }

        });
    }

    /**
     * @SelfDocumented
     */
    public void setEnablePullToBack(boolean enabled) {
        mEnablePullToBack = enabled;
    }

    private void ensureTarget() {
        if (mTarget == null) {
            if (getChildCount() > 1) {
                throw new IllegalStateException("SwipeBackLayout must contains only one direct child");
            }
            mTarget = getChildAt(0);
        }
    }

    private void ensureScrollableChild(@NonNull MotionEvent event) {
        boolean checkIfScrollableHorizontally = mDragEdge == DragEdge.LEFT || mDragEdge == DragEdge.RIGHT;
        if (mScrollChild == null || !isViewScrollable(mScrollChild, checkIfScrollableHorizontally) ||
                !contains(mScrollChild, event.getRawX(), event.getRawY())) {
            mScrollChild = tryFindScrollableViewAt(
                    event.getRawX(),
                    event.getRawY(),
                    checkIfScrollableHorizontally);
        }
        if (mScrollChild == null) {
            mScrollChild = mTarget;
        }
    }

    /**
     * @SelfDocumented
     */
    protected boolean isOverlayTarget(@NonNull MotionEvent event, @NonNull DragDirectMode mode) {
        final View target = mTarget;
        if (target == null || !ViewCompat.isAttachedToWindow(target)) {
            return false;
        }
        int[] targetLocation = new int[2];
        target.getLocationOnScreen(targetLocation);
        int left = targetLocation[0];
        int right = targetLocation[0] + target.getMeasuredWidth();
        int top = targetLocation[1];
        int bottom = targetLocation[1] + target.getMeasuredHeight();
        int y = (int) event.getRawY();
        int x = (int) event.getRawX();

        int leftLimit = left;
        int bottomLimit = bottom;
        int topLimit = top;
        int rightLimit = right;

        if (mode == DragDirectMode.EDGE) {
            switch (mDragEdge) {
                case LEFT:
                    rightLimit = left + mTouchSlop;
                    break;
                case RIGHT:
                    leftLimit = right - mTouchSlop;
                    break;
                case TOP:
                    bottomLimit = top + mTouchSlop;
                    break;
                case BOTTOM:
                    topLimit = bottom - mTouchSlop;
                    break;
            }
        }

        return y > topLimit && y < bottomLimit && x > leftLimit && x < rightLimit;
    }

    /**
     * Пытается найти View, в пределах которого находится указанная точка, и который в данный
     * момент можно прокрутить
     */
    @Nullable
    private View tryFindScrollableViewAt(float x, float y, boolean checkIfScrollableHorizontally) {
        return tryFindViewAt(mTarget, x, y, localRect,
                v -> isViewScrollable(v, checkIfScrollableHorizontally));
    }

    @Nullable
    private View tryFindDraggableViewAt(float x, float y) {
        return tryFindViewAt(mTarget, x, y, localRect, v -> v instanceof DraggableView &&
                isViewDraggableInSwipeDirection((DraggableView) v));
    }

    private boolean isViewDraggableInSwipeDirection(@NonNull DraggableView view) {
        EnumSet<DragDirection> directions = view.getSupportedDragDirections();
        return mDragEdge == DragEdge.LEFT && directions.contains(DragDirection.RIGHT) ||
                mDragEdge == DragEdge.RIGHT && directions.contains(DragDirection.LEFT) ||
                mDragEdge == DragEdge.TOP && directions.contains(DragDirection.BOTTOM) ||
                mDragEdge == DragEdge.BOTTOM && directions.contains(DragDirection.TOP);
    }

    private boolean isViewScrollable(@NonNull View view, boolean checkIfScrollableHorizontally) {
        return checkIfScrollableHorizontally && (view.canScrollHorizontally(-1) || view.canScrollHorizontally(1)) ||
                !checkIfScrollableHorizontally && (view.canScrollVertically(-1) || view.canScrollVertically(1));
    }

    private boolean isChildScrollableInSwipeDirection() {
        if (mScrollChild == null) {
            return false;
        }
        boolean isDirectionHorizontal = mDragEdge == DragEdge.LEFT || mDragEdge == DragEdge.RIGHT;
        boolean isDirectionForward = mDragEdge == DragEdge.RIGHT || mDragEdge == DragEdge.BOTTOM;
        int scrollDirection = isDirectionForward ? 1 : -1;
        return isDirectionHorizontal && mScrollChild.canScrollHorizontally(scrollDirection) ||
                !isDirectionHorizontal && mScrollChild.canScrollVertically(scrollDirection);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mDraggingState == ViewDragHelper.STATE_DRAGGING) {
            mIsLayoutPostponed = true;
            return;
        }

        View child = getChildAt(0);
        if (child == null) {
            return;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + childWidth;
        int childBottom = childTop + childHeight;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new IllegalStateException("SwipeBackLayout must contains only one direct child.");
        }

        if (getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mVerticalDragRange = h;
        mHorizontalDragRange = w;

        switch (mDragEdge) {
            case TOP:
            case BOTTOM:
                mFinishAnchor = mFinishAnchor > 0 ? mFinishAnchor
                        : mVerticalDragRange * BACK_FACTOR;
                break;
            case LEFT:
            case RIGHT:
                mFinishAnchor = mFinishAnchor > 0 ? mFinishAnchor
                        : mHorizontalDragRange * BACK_FACTOR;
                break;
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private int getDragRange() {
        switch (mDragEdge) {
            case TOP:
            case BOTTOM:
                return mVerticalDragRange;
            case LEFT:
            case RIGHT:
                return mHorizontalDragRange;
            default:
                return mVerticalDragRange;
        }
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        boolean handled = false;
        ensureTarget();
        if (isEnabled() && (!mIsSwipeOnlyOnePointerEnabled || ev.getPointerCount() == 1)) {
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getRawX();
                    mDownY = ev.getRawY();
                    ensureScrollableChild(ev);
                    mWasChildScrollableInSwipeDirection = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mScrollChild != null && contains(mScrollChild, mDownX, mDownY)) {
                        float distanceX = Math.abs(ev.getRawX() - mDownX);
                        float distanceY = Math.abs(ev.getRawY() - mDownY);
                        if (mDragEdge == DragEdge.LEFT || mDragEdge == DragEdge.RIGHT || mDragDirectMode == DragDirectMode.HORIZONTAL) {
                            if (distanceX < mViewDragHelper.getTouchSlop() || distanceY > distanceX) {
                                return super.onInterceptTouchEvent(ev);
                            }
                        } else if (mDragEdge == DragEdge.TOP || mDragEdge == DragEdge.BOTTOM || mDragDirectMode == DragDirectMode.VERTICAL) {
                            if (distanceY < mViewDragHelper.getTouchSlop() || distanceX > distanceY) {
                                return super.onInterceptTouchEvent(ev);
                            }
                        }
                    }
                    if (isChildScrollableInSwipeDirection()) {
                        mWasChildScrollableInSwipeDirection = true;
                    }
                    break;
            }
            View draggableView = tryFindDraggableViewAt(ev.getRawX(), ev.getRawY());
            handled = (draggableView == null && isOverlayTarget(ev, mDragDirectMode) ||
                    /*
                    Если совершается жест в месте расположения View, который можно двигать в
                    направлении свайпбэка, то разрешаем свайпбэк только от края.
                     */
                    draggableView != null && !(draggableView instanceof SwipeBackLayout)
                            && isOverlayTarget(ev, DragDirectMode.EDGE))
                    && mViewDragHelper.shouldInterceptTouchEvent(ev);
        } else {
            mViewDragHelper.cancel();
        }
        return handled || super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isOverlayTarget(event, mDragDirectMode) || isMoving()) {
            mViewDragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && mDragEdge != DragEdge.NONE;
    }

    @NonNull
    @Override
    public EnumSet<DragDirection> getSupportedDragDirections() {
        if (mIsNestedSwipeBackSupported && isEnabled()) {
            DragDirection direction = null;
            switch (mDragEdge) {
                case LEFT:
                    direction = DragDirection.RIGHT;
                    break;
                case TOP:
                    direction = DragDirection.BOTTOM;
                    break;
                case RIGHT:
                    direction = DragDirection.LEFT;
                    break;
                case BOTTOM:
                    direction = DragDirection.TOP;
                    break;
            }
            if (direction != null) {
                return EnumSet.of(direction);
            } else {
                return EnumSet.noneOf(DragDirection.class);
            }
        } else {
            return EnumSet.noneOf(DragDirection.class);
        }
    }

    private void smoothScrollToX(int finalLeft) {
        if (mViewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
        }
    }

    private void smoothScrollToY(int finalTop) {
        if (mViewDragHelper.settleCapturedViewAt(0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
        }
    }

    private boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    /**
     * Тип свайпа
     */
    public enum DragDirectMode {
        EDGE, // свайп только от края DragEdge
        DIRECTION_FROM_EDGE, // свайп из любой точки по направлению от края DragEdge
        VERTICAL, // свайп из любой точки влево/вправо
        HORIZONTAL // свайп из любой точки вверх/вниз
    }

    /**
     * Край, определяющий направление свайпа
     */
    public enum DragEdge {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        NONE
    }

    /**
     * SwipeBack слушатель
     */
    public interface SwipeBackListener {

        /**
         * Return scrolled fraction of the layout.
         *
         * @param fractionAnchor relative to the anchor.
         * @param fractionScreen relative to the screen.
         */
        void onViewPositionChanged(float fractionAnchor, float fractionScreen);

        /**
         * Notify when root layout whole disappeared by swipe
         */
        void onViewGoneBySwipe();

        /**
         * Коллбек вызывающийся после того как свайп закончен
         * @param isBack был ли совершен переход назад при завершении свайпа
         */
        default void onEndSwipe(boolean isBack) {
        }

        /**
         * Коллбек вызывающийся при начале свайпа
         * @param isBack
         */
        default void onStartSwipe() {
        }
    }

    private boolean contains(View mView, float x, float y) {
        mView.getGlobalVisibleRect(localRect);
        return localRect.contains((int) x, (int) y);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return (child == mTarget && mIsSwipeBackAvailable/* && mEnablePullToBack*/) || isMoving();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return mVerticalDragRange;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mHorizontalDragRange;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

            int result = 0;

            if (mWasChildScrollableInSwipeDirection) {
                return result;
            }

            if (mDragDirectMode == DragDirectMode.VERTICAL) {
                if (top > 0) {
                    mDragEdge = DragEdge.TOP;
                } else if (top < 0) {
                    mDragEdge = DragEdge.BOTTOM;
                }
            }

            if (mDragEdge == DragEdge.TOP && !canChildScrollUp() && top > 0) {
                final int topBound = getPaddingTop();
                final int bottomBound = mVerticalDragRange;
                result = min(max(top, topBound), bottomBound);
            } else if (mDragEdge == DragEdge.BOTTOM && !canChildScrollDown() && top < 0) {
                final int topBound = -mVerticalDragRange;
                final int bottomBound = getPaddingTop();
                result = min(max(top, topBound), bottomBound);
            }

            return result;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

            int result = 0;

            if (mWasChildScrollableInSwipeDirection) {
                return result;
            }

            if (mDragDirectMode == DragDirectMode.HORIZONTAL) {
                if (left > 0) {
                    mDragEdge = DragEdge.LEFT;
                } else if (left < 0) {
                    mDragEdge = DragEdge.RIGHT;
                }
            }

            if (mDragEdge == DragEdge.LEFT && !canChildScrollLeft() && left > 0) {
                final int leftBound = getPaddingLeft();
                final int rightBound = mHorizontalDragRange;
                result = min(max(left, leftBound), rightBound);
            } else if (mDragEdge == DragEdge.RIGHT && !canChildScrollRight() && left < 0) {
                final int leftBound = -mHorizontalDragRange;
                final int rightBound = getPaddingLeft();
                result = min(max(left, leftBound), rightBound);
            }

            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mSwipeBackListener != null &&
                    mDraggingState == ViewDragHelper.STATE_IDLE
                    && state == ViewDragHelper.STATE_DRAGGING) {
                mSwipeBackListener.onStartSwipe();
            }

            if (state == mDraggingState) {
                return;
            }

            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING
                    || mDraggingState == ViewDragHelper.STATE_SETTLING)
                    && state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.
                if (mDraggingOffset == getDragRange() && mSwipeBackListener != null) {
                    setAlpha(0.0f);
                    mSwipeBackListener.onViewGoneBySwipe();
                }

                if (mIsLayoutPostponed) {
                    mIsLayoutPostponed = false;
                    if (mDraggingOffset == 0) {
                        requestLayout();
                    }
                }
            }

            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            switch (mDragEdge) {
                case TOP:
                case BOTTOM:
                    mDraggingOffset = Math.abs(top);
                    break;
                case LEFT:
                case RIGHT:
                    mDraggingOffset = Math.abs(left);
                    break;
                default:
                    break;
            }

            //The proportion of the sliding.
            float fractionAnchor = (float) mDraggingOffset / mFinishAnchor;
            fractionAnchor = max(min(fractionAnchor, 1f), 0f);

            float fractionScreen = (float) mDraggingOffset / (float) getDragRange();
            fractionScreen = max(min(fractionScreen, 1f), 0f);

            if (mSwipeBackListener != null) {
                mSwipeBackListener.onViewPositionChanged(fractionAnchor, fractionScreen);
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
            if (mDraggingOffset == 0) {
                return;
            }

            if (mDraggingOffset == getDragRange()) {
                return;
            }

            boolean isBack = false;
            if (mEnableFlingBack && backBySpeed(xVel, yVel)) {
                isBack = backByVelocity(xVel, yVel);
            } else if (mDraggingOffset >= mFinishAnchor) {
                isBack = true;
            } else if (mDraggingOffset < mFinishAnchor) {
                isBack = false;
            }

            int finalLeft;
            int finalTop;
            switch (mDragEdge) {
                case LEFT:
                    finalLeft = isBack ? mHorizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case RIGHT:
                    finalLeft = isBack ? -mHorizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case TOP:
                    finalTop = isBack ? mVerticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
                case BOTTOM:
                    finalTop = isBack ? -mVerticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
            }

            mSwipeBackListener.onEndSwipe(isBack);
        }

        private boolean backBySpeed(float xVel, float yVel) {
            switch (mDragEdge) {
                case TOP:
                case BOTTOM:
                    if (Math.abs(yVel) > Math.abs(xVel) && Math.abs(yVel) > AUTO_FINISHED_SPEED_LIMIT) {
                        return mDragEdge == DragEdge.TOP ? !canChildScrollUp() : !canChildScrollDown();
                    }
                    break;
                case LEFT:
                case RIGHT:
                    if (Math.abs(xVel) > Math.abs(yVel) && Math.abs(xVel) > AUTO_FINISHED_SPEED_LIMIT) {
                        return mDragEdge == DragEdge.LEFT ? !canChildScrollLeft() : !canChildScrollRight();
                    }
                    break;
            }
            return false;
        }

        private boolean backByVelocity(float xVel, float yVel) {
            boolean velocityBack = false;
            switch (mDragEdge) {
                case TOP:
                    if (yVel > AUTO_FINISHED_SPEED_LIMIT) {
                        velocityBack = true;// !canChildScrollUp()
                    }
                    break;
                case BOTTOM:
                    if (yVel < -AUTO_FINISHED_SPEED_LIMIT) {
                        velocityBack = true; // !canChildScrollDown();
                    }
                    break;
                case LEFT:
                    if (xVel > AUTO_FINISHED_SPEED_LIMIT) {
                        velocityBack = true; // !canChildScrollLeft();
                    }
                    break;
                case RIGHT:
                    if (xVel < -AUTO_FINISHED_SPEED_LIMIT) {
                        velocityBack = true; // !canChildScrollRight();
                    }
                    break;
            }
            return velocityBack;
        }

        private boolean canChildScrollUp() {
            return mScrollChild != null && mScrollChild.canScrollVertically(-1);
        }

        private boolean canChildScrollDown() {
            return mScrollChild != null && mScrollChild.canScrollVertically(1);
        }

        private boolean canChildScrollRight() {
            return mScrollChild != null && mScrollChild.canScrollHorizontally(1);
        }

        private boolean canChildScrollLeft() {
            return mScrollChild != null && mScrollChild.canScrollHorizontally(-1);
        }

    }

}
