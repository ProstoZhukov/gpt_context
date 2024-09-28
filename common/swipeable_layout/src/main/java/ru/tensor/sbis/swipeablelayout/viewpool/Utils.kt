/**
 * Инструменты по работе с пулом view пунктов свайп-меню.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.swipeablelayout.viewpool

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.swipeable_layout.R

/**
 * Получить экземпляр [SwipeMenuViewPool], привязанный к конкретному [RecyclerView], для типовых элементов (с иконкой),
 * используемый при отсутствии явно заданного пула элементов.
 */
internal fun tryGetDefaultSwipeMenuItemViewPool(view: View): SwipeMenuViewPool? =
    findViewParent<RecyclerView>(view)?.let { parent ->
        parent.getTag(POOL_TAG_KEY) as SwipeMenuViewPool?
            ?: SwipeMenuViewPool.createForItemsWithIcon(view.context, DEFAULT_SWIPE_MENU_POOL_SIZE)
                .also { parent.setTag(POOL_TAG_KEY, it) }
    }

private val POOL_TAG_KEY = R.id.swipeable_layout_default_menu_item_pool
private const val DEFAULT_SWIPE_MENU_POOL_SIZE = 40