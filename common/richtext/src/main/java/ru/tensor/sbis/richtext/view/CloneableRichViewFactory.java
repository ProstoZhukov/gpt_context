package ru.tensor.sbis.richtext.view;

import androidx.annotation.NonNull;

/**
 * Интерфейс для клонирования уже добавленного в иерархию {@link RichViewLayout}
 * со всеми установленными свойствами.
 * Необходимо использовать при создании {@link RichViewLayout.ViewHolder}, которые имею внутри
 * богатый текст.
 *
 * @author am.boldinov
 */
public interface CloneableRichViewFactory {

    /**
     * Клонирует View, резульат содержит идентичные свойства.
     */
    @NonNull
    RichViewLayout cloneView();

}
