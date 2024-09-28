package ru.tensor.sbis.design.design_menu.view.shadow

import androidx.recyclerview.widget.RecyclerView

/**
 * Слушатель касания верхней или нижней границы скроллом списка.
 *
 *  @param scrollDirection направление, в котором проверяется возможность дальнейшего скролла ресайклера
 *  @param onBorderReached колбэк, вызываемый при достижении списком заданной границы
 *  @param onScrolled колбэк, вызываемый при скролле если граница не была достигнута
 *
 * @author ra.geraskin
 */
internal class ScrollBoundsListener(
    private val scrollDirection: Int,
    private val onBorderReached: () -> Unit,
    private val onScrolled: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val canScroll = recyclerView.canScrollVertically(scrollDirection)
        if (canScroll)
            onScrolled()
        else
            onBorderReached()
        super.onScrolled(recyclerView, dx, dy)
    }

}