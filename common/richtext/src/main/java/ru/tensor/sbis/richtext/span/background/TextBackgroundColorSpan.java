package ru.tensor.sbis.richtext.span.background;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.util.LayoutUtil;

/**
 * Span для отрисовки выделения цветом текста на конкретных позициях.
 * Опционально может иметь закругления в начале и конце выделения.
 *
 * @author am.boldinov
 */
public final class TextBackgroundColorSpan extends CharacterStyle implements LayoutBackgroundSpan {

    @NonNull
    private final GradientDrawable mBgDrawable;
    @NonNull
    private final float[] mLeftCornerRadii;
    @NonNull
    private final float[] mRightCornerRadii;
    private final float mCornerRadius;

    public TextBackgroundColorSpan(@ColorInt int color, float cornerRadius) {
        mBgDrawable = new GradientDrawable();
        mBgDrawable.setColor(color);
        mLeftCornerRadii = new float[]{cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius};
        mRightCornerRadii = new float[]{0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f};
        mCornerRadius = cornerRadius;
    }

    public TextBackgroundColorSpan(@ColorInt int color) {
        this(color, 0f);
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Layout layout, int start, int end) {
        final int startOffset = (int) layout.getPrimaryHorizontal(start);
        final int endOffset = (int) layout.getPrimaryHorizontal(end);
        final int startLine = layout.getLineForOffset(start);
        final int endLine = layout.getLineForOffset(end);
        if (startLine == endLine) {
            mBgDrawable.setCornerRadius(mCornerRadius);
            mBgDrawable.setBounds(startOffset, LayoutUtil.getLineAscentRelative(layout, startLine, start, end),
                    endOffset, LayoutUtil.getLineDescentRelative(layout, startLine, start, end));
            mBgDrawable.draw(canvas);
        } else { // один span на разных строках
            // left
            final int startLineEnd = layout.getLineEnd(startLine);
            mBgDrawable.setCornerRadii(mLeftCornerRadii);
            mBgDrawable.setBounds(startOffset, LayoutUtil.getLineAscentRelative(layout, startLine, start, startLineEnd),
                    (int) layout.getLineWidth(startLine) + startOffset, LayoutUtil.getLineDescentRelative(layout, startLine, start, startLineEnd));
            mBgDrawable.draw(canvas);
            // middle
            for (int i = startLine + 1; i < endLine; i++) { // если более двух строк
                final int left = (int) layout.getLineLeft(i);
                final int top = LayoutUtil.getLineAscentRelative(layout, i);
                final int right = (int) layout.getLineRight(i);
                final int bottom = LayoutUtil.getLineDescentRelative(layout, i);
                mBgDrawable.setCornerRadius(0f);
                mBgDrawable.setBounds(left, top, right, bottom);
                mBgDrawable.draw(canvas);
            }
            // right
            final int endLineStart = layout.getLineStart(endLine);
            mBgDrawable.setCornerRadii(mRightCornerRadii);
            mBgDrawable.setBounds((int) layout.getLineLeft(endLine), LayoutUtil.getLineAscentRelative(layout, endLine, endLineStart, end),
                    endOffset, LayoutUtil.getLineDescentRelative(layout, endLine, endLineStart, end));
            mBgDrawable.draw(canvas);
        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        // ignore
    }
}
