package ru.tensor.sbis.business.common.ui.utils

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.common.util.scroll.ScrollHelper

/**
 * Реализация [RecyclerView.OnScrollListener], делегирующая событие скролла [ScrollHelper]
 *
 * @param scrollHelper объект, которому делегируется событие скролла
 */
internal class ScrollListenerForScrollHelper(private val scrollHelper: ScrollHelper) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        scrollHelper.onScroll(dy, recyclerView.computeVerticalScrollOffset())
    }
}