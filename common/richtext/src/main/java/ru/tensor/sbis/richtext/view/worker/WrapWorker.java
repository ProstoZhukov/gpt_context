package ru.tensor.sbis.richtext.view.worker;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.view.strategy.LineCursor;
import ru.tensor.sbis.richtext.view.strategy.ViewLayout;

/**
 * Воркер для построчного обтекания View-компонентов.
 *
 * @author am.boldinov
 */
public interface WrapWorker {

    /**
     * @return true если работа по обтеканию была завершена
     */
    boolean isFinished();

    /**
     * Выполняет работу по обтеканию View-компонента на конкретной строке.
     *
     * @param layout Layout для работы с текстом и обтекаемыми View
     * @param cursor курсор для текущей строки
     */
    void doWork(@NonNull ViewLayout layout, @NonNull LineCursor cursor);
}
