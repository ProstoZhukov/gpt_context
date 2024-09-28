package ru.tensor.sbis.design.view_ext.horizontalpatch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import ru.tensor.sbis.design.view_ext.R;
import android.util.AttributeSet;

/**
 * Реализация текст-вью, которая в качестве фона использует одноцветную фигуру
 * с заданными краями.
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class HorizontalPatchTextView extends AppCompatTextView {

    private static final int DEFAULT_SHAPE_COLOR = Color.WHITE;

    /**
     * Drawable для левого края фигуры.
     */
    @Nullable
    private Drawable mLeftPatch;

    /**
     * Drawable для правого края фигуры.
     */
    @Nullable
    private Drawable mRightPatch;

    /**
     * Фильтр цвета для придания цвета краям фигуры.
     */
    private PorterDuffColorFilter mColorFilter;

    /**
     * Кисть для отрисовки центральной части фигуры.
     */
    private final Paint mPaint = new Paint();

    public HorizontalPatchTextView(Context context) {
        this(context, null);
    }

    public HorizontalPatchTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPatchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HorizontalPatchTextView);
            int color = array.getColor(R.styleable.HorizontalPatchTextView_shapeColor, DEFAULT_SHAPE_COLOR);
            setColorInternal(color);
            mLeftPatch = array.getDrawable(R.styleable.HorizontalPatchTextView_leftPatch);
            mRightPatch = array.getDrawable(R.styleable.HorizontalPatchTextView_rightPatch);
            array.recycle();
        }
    }

    private void setColorInternal(int color) {
        mColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        mPaint.setColor(color);
    }

    /**
     * Задать цвет фигуры.
     */
    public void setColor(int color) {
        setColorInternal(color);
        invalidate();
    }

    /**
     * Задать левый край.
     */
    public void setLeftPatch(@Nullable Drawable drawable) {
        if (mLeftPatch != drawable) {
            mLeftPatch = drawable;
            invalidate();
        }
    }

    /**
     * Задать правый край.
     */
    public void setRightPatch(@Nullable Drawable drawable) {
        if (mRightPatch != drawable) {
            mRightPatch = drawable;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // Translate canvas by scrollX to fix position
        canvas.translate(getScrollX(), 0);
        final int left = 0;
        final int right = getWidth() - left;
        final int top = 0;
        final int bottom = getHeight() - top;
        int leftMiddle = left, rightMiddle = right;
        // Draw left patch
        if (mLeftPatch != null) {
            final float leftAspect = (float) mLeftPatch.getIntrinsicWidth() / mLeftPatch.getIntrinsicHeight();
            leftMiddle = left + Math.round((bottom - top) * leftAspect);
            mLeftPatch.setColorFilter(mColorFilter);
            mLeftPatch.setBounds(left, top, leftMiddle, bottom);
            mLeftPatch.draw(canvas);
        }
        // Draw right patch
        if (mRightPatch != null) {
            final float rightAspect = (float) mRightPatch.getIntrinsicWidth() / mRightPatch.getIntrinsicHeight();
            rightMiddle = right - Math.round((bottom - top) * rightAspect);
            mRightPatch.setColorFilter(mColorFilter);
            mRightPatch.setBounds(rightMiddle, top, right, bottom);
            mRightPatch.draw(canvas);
        }
        // Draw middle patch
        canvas.drawRect(leftMiddle, top, rightMiddle, bottom, mPaint);
        // Restore canvas after shape drawing
        canvas.restore();

        super.onDraw(canvas);
    }

}