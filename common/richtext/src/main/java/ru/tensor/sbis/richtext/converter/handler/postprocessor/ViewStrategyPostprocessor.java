package ru.tensor.sbis.richtext.converter.handler.postprocessor;

import android.content.Context;
import android.text.Editable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.view.strategy.LeftWrapLineStrategy;
import ru.tensor.sbis.richtext.view.strategy.PrefetchStrategy;
import ru.tensor.sbis.richtext.view.strategy.RightWrapLineStrategy;
import ru.tensor.sbis.richtext.view.strategy.ViewStubLayoutStrategy;
import ru.tensor.sbis.richtext.view.strategy.WrapLineStrategy;

/**
 * Постобработчик для установки стратегий рендера кастомных View внутри текста
 *
 * @author am.boldinov
 */
public final class ViewStrategyPostprocessor implements SpanPostprocessor {

    @NonNull
    private final Context mContext;

    public ViewStrategyPostprocessor(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public void process(@NonNull Editable text) {
        final ViewStubSpan[] spans = text.getSpans(0, text.length(), ViewStubSpan.class);
        for (ViewStubSpan span : spans) {
            final WrapLineStrategy strategy = createWrapLineStrategy(span);
            span.setWrapLineStrategy(strategy);
            if (strategy instanceof PrefetchStrategy) {
                final int spanPosition = text.getSpanStart(span);
                final int prefetchPosition = ((PrefetchStrategy) strategy).prefetch(text, spanPosition, span.getOptions());
                if (prefetchPosition != spanPosition) {
                    text.setSpan(span, prefetchPosition, prefetchPosition, text.getSpanFlags(span));
                }
            }
        }
    }

    @NonNull
    private WrapLineStrategy createWrapLineStrategy(@NonNull ViewStubSpan span) {
        switch (span.getAttributes().getTemplate()) {
            case LEFT:
                return new LeftWrapLineStrategy(mContext);
            case RIGHT:
                return new RightWrapLineStrategy(mContext);
            case INLINE:
            case INLINE_SIZE:
                return new ViewStubLayoutStrategy(mContext, ViewStubLayoutStrategy.Template.INLINE);
            case CENTER:
            default:
                return new ViewStubLayoutStrategy(mContext);
        }
    }
}
