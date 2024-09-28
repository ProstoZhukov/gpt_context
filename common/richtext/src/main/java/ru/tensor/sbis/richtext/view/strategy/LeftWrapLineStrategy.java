package ru.tensor.sbis.richtext.view.strategy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.text.style.UpdateLayout;
import android.view.View;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.span.PrioritySpan;
import ru.tensor.sbis.richtext.span.view.ViewStubOptions;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Стратегия по обтеканию текста слева
 *
 * @author am.boldinov
 */
public final class LeftWrapLineStrategy implements WrapLineStrategy {

    @Px
    private final int mExternalMargin;
    @NonNull
    private final ParagraphStretchProcessor mStretchProcessor = new ParagraphStretchProcessor();

    public LeftWrapLineStrategy(@NonNull Context context) {
        mExternalMargin = context.getResources().getDimensionPixelSize(R.dimen.richtext_image_left_text_margin);
    }

    @Nullable
    private LeftWrapSpan mLastSpan = null;
    private int mParaPosition;

    @Override
    public boolean wrap(@NonNull ViewLayout layout, @NonNull View view, @NonNull ViewStubOptions options,
                        @NonNull LineCursor cursor, int spanStart) {
        if (!layout.computeFitToLine(getSpanWidth(view, layout), cursor.get(), ViewTemplate.LEFT)) {
            return false;
        }
        if (mLastSpan == null) {
            prepareParagraph(layout, cursor, spanStart);
        }
        final int line = cursor.get();
        // вызов обязательно выполняется 2 раза, т.к спан реализует UpdateLayout
        // и происходит смещение end позиции у спана
        wrapInternal(layout.getText(), view, layout, line, layout.getWidthMeasureSpec());
        wrapInternal(layout.getText(), view, layout, line, layout.getWidthMeasureSpec());
        return true;
    }

    @Override
    public void layout(@NonNull RichViewLayout.LayoutParams layoutParams, @NonNull ViewLayout layout, int line) {
        layoutParams.topOffset = layout.getLineTop(line) + layout.getTextPaddingTop(mParaPosition, mParaPosition + 1);
        layoutParams.leftOffset = layout.getPriorityParagraphLeft(line, mLastSpan);
    }

    @Override
    public void onWrapCompleted(@NonNull ViewLayout layout, int line, int stretchSpace) {
        mStretchProcessor.process(layout, line, stretchSpace);
        mLastSpan = null;
    }

    private void wrapInternal(@NonNull Editable text, @NonNull View view, @NonNull ViewLayout layout, int line, int widthMeasureSpec) {
        final int end = layout.getLineEnd(line);
        if (mLastSpan == null) {
            mLastSpan = new LeftWrapSpan(getSpanWidth(view, layout));
            // если происходит измерение текста по контенту, то необходимо увеличить размер layout на максимальный размер обтекаемой View
            // из-за того, что при добавлении спанов вида LeadingMargin ширина layout не увеличивается
            if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY) {
                final int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (layout.getWidth() < maxWidth) {
                    final int requiredWidth = Math.min(maxWidth - layout.getWidth(), mLastSpan.mSize);
                    if (requiredWidth > 0) {
                        layout.increaseWidthTo(layout.getWidth() + requiredWidth);
                    }
                }
            }
        } else if (mLastSpan.mEnd == end) { // попытка установить спан на те же позиции, что и на предыдущей итерации
            return;
        }
        mLastSpan.mEnd = end;
        text.setSpan(mLastSpan, mParaPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void prepareParagraph(@NonNull ViewLayout layout, @NonNull LineCursor cursor, int spanStart) {
        final int start;
        final Editable text = layout.getText();
        if (spanStart == text.length()) {
            start = spanStart;
        } else {
            start = layout.getLineStart(cursor.get());
        }
        if (start > 0 && text.charAt(start - 1) != CharUtils.LF && (start == text.length() || text.charAt(start) != CharUtils.LF)) {
            text.insert(start, StringUtils.LF);
            mParaPosition = start + 1;
            cursor.moveTo(layout.getLineForOffset(mParaPosition));
        } else {
            if (text.length() == 0) {
                text.insert(start, StringUtils.LF);
            }
            mParaPosition = start;
        }
    }

    private int getSpanWidth(@NonNull View view, @NonNull ViewLayout layout) {
        final int maxWidth = View.MeasureSpec.getSize(layout.getWidthMeasureSpec());
        return Math.min(maxWidth, view.getMeasuredWidth() + mExternalMargin);
    }

    private static final class LeftWrapSpan implements LeadingMarginSpan, WrapLineSpan, UpdateLayout, PrioritySpan {

        private final int mSize;
        int mEnd;

        LeftWrapSpan(int size) {
            mSize = size;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return mSize;
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        }

        @Override
        public int getPriority() {
            return LeadingMargin.WRAP_VIEW;
        }

        @Override
        public int getSize() {
            return getViewSize();
        }

        @Override
        public int getViewSize() {
            return mSize;
        }

        @NonNull
        @Override
        public ViewTemplate getTemplate() {
            return ViewTemplate.LEFT;
        }
    }
}
