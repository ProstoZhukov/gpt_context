package ru.tensor.sbis.scanner.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import ru.tensor.sbis.scanner.R;

/**
 * @author am.boldinov
 */
public final class SnapshotButton extends View {

    private Paint mCirclePaint;
    private Paint mRingPaint;

    public SnapshotButton(Context context) {
        super(context);
        init(null);
    }

    public SnapshotButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SnapshotButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SnapshotButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.document_snapshot_round_stroke_width));
        if (attrs != null) {
            final TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SnapshotButton, 0, 0);
            final int circleColor = attrArray.getResourceId(R.styleable.SnapshotButton_circle_color, ru.tensor.sbis.design.R.color.palette_color_white1);
            mCirclePaint.setColor(ContextCompat.getColor(getContext(), circleColor));
            final int ringColor = attrArray.getResourceId(R.styleable.SnapshotButton_ring_color, ru.tensor.sbis.design.R.color.palette_color_white1);
            mRingPaint.setColor(ContextCompat.getColor(getContext(), ringColor));
            attrArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float radiusX = getRadius(getWidth(), getPaddingLeft(), getPaddingRight());
        final float radiusY = getRadius(getHeight(), getPaddingTop(), getPaddingBottom());
        final float pivotX = getPaddingLeft() + radiusX;
        final float pivotY = getPaddingTop() + radiusY;
        final float radius = Math.min(radiusX, radiusY);
        canvas.drawCircle(pivotX, pivotY, radius, mRingPaint);
        canvas.drawCircle(pivotX, pivotY, radius - mRingPaint.getStrokeWidth() * 2, mCirclePaint);
    }

    private static float getRadius(int size, int startPadding, int endPadding) {
        return (size - startPadding - endPadding) / 2;
    }
}
