package ru.tensor.sbis.richtext.span.view;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.view.strategy.WrapLineStrategy;
import ru.tensor.sbis.richtext.view.strategy.bounds.SpanBoundsTransformer;

/**
 * Span для встраивания кастомных View в текст.
 * По умолчанию для работы с ним необходимо использовать {@link ru.tensor.sbis.richtext.view.RichViewLayout}
 *
 * @author am.boldinov
 */
public final class ViewStubSpan extends MarkSpan {

    @NonNull
    private BaseAttributesVM mAttributes;
    @Nullable
    private WrapLineStrategy mWrapLineStrategy;
    @NonNull
    private final ViewStubOptions mOptions = new ViewStubOptions();

    /**
     * Создает {@link ViewStubSpan}
     *
     * @param vm данные для рендера
     */
    public ViewStubSpan(@NonNull BaseAttributesVM vm) {
        mAttributes = vm;
    }

    /**
     * Возвращает данные для рендера {@link ViewStubSpan}
     */
    @NonNull
    public BaseAttributesVM getAttributes() {
        return mAttributes;
    }

    /**
     * Устанавливает данны для рендера {@link ViewStubSpan}
     */
    public void setAttributes(@NonNull BaseAttributesVM vm) {
        mAttributes = vm;
    }

    /**
     * Устанавливает стратегию обтекания текстом
     */
    public void setWrapLineStrategy(@Nullable WrapLineStrategy strategy) {
        mWrapLineStrategy = strategy;
    }

    /**
     * Возвращает стратегию обтекания текстом. Если метод возвращает null, то {@link ViewStubSpan}
     * вставлен без обтекания и необходимо использовать логику по умолчанию.
     */
    @Nullable
    public WrapLineStrategy getWrapLineStrategy() {
        return mWrapLineStrategy;
    }

    /**
     * Возвращает набор опций для вставки {@link ViewStubSpan} в текст
     */
    @NonNull
    public ViewStubOptions getOptions() {
        return mOptions;
    }

    /**
     * Устанавливает объект для ручной трасформации границ спанов при изменении текста в процессе измерений.
     */
    public void setSpanBoundsTransformer(@Nullable SpanBoundsTransformer spanBoundsTransformer) {
        mOptions.setSpanBoundsTransformer(spanBoundsTransformer);
    }

    /**
     * Устанавливает смещение View относительно параграфа, см {@link android.text.style.LeadingMarginSpan}
     */
    public void setLeadingOffset(int offset) {
        mOptions.setLeadingOffset(offset);
    }

    /**
     * Устанавливает максимальную высоту для View.
     */
    public void setMaxHeight(int height) {
        mOptions.setMaxHeight(height);
    }

    @NonNull
    @Override
    public Object getRealSpan() {
        return this;
    }
}
