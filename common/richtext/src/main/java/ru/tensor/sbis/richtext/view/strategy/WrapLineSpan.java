package ru.tensor.sbis.richtext.view.strategy;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Span для маркировки обтекания
 *
 * @author am.boldinov
 */
interface WrapLineSpan {

    /**
     * @return размер занимаемого пространства на строке (ширина)
     */
    int getSize();

    /**
     * @return размер исходной обтекаемой View
     */
    int getViewSize();

    /**
     * @return шаблон обтекания
     */
    @NonNull
    ViewTemplate getTemplate();
}
