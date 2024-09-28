package ru.tensor.sbis.richtext.view.strategy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.span.ParagraphLineSpacingSpan;
import ru.tensor.sbis.richtext.span.view.ViewStubOptions;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;
import ru.tensor.sbis.richtext.view.strategy.bounds.SpanBoundsTransformer;

/**
 * Стратегия по вставке View без обтекания в любую часть текста.
 * View элементы вставляются таким образом, что разбивают текст на разные параграфы
 *
 * @author am.boldinov
 */
public class ViewStubLayoutStrategy implements WrapLineStrategy, PrefetchStrategy {

    public enum Template {
        INLINE, // с обтеканием на текущей строке (между букв)
        NEW_LINE // с обтеканием на новой строке
    }

    private static final String STUB_TEXT = String.valueOf(HtmlHelper.VIEW_SYMBOL);

    @NonNull
    private final Template mTemplate;
    @Px
    private final int mExternalMargin;
    @Nullable
    private ViewStubLayoutSpan mPrefetchSpan;
    private int mSpanPosition;
    private int mSpanHeight;
    private int mSpanWidth;

    public ViewStubLayoutStrategy(@NonNull Context context) {
        this(context, Template.NEW_LINE);
    }

    public ViewStubLayoutStrategy(@NonNull Context context, @NonNull Template template) {
        mExternalMargin = context.getResources().getDimensionPixelSize(R.dimen.richtext_image_stub_text_margin);
        mTemplate = template;
    }

