package ru.tensor.sbis.richtext.converter.cfg;

import android.content.Context;
import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkRepository;

/**
 * Конфигурация декорированных ссылок {@link ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkSpan}
 *
 * @author am.boldinov
 */
public interface DecoratedLinkConfiguration {

    /**
     * @return репозиторий для получения данных по декорированной ссылке
     */
    @NonNull
    DecoratedLinkRepository provideRepository(@NonNull Context context);

    /**
     * @return объект, реализующий открытие декорированных ссылок
     */
    @NonNull
    DecoratedLinkOpener provideLinkOpener();
}
