package ru.tensor.sbis.scanner.view.documentfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

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

class DocumentDraweeView extends SimpleDraweeView {

    interface OnLoadingListener {
        void onComplete();
    }

    private DraweeAttacher mAttacher;

    private boolean mEnableDraweeMatrix = true;

    public DocumentDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public DocumentDraweeView(Context context) {
        super(context);
        init();
    }

    public DocumentDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DocumentDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        if (mAttacher == null || mAttacher.getDraweeView() == null) {
            mAttacher = new DraweeAttacher(this);
        }
    }

    public DraweeAttacher getAttacher() {
        return mAttacher;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        if (mEnableDraweeMatrix) {
            canvas.concat(mAttacher.getDrawMatrix());
        }
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
        mAttacher.notifyMatrixChangeIfAllowedState();
    }

    @Override
    protected void onAttachedToWindow() {
        init();
        mAttacher.onAttachedToWindow();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttacher.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public boolean isEnableDraweeMatrix() {
        return mEnableDraweeMatrix;
    }

    public void setEnableDraweeMatrix(boolean enableDraweeMatrix) {
        mEnableDraweeMatrix = enableDraweeMatrix;
    }

    public void rotateCw() {
        mAttacher.setRotation(mAttacher.getNextRotation(true));
    }

    public void rotateCcw() {
        mAttacher.setRotation(mAttacher.getNextRotation(false));
    }

    public void setDraweeUri(String uri, @Nullable OnLoadingListener onLoadingListener) {
        setDraweeUri(uri, null, onLoadingListener);
    }

    public void setDraweeUri(String uri, @Nullable Context context, @Nullable OnLoadingListener onLoadingListener) {
        mEnableDraweeMatrix = false;
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setCallerContext(context)
                .setUri(uri)
                .setOldController(getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        mEnableDraweeMatrix = false;
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mEnableDraweeMatrix = true;
                        if (onLoadingListener != null) {
                            onLoadingListener.onComplete();
                        }
                        if (imageInfo != null) {
                            mAttacher.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        mEnableDraweeMatrix = false;
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        mEnableDraweeMatrix = true;
                        if (imageInfo != null) {
                            mAttacher.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                })
                .build();
        setController(controller);
    }
}