    @Override
    public boolean wrap(@NonNull ViewLayout layout, @NonNull View view, @NonNull ViewStubOptions options,
                        @NonNull LineCursor cursor, int spanStart) {
        final Editable text = layout.getText();
        if (mPrefetchSpan == null) {
            final int spanWidth = getSpanWidth(view, layout.getWidthMeasureSpec());
            if (mTemplate == Template.NEW_LINE && !layout.computeFitToLine(spanWidth, cursor.get(), ViewTemplate.CENTER)) {
                return false;
            }
            spanStart = validateSpanPosition(text, spanStart);
            insertStub(text, spanStart, options);
            final ViewStubLayoutSpan span = new ViewStubLayoutSpan(mTemplate);
            span.mWidth = spanWidth;
            span.mViewWidth = getSpanViewWidth(view);
            span.mHeight = getSpanHeight(view);
            // параграф ломает размещение View
            removeParagraphSpans(text, spanStart - 2, spanStart);
            if (mTemplate == Template.NEW_LINE) {
                span.mWidth -= layout.getParagraphLeft(cursor.get());
            }
            text.setSpan(span, spanStart, spanStart + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            cursor.moveTo(layout.getLineForOffset(spanStart));
            mSpanPosition = spanStart;
            mSpanHeight = span.mHeight;
            mSpanWidth = span.mWidth;
        } else {
            mSpanPosition = text.getSpanStart(mPrefetchSpan);
            if (mTemplate == Template.NEW_LINE) {
                final int spanLine;
                if (mSpanPosition != spanStart) {
                    spanLine = layout.getLineForOffset(mSpanPosition);
                    cursor.moveTo(spanLine);
                } else {
                    spanLine = cursor.get();
                }
                final int paraLeft = layout.getParagraphLeft(spanLine);
                if (paraLeft > 0) {
                    final int spanEnd = text.getSpanEnd(mPrefetchSpan);
                    final int flags = text.getSpanFlags(mPrefetchSpan);
                    mPrefetchSpan.mWidth -= paraLeft;
                    text.setSpan(mPrefetchSpan, mSpanPosition, spanEnd, flags);
                }
            }
            mSpanHeight = mPrefetchSpan.mHeight;
            mSpanWidth = mPrefetchSpan.mWidth;
        }
        return true;
    }

    @Override
    public void layout(@NonNull RichViewLayout.LayoutParams layoutParams, @NonNull ViewLayout layout, int line) {
        layoutParams.topOffset = layout.getLineBottom(line) - mSpanHeight;
        if (mTemplate == Template.NEW_LINE) {
            layoutParams.leftOffset = layout.getParagraphLeft(line);
            layoutParams.topOffset += mExternalMargin;
        } else {
            layoutParams.leftOffset = Math.round(layout.getPrimaryHorizontal(mSpanPosition));
            if (mSpanWidth < layout.getWidth()) { // если отрисовка не на весь Layout
                layoutParams.leftOffset += mExternalMargin / 2;
            }
            layoutParams.topOffset += mExternalMargin / 2;
        }
    }

    @Override
    public int prefetch(@NonNull Editable text, int position, @NonNull ViewStubOptions options) {
        position = validateSpanPosition(text, position);
        insertStub(text, position, options);
        mPrefetchSpan = new ViewStubLayoutSpan(mTemplate);
        text.setSpan(mPrefetchSpan, position, position + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return position;
    }

    @Override
    public void onViewMeasured(@NonNull View view, int widthMeasureSpec) {
        if (mPrefetchSpan != null) {
            mPrefetchSpan.mWidth = getSpanWidth(view, widthMeasureSpec);
            mPrefetchSpan.mViewWidth = getSpanViewWidth(view);
            mPrefetchSpan.mHeight = getSpanHeight(view);
        }
    }

    @Override
    public void onWrapCompleted(@NonNull ViewLayout layout, int line, int stretchSpace) {
        // ignore
    }

    private int getSpanWidth(@NonNull View view, int widthMeasureSpec) {
        int width = getSpanViewWidth(view);
        final int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        if (width < maxWidth) {
            if (mTemplate == Template.NEW_LINE) {
                width = maxWidth;
            } else {
                width = Math.min(width + mExternalMargin, maxWidth);
            }
        }
        return width;
    }

    private int getSpanViewWidth(@NonNull View view) {
        return view.getMeasuredWidth();
    }

    private int getSpanHeight(@NonNull View view) {
        int height = view.getMeasuredHeight();
        // отступы до предыдущей и до следующей строки
        if (mTemplate == Template.NEW_LINE) {
            height += mExternalMargin * 2;
        } else {
            height += mExternalMargin;
        }
        return height;
    }

    private static void insertStub(@NonNull Editable text, int position, @NonNull ViewStubOptions options) {
        final SpanBoundsTransformer transformer = options.getSpanBoundsTransformer();
        if (transformer != null) {
            transformer.onBeforeTextInserted(text, position);
        }
        text.insert(position, STUB_TEXT);
        if (transformer != null) {
            transformer.onAfterTextInserted(text);
        }
    }

    private static int validateSpanPosition(@NonNull Editable text, int start) {
        if (text.length() > 0) {
            while (start < text.length()) {
                final int next = text.nextSpanTransition(start, start + 2, WrapLineSpan.class);
                if (next > start + 1) {
                    break;
                } else {
                    start++;
                }
            }
        }
        return start;
    }

    private static void removeParagraphSpans(@NonNull Editable text, int from, int to) {
        from = Math.max(from, 0);
        if (from != to) {
            final ParagraphLineSpacingSpan[] spans = text.getSpans(from, to, ParagraphLineSpacingSpan.class);
            for (ParagraphLineSpacingSpan span : spans) {
                text.removeSpan(span);
            }
        }
    }

    private static final class ViewStubLayoutSpan extends ReplacementSpan implements LineHeightSpan.WithDensity, WrapLineSpan {

        private int mWidth;
        private int mViewWidth;
        private int mHeight;
        private int mCurrentLineHeight;
        private int mPrevLineHeight;
        @NonNull
        private final Template mTemplate;

        private ViewStubLayoutSpan(@NonNull Template template) {
            mTemplate = template;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            mCurrentLineHeight = 0;
            mPrevLineHeight = 0;
            if (fm != null) {
                paint.getFontMetricsInt(fm); // важно скинуть font metrics, могут прийти измененные ранее
            }
            return mWidth;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        }

        @Override
        public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm, TextPaint paint) {
            if (fm != null && mHeight > 0) {
                final Spanned spanned = (Spanned) text;
                final int spanStart = spanned.getSpanStart(this);
                if (spanStart >= start && spanStart < end) {
                    mCurrentLineHeight = mHeight;
                    final int topOffset = mCurrentLineHeight - (fm.bottom - fm.top);
                    if (topOffset > 0) {
                        fm.top -= topOffset;
                        fm.ascent = fm.top;
                        fm.descent = fm.bottom; // StaticLayout при измерении уменьшает заданную высоту из-за отступа
                    }
                } else {
                    if (spanStart < start && lineHeight > 0 && lineHeight - mPrevLineHeight == mCurrentLineHeight
                            && fm.bottom - fm.top == mCurrentLineHeight) {
                        paint.getFontMetricsInt(fm);
                    }
                }
            }
            mPrevLineHeight = lineHeight;
        }

        @Override
        public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {

        }

        @Override
        public int getSize() {
            return mWidth;
        }

        @Override
        public int getViewSize() {
            return mViewWidth;
        }

        @NonNull
        @Override
        public ViewTemplate getTemplate() {
            if (mTemplate == Template.INLINE) {
                return ViewTemplate.INLINE;
            }
            return ViewTemplate.CENTER;
        }
    }
}