package ru.tensor.sbis.communicator.common.util.layout_manager

import android.content.Context
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager

/**
 * Реализация LayoutManager-а для списка контактов и диалогов/каналов
 * Даёт возможность убирать скролл в случае отображения заглушки
 *
 * @author da.zhukov
 */
class CommunicatorLayoutManager(
    context: Context,
    scrollHelper: ScrollHelper?,
    progressViewType: Int = BaseTwoWayPaginationAdapter.HOLDER_PROGRESS,
    emptyViewType: Int = BaseTwoWayPaginationAdapter.HOLDER_EMPTY
) : PaginationLayoutManager(context = context, scrollHelper = scrollHelper,
    progressViewType = progressViewType, emptyViewType = emptyViewType) {

    var isVerticalScrollEnabled = true

    override fun canScrollVertically() = isVerticalScrollEnabled && super.canScrollVertically()
}