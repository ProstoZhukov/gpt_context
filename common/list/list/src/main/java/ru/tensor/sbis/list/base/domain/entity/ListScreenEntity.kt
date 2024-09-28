package ru.tensor.sbis.list.base.domain.entity

import ru.tensor.sbis.list.base.data.filter.PagesFiltersProvider
import ru.tensor.sbis.list.base.presentation.StubEntity
import ru.tensor.sbis.list.view.utils.ListData

/**
 * "Бизнес модель"(БМ) экрана списка. Представляет собой сами данные списка, а так же состояние пагинации
 * и данные для отображения заглушки.
 */
interface ListScreenEntity : StubEntity {

    /**
     * Конвертация данных для отображения в списке.
     *
     * @return данные списка.
     */
    fun toListData(): ListData

    /**
     * Содержит ли текущая БМ данные для отображения заглушки.
     */
    fun isStub(): Boolean = false

    /**
     * Содержит ли текущая БМ данные для отображения в списке.
     */
    fun isData(): Boolean = false

    /**
     * Находится ли текущая БМ в актуальном состоянии (например, синхронизирована с облаком)
     */
    fun isUpToDate(): Boolean = false

    /**
     * Есть ли еще страницы для подгрузки в конец списка, если флаг вернет false, то пагинация в списке отключится
     * до следующего обновления списка с флагом true.
     */
    fun hasNext(): Boolean = false

    /**
     * Есть ли еще страницы для подгрузки в начало списка, если флаг вернет false, то пагинация в списке отключится
     * до следующего обновления списка с флагом true.
     */
    fun hasPrevious(): Boolean = false

    /**
     * Очистка ранее загруженных страниц с данными. Фильтр должен остаться.
     */
    fun cleanPagesData()

    fun increasePage()

    fun decreasePage()
}

interface PagingListScreenEntity<FILTER> : ListScreenEntity, PagesFiltersProvider<FILTER>