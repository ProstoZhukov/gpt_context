package ru.tensor.sbis.list.view

import android.content.Context
import androidx.annotation.ColorInt
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain

/**
 * Держит ссылку на [data], обеспечивает синхронизацию доступа и измения данных к ней.
 */
class ListDataHolder : DataInfo {

    private var data: DataInfo = Plain()

    @Synchronized
    fun setData(data: DataInfo) {
        this.data = data
    }

    @Synchronized
    override fun hasMoreThanOneSection() = data.hasMoreThanOneSection()

    @Synchronized
    override fun getItemsTotal() = data.getItemsTotal()

    @Synchronized
    override fun isEmpty() = data.isEmpty()

    @Synchronized
    override fun hasDividers(absoluteItemPosition: Int) = data.hasDividers(absoluteItemPosition)

    @Synchronized
    override fun isFirstInSection(absoluteItemPosition: Int) =
        data.isFirstInSection(absoluteItemPosition)

    @Synchronized
    override fun isLastItemInSection(absoluteItemPosition: Int) =
        data.isLastItemInSection(absoluteItemPosition)

    @Synchronized
    override fun runIfIsFirstItemInSectionAndHasLine(
        absoluteItemPosition: Int,
        function: (color: Int) -> Unit
    ) = data.runIfIsFirstItemInSectionAndHasLine(absoluteItemPosition, function)

    @Synchronized
    override fun isMovable(absoluteItemPosition: Int) = data.isMovable(absoluteItemPosition)

    @Synchronized
    override fun getSpanSize(absoluteItemPosition: Int, returnIfIsNotCard: Int) =
        data.getSpanSize(absoluteItemPosition, returnIfIsNotCard)

    @Synchronized
    override fun needDrawDividerUnderFirst(absoluteItemPosition: Int) =
        data.needDrawDividerUnderFirst(absoluteItemPosition)

    @Synchronized
    override fun needDrawDividerUpperLast(absoluteItemPosition: Int) =
        data.needDrawDividerUpperLast(absoluteItemPosition)

    @Synchronized
    override fun getItems() = data.getItems()

    //todo удалить https://online.sbis.ru/opendoc.html?guid=55f4af80-97f9-4347-b377-4d7e17819067
    @Synchronized
    override fun getSections() = data.getSections()

    @ColorInt
    @Synchronized
    override fun getBackgroundResId(context: Context) = data.getBackgroundResId(context)

    @Synchronized
    override fun isNeedExpandLastSection() = data.isNeedExpandLastSection()

    @Synchronized
    override fun getMarginDp(
        context: Context,
        position: Int,
        hasNoItemAtLeft: Boolean,
        hasNoSpanSpaceAtRight: Boolean,
        isFirstGroupInSection: Boolean,
        isInLastGroup: Boolean
    ) = data.getMarginDp(
        context,
        position,
        hasNoItemAtLeft,
        hasNoSpanSpaceAtRight,
        isFirstGroupInSection,
        isInLastGroup
    )

    override fun isCard(position: Int) = data.isCard(position)

    override fun getCardOption(position: Int) = data.getCardOption(position)

    override fun hasCollapsibleItems(): Boolean = data.hasCollapsibleItems()

    override fun getIndexOfSection(absoluteItemPosition: Int) =
        data.getIndexOfSection(absoluteItemPosition)

    /**
     * Вернуть копию текущей коллекции [data], в которой поменяли местами элементы
     * [AnyItem] по индексам в адаптере [p1] и [p2]. Реализация не должна содержать сайд-эффектов.
     */
    fun reorder(p1: Int, p2: Int): ListData? {
        return with(data) {
            if (this is ListData) this.reorder(this, p1, p2) else null
        }
    }

    override fun reorder(listData: ListData, p1: Int, p2: Int): ListData? = data.reorder(listData, p1, p2)
}