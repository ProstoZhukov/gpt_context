package ru.tensor.sbis.list.view.utils

import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.section.Options
import ru.tensor.sbis.list.view.section.Section
import ru.tensor.sbis.list.view.section.SectionsHolder

/**
 * Данные для списка [SbisList]. Можно задать позиция для скролла [positionToInitialScroll], который будет
 * выполнен при первичном показе данных.
 */
sealed interface ListData : DataInfo {
    val positionToInitialScroll: Int get() = 0

    val forceScrollToInitialPosition: Boolean get() = false
}

/**
 * Плоский список из элементов [data].
 */
data class Plain(
    val data: List<AnyItem> = emptyList(),
    override val positionToInitialScroll: Int = 0,
    private val options: Options = Options(needDrawDividerUnderFirst = true, hasTopMargin = false),
    override val forceScrollToInitialPosition: Boolean = false
) : ListData, DataInfo by SectionsHolder(listOf(Section(data, options = options))) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Plain

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}


/**
 * Секции, каждая из которых содержит плоский список. Можно задать позиция для скрола [positionToInitialScroll],
 * который будет выполнен при первичном показе данных.
 */
data class Sections internal constructor(
    @Suppress("unused") //Конструктор с таким параметом нужен для существования удобных
    // публичных конструкоров
    val _data: List<Section> = emptyList(),
    private val info: DataInfo,
    override val positionToInitialScroll: Int = 0,
    override val forceScrollToInitialPosition: Boolean
) : ListData, DataInfo by info {

    //TODO Удалить устранив использование в ArticleContentsPresenter. https://online.sbis.ru/opendoc.html?guid=55f4af80-97f9-4347-b377-4d7e17819067
    val data: List<Section> get() = info.getSections()

    /**
     * Секции [data], каждая из которых содержит плоский список. Можно задать позиция для скрола [positionToInitialScroll],
     * который будет выполнен при первичном показе данных.
     *
     * @param forceScrollToInitialPosition Требуется ли выполнить при обновлении списка безусловную прокрутку к
     * [positionToInitialScroll].
     */
    constructor(
        data: List<Section>,
        positionToInitialScroll: Int = 0,
        forceScrollToInitialPosition: Boolean = false
    ) : this(
        info = SectionsHolder(data),
        positionToInitialScroll = positionToInitialScroll,
        forceScrollToInitialPosition = forceScrollToInitialPosition
    )

    /**
     * Произвольная реализация [DataInfo], на случай, если Sections или Plain окажется не достаточно, но лучше,
     * все таки, их стараться использвать. Можно задать позиция для скрола [positionToInitialScroll],
     * который будет выполнен при первичном показе данных.
     *
     * @param forceScrollToInitialPosition Требуется ли выполнить при обновлении списка безусловную прокрутку к
     * [positionToInitialScroll].
     */
    constructor(
        info: DataInfo,
        positionToInitialScroll: Int = 0,
        forceScrollToInitialPosition: Boolean = false
    ) : this(
        emptyList<Section>(),
        info = info,
        positionToInitialScroll = positionToInitialScroll,
        forceScrollToInitialPosition = forceScrollToInitialPosition
    )
}