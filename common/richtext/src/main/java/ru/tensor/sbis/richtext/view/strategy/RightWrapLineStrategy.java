package ru.tensor.sbis.richtext.view.strategy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.style.ReplacementSpan;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.span.LineReplacementSpan;
import ru.tensor.sbis.richtext.span.view.ViewStubOptions;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.StaticLayoutProxy;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;
import ru.tensor.sbis.richtext.view.strategy.bounds.SpanBoundsTransformer;

/**
 * Стратегия по обтеканию текста справа
 *
 * @author am.boldinov
 */
public final class RightWrapLineStrategy implements WrapLineStrategy {

    @NonNull
    private final ParagraphStretchProcessor mStretchProcessor = new ParagraphStretchProcessor();
    @Px
    private final int mExternalMargin;
    private int mLineRight;
    private boolean mLayoutIncreased;

    public RightWrapLineStrategy(@NonNull Context context) {
        mExternalMargin = context.getResources().getDimensionPixelSize(R.dimen.richtext_image_right_text_margin);
    }

    @Override
    public boolean wrap(@NonNull ViewLayout layout, @NonNull View view, @NonNull ViewStubOptions options,
                        @NonNull LineCursor cursor, int spanStart) {
        final int line = cursor.get();
        final Editable text = layout.getText();
        final int maxWidth = View.MeasureSpec.getSize(layout.getWidthMeasureSpec());
        int frameWidth = Math.min(maxWidth, view.getMeasuredWidth() + mExternalMargin);
        if (!layout.computeFitToLine(frameWidth, line, ViewTemplate.RIGHT)) {
            return false;
        }
        final int mode = View.MeasureSpec.getMode(layout.getWidthMeasureSpec());
        mLineRight = maxWidth - frameWidth;
        if (mode != View.MeasureSpec.EXACTLY) {
            mLineRight = Math.min(mLineRight, layout.getWidth());
            if (!mLayoutIncreased && layout.getWidth() < maxWidth) {
                final int requiredWidth = Math.min(maxWidth - layout.getWidth(), frameWidth);
                if (requiredWidth > 0) {
                    layout.increaseWidthTo(layout.getWidth() + requiredWidth);
                    mLayoutIncreased = true;
                }
            }
        }
        final int maxLineWidth = mLineRight - layout.getParagraphLeft(line);
        if (maxLineWidth < 0) {
            return false;
        }
        final int start = layout.getLineStart(line);
        final int end = layout.getLineEnd(line);
        final float width = layout.getLineWidth(line);
        final LineReplacementSpan[] lineReplacements = text.getSpans(start, end, LineReplacementSpan.class);
        if (lineReplacements.length > 0) {
            lineReplacements[0].setMaxLineWidth(maxLineWidth);
            final int position = Math.max(end - 1, start);
            text.setSpan(new MarkRightWrapSpan(frameWidth), position, position, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (width > maxLineWidth || options.getSpanBoundsTransformer() != null) {
            // TODO сделать prefetch для нескольких строк в случае большой высоты View https://online.sbis.ru/opendoc.html?guid=c9ecf031-8daa-45e3-96a2-64611d392ac4
            final StaticLayout staticLayout = StaticLayoutProxy.create(text, start, end, maxLineWidth, layout.getTextLayout());
            int lineEnd = staticLayout.getLineEnd(0);
            if (lineEnd == 0 || lineEnd > 0 && !HtmlHelper.isSpaceCharacter(text.charAt(lineEnd - 1))) {
                insert(text, lineEnd, StringUtils.SPACE, options);
            } else {
                lineEnd--;
            }
            final float lineWidth = staticLayout.getLineWidth(0);
            if (lineWidth < maxLineWidth) {
                frameWidth += maxLineWidth - lineWidth;
            }
            text.setSpan(new RightWrapSpan(frameWidth), lineEnd, lineEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (layout.getLineForOffset(lineEnd) > line) { // если после установки спана произошел перенос на новую строку
                text.insert(lineEnd + 1, StringUtils.SPACE);
            }
        } else {
            final int position = Math.max(end - 1, start);
            text.setSpan(new MarkRightWrapSpan(frameWidth), position, position, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return true;
    }

    @Override
    public void layout(@NonNull RichViewLayout.LayoutParams layoutParams, @NonNull ViewLayout layout, int line) {
        final int start = layout.getLineStart(line);
        layoutParams.topOffset = layout.getLineTop(line) + layout.getTextPaddingTop(start, start + 1);
        layoutParams.leftOffset = mLineRight;
        if (mLineRight > 0) {
            layoutParams.leftOffset += mExternalMargin;
        }
    }

    @Override
    public void onWrapCompleted(@NonNull ViewLayout layout, int line, int stretchSpace) {
        if (stretchSpace > 0) {
            mStretchProcessor.process(layout, line, stretchSpace);
        }
        mLineRight = 0;
        mLayoutIncreased = false;
    }

    @SuppressWarnings("SameParameterValue")
    private static void insert(@NonNull Editable text, int position, @NonNull CharSequence inserted, @NonNull ViewStubOptions options) {
        final SpanBoundsTransformer transformer = options.getSpanBoundsTransformer();
        if (transformer != null) {
            transformer.onBeforeTextInserted(text, position);
        }
        text.insert(position, inserted);
        if (transformer != null) {
            transformer.onAfterTextInserted(text);
        }
    }

    private static final class RightWrapSpan extends ReplacementSpan implements WrapLineSpan {

        private final int mSize;

        RightWrapSpan(int size) {
            mSize = size;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            return mSize;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

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
            return ViewTemplate.RIGHT;
        }
    }

    private static final class MarkRightWrapSpan implements WrapLineSpan {

        private final int mSize;

        MarkRightWrapSpan(int size) {
            mSize = size;
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
            return ViewTemplate.RIGHT;
        }
    }
}
