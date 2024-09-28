package ru.tensor.sbis.richtext.span.view.block;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.theme.global_variables.Offset;
import ru.tensor.sbis.design.theme.global_variables.StyleColor;
import ru.tensor.sbis.richtext.view.CloneableRichViewFactory;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * Компонент для рендера блока богатого текста, имеющего фон и иконку в начале.
 *
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
public final class BlockViewLayout extends FrameLayout {

    @NonNull
    private final RichViewLayout mRichViewLayout;
    @Nullable
    private Drawable mIcon;
    private final int mIconRightMargin;
    private int mContentPaddingLeft;
    private int mInternalPaddingLeft;

    public BlockViewLayout(@NonNull Context context, @NonNull CloneableRichViewFactory richViewFactory) {
        super(context);
        setWillNotDraw(false);
        mRichViewLayout = richViewFactory.cloneView();
        addView(mRichViewLayout);
        mIconRightMargin = Offset.S.getDimenPx(context);
        final int padding = Offset.ST.getDimenPx(context);
        setPadding(padding, padding, padding, padding);
        setBackgroundColor(StyleColor.UNACCENTED.getBackgroundColor(context));
    }

    public void setContent(@Nullable Drawable icon, @NonNull Spannable content) {
        mIcon = icon;
        if (mIcon != null) {
            mContentPaddingLeft = mIcon.getIntrinsicWidth() + mIconRightMargin;
        } else {
            mContentPaddingLeft = 0;
        }
        updateContentPadding();
        mRichViewLayout.setText(content);
    }

    /**
     * Отправляет дочерние View на переиспользование.
     */
    public void recycle() {
        mRichViewLayout.recycle();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        mInternalPaddingLeft = left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIcon != null) {
            canvas.save();
            final int dx = Math.max(getPaddingLeft() - mContentPaddingLeft, 0);
            canvas.translate(dx, getPaddingTop());
            mIcon.draw(canvas);
            canvas.restore();
        }
    }

    private void updateContentPadding() {
        // вызов без обновления mInternalPaddingLeft для избежания циклического увеличения отступа
        super.setPadding(mInternalPaddingLeft + mContentPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
}
