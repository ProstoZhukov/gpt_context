package ru.tensor.sbis.list.view.utils.layout_manager

import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper

/**
 * Поставщик размера ячейки для таблицы [GridLayoutManager].
 * Учитывает наличие индикаторов прогресса, добавляемых [BottomLoadMoreProgressHelper] и [TopLoadMoreProgressHelper] и
 * подбирает правильные размер для ячейки из [dataInfo].
 */
internal class SpanSizeProvider(
    private val dataInfo: DataInfo,
    bottomLoadMoreProgressHelper: BottomLoadMoreProgressHelper,
    topLoadMoreProgressHelper: TopLoadMoreProgressHelper,
    private val checkIsTopProgress: (Int) -> Boolean
    = { position -> topLoadMoreProgressHelper.isAdded() && position == 0 },
    private val getActualPosition: (Int) -> Int = { position ->
        if (!topLoadMoreProgressHelper.isAdded()) position else position - 1
    },
    private val checkIsBottomProgress: (Int) -> Boolean
    = { position ->
        val actualItemPosition =
            if (!topLoadMoreProgressHelper.isAdded()) position else position - 1
        bottomLoadMoreProgressHelper.isAdded()
                && actualItemPosition == dataInfo.getItemsTotal()
    }
) {
    /**
     * Предоставить размер ячейки для элемента в позиции адаптера [adapterPosition] с учетом общего количества
     * доступного места в [gridLayoutManager].
     * Для элементов списка - индикаторов прогресса отдает всегда максимальный размер.
     * Если размер элементы больше чем общее количетсво доступного места в [gridLayoutManager], то выставляет ячейки
     * максимально возможный размер.
     * Если размер ячейки больше половины общего количетсво доступного места, то выставляет размер раынй половине общего
     * количетсво доступного места.
     */
    fun provide(adapterPosition: Int, gridLayoutManager: GridLayoutManager): Int {
        val spanCount = gridLayoutManager.spanCount
        if (checkIsTopProgress(adapterPosition)) return spanCount

        val actualItemPosition = getActualPosition(adapterPosition)

        if (checkIsBottomProgress(adapterPosition)) return spanCount

        val cardSpanSize = dataInfo.getSpanSize(actualItemPosition, spanCount)

        return when {
            cardSpanSize >= spanCount -> spanCount
            cardSpanSize > spanCount / 2 -> spanCount / 2
            else -> cardSpanSize
        }
    }
}