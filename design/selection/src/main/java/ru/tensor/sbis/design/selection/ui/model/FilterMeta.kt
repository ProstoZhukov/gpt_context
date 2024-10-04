package ru.tensor.sbis.design.selection.ui.model

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId

/**
 * Направление, в котором запрашивается очередная страница
 */
enum class PageDirection { PREVIOUS, NEXT }

/**
 * Данные, на основе которых нужно создать фильтр
 *
 * @author ma.kolpakov
 */
sealed class FilterMeta<out DATA : SelectorItemModel, out ANCHOR> {
    /**
     * Поисковой запрос
     */
    abstract val query: String

    /**
     * Якорь для постраничной загрузки
     */
    abstract val anchor: ANCHOR?

    /**
     * Число элементов на странице
     */
    abstract val itemsOnPage: Int

    /**
     * Идентификатор элемента списка, от которого был переход вглубь по иерархии
     */
    abstract val parent: SelectorItemId?

    /**
     * Индекс страницы
     */
    abstract val pageIndex: Int

    /**
     * Является ли запрашиваемая страница следующей, либо предыдущей
     */
    abstract val pageDirection: PageDirection
}

/**
 * Данные, на основе которых нужно создать фильтр одиночного выбора
 */
data class SingleFilterMeta<out ANCHOR> internal constructor(
    override val query: String,
    override val anchor: ANCHOR?,
    override val itemsOnPage: Int,
    override val parent: SelectorItemId?,
    override val pageIndex: Int,
    override val pageDirection: PageDirection
) : FilterMeta<Nothing, ANCHOR>()

/**
 * Данные, на основе которых нужно создать фильтр множественного выбора
 */
data class MultiFilterMeta<out DATA : SelectorItemModel, out ANCHOR> internal constructor(
    override val query: String,
    override val anchor: ANCHOR?,
    override val itemsOnPage: Int,
    override val parent: SelectorItemId?,
    override val pageIndex: Int,
    override val pageDirection: PageDirection,
    /**
     * Список выбранных элементов
     */
    val selection: List<DATA>,
    /**
     * Список доступных для выбора элементов
     */
    val items: List<DATA>
) : FilterMeta<DATA, ANCHOR>()