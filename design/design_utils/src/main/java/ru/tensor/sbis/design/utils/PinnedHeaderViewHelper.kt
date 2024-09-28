package ru.tensor.sbis.design.utils

import android.view.View
import androidx.annotation.Px
import com.google.android.material.appbar.AppBarLayout
import androidx.core.view.isVisible
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import kotlin.math.abs
import ru.tensor.sbis.design.R

/**
 * Обработчик изменения положения закреплённого view под шапкой. Позволяет расположить под ним дополнительные элементы.
 * В обработчик передаётся расстояние от верха контейнера до нижней границы закреплённого view, либо его предполагаемой
 * верхней границы, в зависимости от его видимости
 */
typealias PinnedHeaderViewOffsetChangedListener = (pinnedViewOffset: Int) -> Unit

/**
 * Инструмент для закрепления view под шапкой, которая может сворачиваться в ScrollToTop
 *
 * @param updateListViewTopMargin устанавливает отступ сверху view списка, исходя из базового значения
 * ([updateListMargin]), а также добавочного отступа, обусловленного присутствием закреплённого view
 * @param [onOffsetChanged] дополнительное действие, которое требуется выполнить при изменении offset'а [AppBarLayout]
 *
 * @author us.bessonov
 */
class PinnedHeaderViewHelper(
    private val pinnedView: View,
    private val scrollToTopPanel: View? = null,
    private val updateListViewTopMargin: (margin: Int) -> Unit,
    private val onOffsetChanged: PinnedHeaderViewOffsetChangedListener? = null
) : AppBarLayout.OnOffsetChangedListener {

    @Px
    private val scrollToTopPanelHeight: Int =
        pinnedView.resources.getDimensionPixelSize(R.dimen.scroll_to_top_panel_height)

    @Px
    private var baseListMargin = 0

    /**
     * Обновляет отступ сверху view списка, учитывая как значение [baseMarginTop], так и добавочный отступ, зависящий от
     * видимости закреплённого view
     */
    fun updateListMargin(@Px baseMarginTop: Int) {
        baseListMargin = baseMarginTop
        updateListTopMargin()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout ?: return
        val topOffset = getTopOffset(appBarLayout, verticalOffset)
        pinnedView.updateTopMargin(topOffset)
        onOffsetChanged?.invoke(topOffset + getPinnedViewEffectiveHeight())
        updateListTopMargin()
    }

    @Px
    private fun getTopOffset(appBarLayout: AppBarLayout, verticalOffset: Int): Int {
        val verticalOffsetAbs = abs(verticalOffset)
        return if (appBarLayout.totalScrollRange == 0) {
            appBarLayout.height - verticalOffsetAbs
        } else {
            val appBarVisibleHeight = appBarLayout.totalScrollRange - verticalOffsetAbs
            if (scrollToTopPanel?.isVisible == true && appBarVisibleHeight <= scrollToTopPanelHeight) {
                scrollToTopPanelHeight
            } else {
                appBarVisibleHeight
            }
        }
    }

    private fun updateListTopMargin() {
        updateListViewTopMargin(baseListMargin + getPinnedViewEffectiveHeight())
    }

    @Px
    private fun getPinnedViewEffectiveHeight() = pinnedView.measuredHeight
        .takeIf { pinnedView.isVisible }
        ?: 0
}