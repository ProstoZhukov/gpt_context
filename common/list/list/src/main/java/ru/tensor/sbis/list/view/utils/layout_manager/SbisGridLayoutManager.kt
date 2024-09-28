package ru.tensor.sbis.list.view.utils.layout_manager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.list.view.section.DataInfo
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper

/**
 * Реализация [GridLayoutManager] отображающая количество колонок в соответствии с шириной использующего ее вью списка.
 * Максимально - 3 элемента в строке.
 */
internal class SbisGridLayoutManager(
    context: Context,
    info: DataInfo,
    bottomLoadMoreProgressHelper: BottomLoadMoreProgressHelper,
    topLoadMoreProgressHelper: TopLoadMoreProgressHelper,
    private val spanCountsCalculator: SpanCountsCalculator = SpanCountsCalculator(context.resources.displayMetrics.density),
    private val customSpanSizeLookup: ItemSpanSizeLookup = ItemSpanSizeLookup(
        info,
        bottomLoadMoreProgressHelper,
        topLoadMoreProgressHelper
    ),
    private val isFirstGroupInSection: IsFirstGroupInSection = IsFirstGroupInSection(
        info,
        customSpanSizeLookup
    ),
    private val isInLastGroup: (Int, SbisGridLayoutManager) -> Boolean = { position, manager ->
        customSpanSizeLookup.getSpanGroupIndex(
            position,
            manager.spanCount
        ) == customSpanSizeLookup.getSpanGroupIndex(
            info.getItemsTotal() - 1,
            manager.spanCount
        )
    }
) : GridLayoutManager(context, defaultSpanCount) {

    private var isScrollable = true

    init {
        spanSizeLookup = customSpanSizeLookup
        customSpanSizeLookup.setGridLayoutManager(this)
    }

    /**
     * Фикс для https://issuetracker.google.com/issues/37007605
     */
    override fun supportsPredictiveItemAnimations() = false

    override fun canScrollVertically(): Boolean {
        return if (isScrollable) {
            super.canScrollVertically()
        } else {
            false
        }
    }

    /**
     * Указать размер вью для определения количества элементов в строке.
     */
    fun setViewWidth(width: Int) {
        spanCount = spanCountsCalculator.calculate(width)
    }

    /** @SelfDocumented */
    fun setScrollEnabled(enabled: Boolean) {
        isScrollable = enabled
    }

    /**
     * Находится ли элемент в позиции [position] в первой группе(строке) секции.
     */
    fun isFirstGroupInSection(position: Int) = isFirstGroupInSection(position, spanCount)
    /**
     * Находится ли элемент в позиции [position] в самой последней группе.
     */
    fun isInLastGroup(position: Int) = isInLastGroup(position, this)
    fun hasNoItemAtLeft(position: Int) = customSpanSizeLookup.getSpanIndex(position, spanCount) == 0

    fun hasNoSpanSpaceAtRight(position: Int) = customSpanSizeLookup.hasNoSpanSpaceAtRight(position)
}

private const val defaultSpanCount = 1