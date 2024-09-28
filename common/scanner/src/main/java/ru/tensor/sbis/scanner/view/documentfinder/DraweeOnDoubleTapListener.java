package ru.tensor.sbis.scanner.view.documentfinder;

import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnViewTapListener;

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

class DraweeOnDoubleTapListener implements GestureDetector.OnDoubleTapListener {

    @NonNull
    private final DraweeAttacher mAttacher;
    @Nullable
    private OnPhotoTapListener mPhotoTapListener;
    @Nullable
    private OnViewTapListener mViewTapListener;

    DraweeOnDoubleTapListener(@NonNull DraweeAttacher attacher) {
        mAttacher = attacher;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        final DraweeView<GenericDraweeHierarchy> draweeView = mAttacher.getDraweeView();
        if (draweeView == null) {
            return false;
        }
        if (mPhotoTapListener != null) {
            final RectF displayRect = mAttacher.getDisplayRect();
            if (displayRect != null) {
                final float x = e.getX(), y = e.getY();
                if (displayRect.contains(x, y)) {
                    final float xResult = (x - displayRect.left) / displayRect.width();
                    final float yResult = (y - displayRect.top) / displayRect.height();
                    mPhotoTapListener.onPhotoTap(draweeView, xResult, yResult);
                    return true;
                }
            }
        }

        if (mViewTapListener != null) {
            mViewTapListener.onViewTap(draweeView, e.getX(), e.getY());
            return true;
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        try {
            final float scale = mAttacher.getScale();
            final float x = event.getX(), y = event.getY();

            // min, mid, max
            final float newScale;
            if (scale < mAttacher.getScaleSettings().getMedium()) {
                newScale = mAttacher.getScaleSettings().getMedium();
            } else if (scale >= mAttacher.getScaleSettings().getMedium() && scale < mAttacher.getScaleSettings().getMaximum()) {
                newScale = mAttacher.getScaleSettings().getMaximum();
            } else {
                newScale = mAttacher.getScaleSettings().getMinimum();
            }
            mAttacher.setScale(newScale, x, y, true);
        } catch (Exception e) {
            // Can sometimes happen when getX() and getY() is called
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    void setOnPhotoTapListener(@Nullable OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    void setOnViewTapListener(@Nullable OnViewTapListener listener) {
        mViewTapListener = listener;
    }
}