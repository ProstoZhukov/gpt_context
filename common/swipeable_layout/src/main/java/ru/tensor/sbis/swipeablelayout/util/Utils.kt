/**
 * Инструменты для работы с компонентом свайп-меню
 *
 * @author us.bessonov
 */
@file:JvmName("SwipeableLayoutUtils")

package ru.tensor.sbis.swipeablelayout.util

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.design_notification.snackbar.showFloatingSnackbar
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.util.swipestate.SwipeListVm
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Показывает [Snackbar] для отмены удаления смахиванием.
 *
 * @see [showFloatingSnackbar]
 * @param onDismissCancelled обработчик события отмены удаления (по нажатию кнопки)
 * @param onDismissConfirmed обработчик события подтверждения удаления (при скрытии [Snackbar]'а, в т.ч. по истечении
 * таймаута)
 */
@JvmOverloads
fun showUndoSwipeDismissSnackBar(
    view: View,
    @StringRes messageResId: Int,
    onDismissCancelled: Runnable,
    onDismissConfirmed: Runnable = Runnable { },
    @StringRes actionResId: Int = RDesign.string.design_undo
): Snackbar {

    val callback = object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) {
                onDismissConfirmed.run()
            }
        }
    }
    return showFloatingSnackbar(
        view,
        messageResId,
        actionResId,
        { onDismissCancelled.run() },
        callback
    )
}

/**
 * Позволяет закрывать свайп-меню при прокрутке [RecyclerView], не являющегося непосредственно родительским для
 * [SwipeableLayout]
 *
 * @param swipeMenuParentRecyclerView родительский [RecyclerView], прокрутка которого закрывает меню по умолчанию
 * @param scrollResponsiveRecyclerView внешний [RecyclerView], прокрутка которого должна закрывать меню
 */
fun setRecyclerViewToCloseMenuOnScroll(
    swipeMenuParentRecyclerView: RecyclerView, scrollResponsiveRecyclerView: RecyclerView
) = with(CloseMenuOnScrollListener()) {
    swipeMenuParentRecyclerView.setTag(R.id.swipeable_layout_close_menu_on_scroll_listener, this)
    scrollResponsiveRecyclerView.addOnScrollListener(this)
}

/**
 * Получить вьюмодель, ассоциируемую с экраном, в котором отображается список элементов с поддержкой свайпа.
 */
internal fun getSwipeListVm(view: View): SwipeListVm? {
    val owner = view.findViewTreeViewModelStoreOwner()
    return owner?.let { ViewModelProvider(it, ViewModelProvider.NewInstanceFactory())[SwipeListVm::class.java] }
        ?: run {
            if (view.isAttachedToWindow && AppConfig.isDebug()) {
                Timber.w("Cannot get SwipeListVm. Swipe menu state won't be saved")
            }
            null
        }
}
