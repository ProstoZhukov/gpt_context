package ru.tensor.sbis.swipeablelayout.util

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithTimeout

/**
 * Инструмент для обеспечения закрытия свайп-меню при прокрутке [RecyclerView]
 *
 * @author us.bessonov
 */
internal class CloseMenuOnScrollListener : RecyclerView.OnScrollListener() {

    /** @SelfDocumented */
    var openedSwipeableLayout: SwipeableLayout? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy != 0) {
            val lastEvent = openedSwipeableLayout?.lastEvent
            if (lastEvent !is Dismissed && lastEvent !is DismissedWithTimeout) {
                openedSwipeableLayout?.close()
            }
            openedSwipeableLayout = null
        }
    }
}