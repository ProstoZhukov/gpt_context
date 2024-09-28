package ru.tensor.sbis.design_selection.ui.content.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionHeaderShadowDelegate

/** Направление скрола вверх */
internal const val SCROLL_DIRECTION_UP = -1

/**
 * Вспомогательная реализация для наложения тени от [shadowView] на список контента выбора,
 * когда он находится в проскролленом состоянии.
 * Исключает случаи промелькивания тени при программном скролле,
 * когда при отмене выбора ячейки добавляются в начало списка.
 *
 * @author vv.chekurda
 */
class SelectionElevationHelper :
    RecyclerView.OnScrollListener(),
    SelectionHeaderShadowDelegate.ScrollableContent {

    private var shadowView: View? = null

    private val shadowElevation by lazy {
        shadowView!!.resources.getDimension(R.dimen.elevation_high)
    }

    override fun setShadowView(view: View?) {
        if (isShadowVisible()) {
            hideShadow()
            shadowView = view
            showShadow()
        } else {
            shadowView = view
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isShadowVisible() && !recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
            hideShadow()
        } else if (!isShadowVisible() && recyclerView.canScrollVertically(SCROLL_DIRECTION_UP) &&
            recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
        ) {
            showShadow()
        }
    }

    private fun isShadowVisible(): Boolean =
        shadowView?.let { it.elevation != 0f } ?: false

    private fun hideShadow() {
        shadowView?.elevation = 0f
    }

    private fun showShadow() {
        shadowView?.elevation = shadowElevation
    }
}