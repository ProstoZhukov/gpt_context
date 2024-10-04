package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Компонент демонстрирующий прогресс загрузки изображения.
 */
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class LoadingImageViewProgressBar extends Drawable {

    @NonNull
    private final Paint arcPaintBackground;
    @NonNull
    private final Paint arcPaintPrimary;
    private final int widthPixels;
    private final int initSize;

    private int progress;

    public LoadingImageViewProgressBar(@NonNull Context context) {
        initSize = context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_loading_placeholder_size);
        widthPixels = context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_loading_progressbar_width);
        arcPaintBackground = getNeededPaintForLoadingProgress(context, true);
        arcPaintPrimary = getNeededPaintForLoadingProgress(context, false);
    }

    @NonNull
    private Paint getNeededPaintForLoadingProgress(@NonNull Context context, boolean isBackgroundColor) {
        Paint neededPaint = new Paint();
        int color;
        if (isBackgroundColor) {
            color = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.loading_progressbar_bg);
        } else {
            color = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.loading_progressbar_progress);
        }
        neededPaint.setDither(true);
        neededPaint.setStyle(Paint.Style.STROKE);
        neededPaint.setStrokeCap(Paint.Cap.BUTT);
        neededPaint.setStrokeJoin(Paint.Join.BEVEL);
        neededPaint.setColor(color);
        neededPaint.setStrokeWidth(isBackgroundColor ? widthPixels : (widthPixels - 1f)); // -1px is for border
        neededPaint.setAntiAlias(true);
        return neededPaint;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //special size is needed for small views which smaller then R.dimen.loading_placeholder_size
        int specialSize = Math.min(canvas.getWidth(), canvas.getHeight()) / 3;
        if (initSize < specialSize) {
            specialSize = initSize;
        }
        RectF rect = new RectF(canvas.getWidth() / 2 - specialSize, canvas.getHeight() / 2 - specialSize,
                canvas.getWidth() / 2 + specialSize, canvas.getHeight() / 2 + specialSize);
        canvas.drawOval(rect, arcPaintBackground);
        float angle = 360 * progress / 10000f; //10 000 - max fresco progress
        canvas.drawArc(rect, -90, angle, false, arcPaintPrimary);
    }

    @Override
    protected boolean onLevelChange(int level) {
        progress = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        //ignore
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        //ignore
    }

    @Override
    public int getOpacity() {
        //ignore
        return PixelFormat.UNKNOWN;
    }
}
