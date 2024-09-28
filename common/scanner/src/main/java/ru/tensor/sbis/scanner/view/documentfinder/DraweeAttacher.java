package ru.tensor.sbis.scanner.view.documentfinder;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import java.lang.ref.WeakReference;

import me.relex.photodraweeview.IAttacher;
import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnScaleChangeListener;
import me.relex.photodraweeview.OnScaleDragGestureListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.ScaleDragDetector;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.scanner.data.model.Rotation;

/**
 * Copyright 2015-2016 Relex
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class DraweeAttacher implements View.OnTouchListener, OnScaleDragGestureListener {

    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0, EDGE_TOP = 0;
    private static final int EDGE_RIGHT = 1, EDGE_BOTTOM = 1;
    private static final int EDGE_BOTH = 2;

    interface OnMatrixChangeListener {
        void onMatrixChange(@NonNull RectF imageBounds, @NonNull Matrix matrix);
    }

    private final float[] mMatrixValues = new float[9];
    @NonNull
    private final RectF mDisplayRect = new RectF();
    private final Interpolator mZoomInterpolator = new AccelerateDecelerateInterpolator();

    private long mZoomDuration = IAttacher.ZOOM_DURATION;

    @Nullable
    private ScaleDragDetector mScaleDragDetector;
    @Nullable
    private GestureDetectorCompat mGestureDetector;

    private boolean mBlockParentIntercept = false;
    private boolean mAllowParentInterceptOnEdge = true;
    private int mScrollEdgeX = EDGE_BOTH;
    private int mScrollEdgeY = EDGE_BOTH;

    @NonNull
    private final ScaleSettings mScaleSettings = new ScaleSettings();
    @NonNull
    private final Matrix mMatrix = new Matrix();
    private int mImageInfoHeight = -1, mImageInfoWidth = -1;
    private DraweeAttacher.FlingRunnable mCurrentFlingRunnable;
    private WeakReference<DraweeView<GenericDraweeHierarchy>> mDraweeView;
    private boolean mMatrixEventSendState;
    @NonNull
    private Rotation mRotation = Rotation.DEFAULT;

    private View.OnLongClickListener mLongClickListener;
    private OnScaleChangeListener mScaleChangeListener;
    @Nullable
    private OnMatrixChangeListener mMatrixChangeListener;
    @Nullable
    private DraweeOnDoubleTapListener mTapListener;
    private boolean mIsTouchEnabled = true;

    DraweeAttacher(DraweeView<GenericDraweeHierarchy> draweeView) {
        mDraweeView = new WeakReference<>(draweeView);
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
    }

    void onAttachedToWindow() {
        if (mIsTouchEnabled) {
            initTouchListeners();
        }
    }

    void onDetachedFromWindow() {
        cancelFling();
        if (mIsTouchEnabled) {
            releaseTouchListeners();
        }
    }

    private void initTouchListeners() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null && mGestureDetector == null) {
            draweeView.setOnTouchListener(this);
            mScaleDragDetector = new ScaleDragDetector(draweeView.getContext(), this);
            mGestureDetector = new GestureDetectorCompat(draweeView.getContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public void onLongPress(MotionEvent e) {
                            super.onLongPress(e);
                            if (mLongClickListener != null) {
                                mLongClickListener.onLongClick(getDraweeView());
                            }
                        }
                    });
            mTapListener = new DraweeOnDoubleTapListener(this);
            mGestureDetector.setOnDoubleTapListener(mTapListener);
        }
    }

    private void releaseTouchListeners() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null) {
            draweeView.setOnTouchListener(null);
        }
        mScaleDragDetector = null;
        mGestureDetector = null;
        mTapListener = null;
        mGestureDetector = null;
    }

    void setTouchEnabled(boolean enabled) {
        mIsTouchEnabled = enabled;
        if (mIsTouchEnabled) {
            initTouchListeners();
        } else {
            releaseTouchListeners();
        }
    }

    @Nullable
    DraweeView<GenericDraweeHierarchy> getDraweeView() {
        return mDraweeView.get();
    }

    @NonNull
    ScaleSettings getScaleSettings() {
        return mScaleSettings;
    }

    void setRotation(@NonNull Rotation rotation) {
        new RotationRunnable(rotation).run();
    }

    void resetRotation() {
        mRotation = Rotation.HORIZONTAL;
    }

    @NonNull
    Rotation getRotation() {
        return mRotation;
    }

    @NonNull
    Rotation getNextRotation(boolean cw) {
        return mRotation.nextRotation(cw);
    }

    float getScale() {
        return (float) Math.sqrt(
                (float) Math.pow(getMatrixValue(mMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(
                        getMatrixValue(mMatrix, Matrix.MSKEW_Y), 2));
    }

    void setScale(float scale) {
        setScale(scale, false);
    }

    void setScale(float scale, boolean animate) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null) {
            setScale(scale, (draweeView.getRight()) / 2, (draweeView.getBottom()) / 2, animate);
        }
    }

    void setScale(float scale, float focalX, float focalY, boolean animate) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();

        if (draweeView == null || scale < mScaleSettings.getMinimum() || scale > mScaleSettings.getMaximum()) {
            return;
        }

        if (animate) {
            draweeView.post(new DraweeAttacher.AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        } else {
            mMatrix.setScale(scale, scale, focalX, focalY);
            postMatrixInvalidate();
        }
    }

    void setZoomTransitionDuration(long duration) {
        duration = duration < 0 ? IAttacher.ZOOM_DURATION : duration;
        mZoomDuration = duration;
    }

    void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    void setOnScaleChangeListener(@Nullable OnScaleChangeListener listener) {
        ensureTouchEnabled();
        mScaleChangeListener = listener;
    }

    void setOnLongClickListener(@Nullable View.OnLongClickListener listener) {
        ensureTouchEnabled();
        mLongClickListener = listener;
    }

    void setOnPhotoTapListener(@Nullable OnPhotoTapListener listener) {
        ensureTouchEnabled();
        CommonUtils.checkNotNull(mTapListener).setOnPhotoTapListener(listener);
    }

    void setOnViewTapListener(@Nullable OnViewTapListener listener) {
        ensureTouchEnabled();
        CommonUtils.checkNotNull(mTapListener).setOnViewTapListener(listener);
    }

    void update(int imageInfoWidth, int imageInfoHeight) {
        if (imageInfoWidth == mImageInfoWidth && imageInfoHeight == mImageInfoHeight
                || imageInfoWidth == -1 && imageInfoHeight == -1) {
            return;
        }
        mImageInfoWidth = imageInfoWidth;
        mImageInfoHeight = imageInfoHeight;
        resetMatrix();
    }

    void setOnMatrixChangeListener(@Nullable OnMatrixChangeListener onMatrixChangeListener) {
        mMatrixChangeListener = onMatrixChangeListener;
    }

    private int getViewWidth() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null) {
            return draweeView.getWidth()
                    - draweeView.getPaddingLeft()
                    - draweeView.getPaddingRight();
        }
        return 0;
    }

    private int getViewHeight() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null) {
            return draweeView.getHeight()
                    - draweeView.getPaddingTop()
                    - draweeView.getPaddingBottom();
        }
        return 0;
    }

    private float getMatrixValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    float[] getDrawMatrixValues() {
        getDrawMatrix().getValues(mMatrixValues);
        return mMatrixValues;
    }

    @NonNull
    Matrix getDrawMatrix() {
        return mMatrix;
    }

    @Nullable
    RectF getDisplayRect() {
        return getDisplayRect(getDrawMatrix());
    }

    private void postMatrixInvalidate() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView == null) {
            return;
        }
        draweeView.invalidate();
        notifyMatrixChange();
    }

    private boolean checkMatrixBounds() {
        RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0.0F;
        float deltaY = 0.0F;
        int viewHeight = getViewHeight();

        if (height <= (float) viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
            mScrollEdgeY = EDGE_BOTH;
        } else if (rect.top > 0.0F) {
            deltaY = -rect.top;
            mScrollEdgeY = EDGE_TOP;
        } else if (rect.bottom < (float) viewHeight) {
            deltaY = viewHeight - rect.bottom;
            mScrollEdgeY = EDGE_BOTTOM;
        } else {
            mScrollEdgeY = EDGE_NONE;
        }
        int viewWidth = getViewWidth();
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
            mScrollEdgeX = EDGE_BOTH;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
            mScrollEdgeX = EDGE_LEFT;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdgeX = EDGE_RIGHT;
        } else {
            mScrollEdgeX = EDGE_NONE;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private RectF getDisplayRect(@NonNull Matrix matrix) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView == null || (mImageInfoWidth == -1 && mImageInfoHeight == -1)) {
            return null;
        }
        mDisplayRect.set(0.0F, 0.0F, mImageInfoWidth, mImageInfoHeight);
        draweeView.getHierarchy().getActualImageBounds(mDisplayRect);
        matrix.mapRect(mDisplayRect);
        return mDisplayRect;
    }

    private void resetMatrix() {
        mMatrix.reset();
        resetRotation();
        checkMatrixBounds();
        postMatrixInvalidate();
    }

    private void checkMinScale() {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView == null) {
            return;
        }

        if (getScale() < mScaleSettings.getMinimum()) {
            RectF rect = getDisplayRect();
            if (null != rect) {
                draweeView.post(new DraweeAttacher.AnimatedZoomRunnable(getScale(), mScaleSettings.getMinimum(), rect.centerX(),
                        rect.centerY()));
            }
        }
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if (getScale() < mScaleSettings.getMaximum() || scaleFactor < 1.0F) {

            if (mScaleChangeListener != null) {
                mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
            }

            mMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            postMatrixInvalidate();
        }
    }

    @Override
    public void onScaleEnd() {
        checkMinScale();
    }

    @Override
    public void onDrag(float dx, float dy) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView != null && (mScaleDragDetector == null || !mScaleDragDetector.isScaling())) {
            mMatrix.postTranslate(dx, dy);
            postMatrixInvalidate();

            ViewParent parent = draweeView.getParent();
            if (parent == null) {
                return;
            }
            if (mAllowParentInterceptOnEdge
                    && (mScaleDragDetector == null || !mScaleDragDetector.isScaling())
                    && !mBlockParentIntercept) {
                if ((mRotation == Rotation.HORIZONTAL || mRotation == Rotation.HORIZONTAL_INVERSE) && (mScrollEdgeX == EDGE_BOTH || (mScrollEdgeX
                        == EDGE_LEFT && dx >= 1f) || (mScrollEdgeX == EDGE_RIGHT && dx <= -1f))) {
                    parent.requestDisallowInterceptTouchEvent(false);
                } else if ((mRotation == Rotation.VERTICAL || mRotation == Rotation.VERTICAL_INVERSE) && (mScrollEdgeY == EDGE_BOTH || (mScrollEdgeY
                        == EDGE_TOP && dy >= 1f) || (mScrollEdgeY == EDGE_BOTTOM && dy <= -1f))) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
        if (draweeView == null) {
            return;
        }
        mCurrentFlingRunnable = new DraweeAttacher.FlingRunnable(draweeView.getContext());
        mCurrentFlingRunnable.fling(getViewWidth(), getViewHeight(), (int) velocityX,
                (int) velocityY);
        draweeView.post(mCurrentFlingRunnable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                ViewParent parent = v.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                cancelFling();
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                ViewParent parent = v.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
            break;
        }

        boolean handled = false;

        if (mScaleDragDetector != null) {
            final boolean wasScaling = mScaleDragDetector.isScaling();
            final boolean wasDragging = mScaleDragDetector.isDragging();

            handled = mScaleDragDetector.onTouchEvent(event);

            boolean noScale = !wasScaling && !mScaleDragDetector.isScaling();
            boolean noDrag = !wasDragging && !mScaleDragDetector.isDragging();
            mBlockParentIntercept = noScale && noDrag;
        }

        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            handled = true;
        }

        return handled;
    }

    private class RotationRunnable implements Runnable {

        private static final int RUN_THRESHOLD = 2;

        @NonNull
        private final Rotation mTargetRotation;
        private int mRunCount;

        public RotationRunnable(@NonNull Rotation rotation) {
            mTargetRotation = rotation;
        }

        @Override
        public void run() {
            if (mRunCount == RUN_THRESHOLD) {
                return;
            }
            mRunCount++;
            if (mRotation != mTargetRotation) {
                final RectF displayRect = getDisplayRect();
                if (displayRect == null || displayRect.isEmpty()) {
                    DraweeView draweeView = getDraweeView();
                    if (draweeView != null) {
                        draweeView.post(this);
                    }
                    return;
                }
                mRotation = mTargetRotation;
                final Matrix inverseMatrix = new Matrix();
                mMatrix.invert(inverseMatrix);
                inverseMatrix.mapRect(displayRect); // в любом положении anchor x,y после rotate возвращаемся в центр
                mMatrix.setRotate(mRotation.getDegrees(), displayRect.centerX(), displayRect.centerY()); // TODO сделать поворот на интерполяторе
                final RectF rotatedDisplayRect = getDisplayRect();
                final float scaleX = getViewWidth() / rotatedDisplayRect.width();
                final float scaleY = getViewHeight() / rotatedDisplayRect.height();
                final float scale = Math.min(scaleX, scaleY);
                final float oldScale = getScale();
                mMatrix.postScale(scale, scale, rotatedDisplayRect.centerX(), rotatedDisplayRect.centerY());
                mScaleSettings.postAll(oldScale - getScale());
                postMatrixInvalidate();
            }
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
            if (draweeView == null) {
                return;
            }

            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, mFocalX, mFocalY);

            if (t < 1f) {
                ViewCompat.postOnAnimation(draweeView, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mZoomInterpolator.getInterpolation(t);
            return t;
        }
    }

    private class FlingRunnable implements Runnable {

        private final ScrollerCompat mScroller;
        private int mCurrentX, mCurrentY;

        FlingRunnable(Context context) {
            mScroller = ScrollerCompat.create(context);
        }

        void cancelFling() {
            mScroller.abortAnimation();
        }

        void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return;
            }
            DraweeView<GenericDraweeHierarchy> draweeView = getDraweeView();
            if (draweeView != null && mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();
                mMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                postMatrixInvalidate();
                mCurrentX = newX;
                mCurrentY = newY;
                ViewCompat.postOnAnimation(draweeView, this);
            }
        }
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    private void notifyMatrixChange() {
        if (mMatrixChangeListener != null) {
            final RectF displayRect = getDisplayRect();
            if (displayRect != null && !displayRect.isEmpty()) {
                mMatrixEventSendState = true;
                mMatrixChangeListener.onMatrixChange(displayRect, mMatrix);
            }
        }
    }

    private void ensureTouchEnabled() {
        if (!mIsTouchEnabled) {
            throw new IllegalStateException("Enable touch processing before calling this method");
        }
    }

    void notifyMatrixChangeIfAllowedState() {
        if (!mMatrixEventSendState) {
            notifyMatrixChange();
        }
    }
}
