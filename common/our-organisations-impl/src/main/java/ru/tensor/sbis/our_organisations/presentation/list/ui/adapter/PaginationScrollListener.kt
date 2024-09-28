package ru.tensor.sbis.our_organisations.presentation.list.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *  Генерация событий для подгрузки страниц во время скролла.
 *
 *  @author mv.ilin
 */
class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val pageSize: Int,
    private val requestPage: (next: Boolean) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            if (layoutManager.itemCount - layoutManager.findLastVisibleItemPosition() < pageSize) {
                requestPage(true)
            }
        } else if (dy < 0) {
            if (layoutManager.findFirstVisibleItemPosition() < pageSize) {
                requestPage(false)
            }
        }
    }
}
