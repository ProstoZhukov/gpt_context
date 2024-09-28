package ru.tensor.sbis.design.list_utils.util

import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.R

/** Направление скрола вверх */
internal const val SCROLL_DIRECTION_UP = -1

/**
 * Инструмент для отображения тени под шапкой списка.
 * Управляет значением elevation для целевого [View], в зависимости от того, можно ли прокрутить список вверх.
 *
 * @author us.bessonov
 */
class ListHeaderElevationHelper(
    private var headerView: View? = null,
    @DimenRes
    private val elevation: Int = R.dimen.elevation_high
) : RecyclerView.OnScrollListener() {

    private val shadowElevation by lazy {
        headerView!!.resources.getDimension(elevation)
    }

    /**
     * Задаёт [View], у которого должна отображаться тень при возможности прокрутки вверх.
     * Если ранее был задан другой [View], у которого отображается тень, то она будет показана у переданного [View], а
     * у предыдущего тень будет скрыта
     */
    fun setHeaderView(view: View?) {
        if (isShadowVisible()) {
            hideShadow()
            headerView = view
            showShadow()
        } else {
            headerView = view
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isShadowVisible() && !recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
            hideShadow()
        } else if (!isShadowVisible() && recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
            showShadow()
        }
    }

    private fun isShadowVisible() : Boolean {
        return headerView?.let { it.elevation != 0f }
            ?: false
    }

    private fun hideShadow() {
        headerView?.elevation = 0f
    }

    private fun showShadow() {
        headerView?.elevation = shadowElevation
    }
}