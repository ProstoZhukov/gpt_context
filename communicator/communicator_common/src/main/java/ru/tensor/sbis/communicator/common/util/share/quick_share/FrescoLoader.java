package ru.tensor.sbis.communicator.common.util.share.quick_share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import timber.log.Timber;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Вспомогательный класс для загрузки Bitmap из fresco
 *
 * @author vv.chekurda
 */
enum FrescoLoader {
    INSTANCE;

    /**
     * Колбэк о загрузке Bitmap
     */
    interface OnLoad {
        void onLoad(Bitmap bitmap);
    }

    public void setDataSubscriber(Context context, @Nullable String uri, @DrawableRes int stub, int width, int height, OnLoad onLoad) {
        if (uri != null && !uri.isEmpty()) {
            DataSubscriber<CloseableReference<CloseableImage>> dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

                @Override
                public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (!dataSource.isFinished()) {
                        return;
                    }
                    CloseableReference<CloseableImage> imageReference = dataSource.getResult();
                    if (imageReference != null && imageReference.isValid()) {
                        final CloseableReference<CloseableImage> closeableReference = imageReference.clone();
                        try {
                            CloseableImage closeableImage = closeableReference.get();
                            if (closeableImage instanceof CloseableBitmap) {
                                CloseableBitmap closeableBitmap = (CloseableBitmap) closeableImage;
                                Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                                if (bitmap != null && !bitmap.isRecycled()) {
                                    onLoad.onLoad(getRoundedCornerBitmap(bitmap, (height / 2) - 3));
                                }
                            }
                        } catch (Throwable e) {
                            Timber.e(e);
                        } finally {
                            imageReference.close();
                            closeableReference.close();
                        }
                    }
                }

                @Override
                public void onFailureImpl(DataSource dataSource) {
                    Throwable throwable = dataSource.getFailureCause();
                    Timber.e(throwable);
                }
            };

            loadBitmapFromUri(context, Uri.parse(uri), width, height, dataSubscriber);
        } else {
            loadBitmapFromDrawable(
                ContextCompat.getDrawable(context, stub),
                onLoad
            );
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @SuppressWarnings({"unchecked"})
    private void loadBitmapFromUri(Context context, Uri uri, int width, int height, DataSubscriber<?> dataSubscriber){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (width > 0 && height > 0){
            builder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest request = builder.build();
        try {
            DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(request, context);
            dataSource.subscribe((DataSubscriber<CloseableReference<CloseableImage>>) dataSubscriber,
                    UiThreadImmediateExecutorService.getInstance());
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    private void loadBitmapFromDrawable(Drawable stubPhoto, OnLoad onLoad) {
        Bitmap bitmap;
        if (stubPhoto instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) stubPhoto).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(stubPhoto.getIntrinsicWidth(), stubPhoto.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            stubPhoto.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            stubPhoto.draw(canvas);
        }
        onLoad.onLoad(bitmap);
    }
}
