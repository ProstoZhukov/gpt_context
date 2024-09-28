package ru.tensor.sbis.crud4.domain

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import ru.tensor.sbis.crud4.ListComponentViewViewModel
import ru.tensor.sbis.list.view.section.Options.Companion.defaultValue
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Маппер элемента коллекции контроллер в элемент списочного компонента.
 * Описание аргументов см в [ListComponentViewViewModel].
 */
@AnyThread
interface ItemInSectionMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> :
    ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER> where SOURCE_ITEM : DecoratedProtocol<IDENTIFIER> {
    /**
     * Преобразует модель элемента из контроллера в настройки секции для этого элемента. Чтобы элементы были внутри
     * одной секции метод должен возвращать один и тот же инстанс настроек секции, это необходимо для случаев когда
     * у нескольких секций одинаковые натройки.
     */
    @WorkerThread
    fun mapSection(item: SOURCE_ITEM): SectionOptions = defaultValue
}