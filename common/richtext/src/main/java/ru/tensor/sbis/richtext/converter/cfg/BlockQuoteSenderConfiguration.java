package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;
import ru.tensor.sbis.design.theme.res.SbisColor;
import ru.tensor.sbis.design.theme.res.SbisDimen;

/**
 * Конфигурация параметров автора цитаты, отображается перед содержимым в виде заголовка
 *
 * @author am.boldinov
 */
public interface BlockQuoteSenderConfiguration {

    /**
     * @return ссылка на ресурс с размером текста для автора цитаты
     */
    @NonNull
    SbisDimen getSenderTextSize();

    /**
     * @return ссылка на ресурс с цветом текста для автора цитаты
     */
    @NonNull
    SbisColor getSenderTextColor();
}
