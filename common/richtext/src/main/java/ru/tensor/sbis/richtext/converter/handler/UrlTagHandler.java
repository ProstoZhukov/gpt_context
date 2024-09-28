package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;
import android.text.Editable;

import org.apache.commons.lang3.StringUtils;

import ru.tensor.sbis.common.util.UrlUtils;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.DecoratedLinkConfiguration;
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;

/**
 * Обработчик тегов ссылок: a
 *
 * @author am.boldinov
 */
public class UrlTagHandler extends MarkedTagHandler {

    /**
     * Сигнализирует о необходимости добавлять url в рендер
     */
    private final boolean mAppendUrl;
    @NonNull
    private final DecoratedLinkOpener mLinkOpener;

    public UrlTagHandler(boolean appendUrl, @NonNull DecoratedLinkConfiguration configuration) {
        mAppendUrl = appendUrl;
        mLinkOpener = configuration.provideLinkOpener();
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final String href = StringUtils.trimToEmpty(
                UrlUtils.checkAndFormatUrlWithHost(attributes.getValue("href"))
        );
        mark(stream, new MarkSpan.Url(href, mLinkOpener));
        if (mAppendUrl && !href.isEmpty()) {
            stream.append(href);
        }
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        span(stream, MarkSpan.Url.class);
    }

    @Override
    public void recycle() {

    }
}
