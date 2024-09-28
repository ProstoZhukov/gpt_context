package ru.tensor.sbis.list.view.utils.layout_manager

import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper

/**
 * Реализация определителя количества span ячейки для группы [GridLayoutManager].
 * Основную работу делает [spanSizeProvider].
 */
internal class ItemSpanSizeLookup(
    private val dataInfo: DataInfo,
    bottomLoadMoreProgressHelper: BottomLoadMoreProgressHelper,
    topLoadMoreProgressHelper: TopLoadMoreProgressHelper,
    private val spanSizeProvider: SpanSizeProvider = SpanSizeProvider(
        dataInfo,
        bottomLoadMoreProgressHelper,
        topLoadMoreProgressHelper
    )
) : GridLayoutManager.SpanSizeLookup() {

    private lateinit var gridLayoutManager: GridLayoutManager

    override fun getSpanSize(position: Int) = spanSizeProvider.provide(position, gridLayoutManager)

    /**
     * Задаем [gridLayoutManager], который будет использоваться в [spanSizeProvider].
     */
    fun setGridLayoutManager(gridLayoutManager: GridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager
    }

    /**
     * В строке не модержитьс метсо под еще один элемент любого размера.
     */
    fun hasNoSpanSpaceAtRight(position: Int): Boolean {
        val groupIndex = getSpanGroupIndex(position, gridLayoutManager.spanCount)
        var occupied = getSpanSize(position)
        var pos = position
        while (--pos >= 0 && getSpanGroupIndex(
                pos,
                gridLayoutManager.spanCount
            ) == groupIndex
        ) occupied += getSpanSize(pos)

        return occupied == gridLayoutManager.spanCount
    }
}

//todo Нужно сделать общий менеджер элементов, который бы объединял sectionsHolder, bottomLoadMoreProgressHelper и topLoadMoreProgressHelper