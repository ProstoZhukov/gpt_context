package ru.tensor.sbis.richtext.span.view;

import org.jetbrains.annotations.Nullable;

import ru.tensor.sbis.richtext.view.strategy.bounds.SpanBoundsTransformer;

/**
 * Набор опций для конфигурации текста при размещении View
 *
 * @author am.boldinov
 */
public final class ViewStubOptions {

    @Nullable
    private SpanBoundsTransformer mSpanBoundsTransformer;
    private int mLeadingOffset;
    private int mMaxHeight;

    /**
     * Возвращает объект для ручной трасформации границ спанов при изменении текста.
     * Если границы изменять не требуется, метод вернет null.
     */
    @Nullable
    public SpanBoundsTransformer getSpanBoundsTransformer() {
        return mSpanBoundsTransformer;
    }

    void setSpanBoundsTransformer(@Nullable SpanBoundsTransformer spanBoundsTransformer) {
        mSpanBoundsTransformer = spanBoundsTransformer;
    }

    /**
     * Возвращает смещение View относительно параграфа, см {@link android.text.style.LeadingMarginSpan}
     */
    public int getLeadingOffset() {
        return mLeadingOffset;
    }

    void setLeadingOffset(int offset) {
        mLeadingOffset = offset;
    }

    /**
     * Возвращает максимальную высоту для View.
     */
    public int getMaxHeight() {
        return mMaxHeight;
    }

    void setMaxHeight(int height) {
        mMaxHeight = height;
    }
}
