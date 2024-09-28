package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.objectpool.impl.ViewFactoryConcurrentObjectPool
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.DEFAULT_SWIPE_MENU_DIVIDER_COLOR

/**
 * Пул, предназначенный для хранения до востребования и досоздания view пунктов свайп-меню и их разделителей.
 *
 * @param capacity изначальное макс. количество view в пуле
 *
 * @author us.bessonov
 */
class SwipeMenuViewPool private constructor(
    private val itemViewPool: ViewFactoryConcurrentObjectPool<View>, context: Context, private val capacity: Int
) {

    private val dividerViewPool = SwipeMenuDividerViewPool(DEFAULT_SWIPE_MENU_DIVIDER_COLOR, context, capacity)

    /** @SelfDocumented */
    fun takeItemView() = itemViewPool.take()

    /** @SelfDocumented */
    fun takeDividerView() = dividerViewPool.take()

    /** @SelfDocumented */
    fun releaseItemView(view: View) = itemViewPool.put(view)

    /** @SelfDocumented */
    fun releaseDividerView(view: View) = dividerViewPool.put(view)

    /**
     * Заполняет пул необходимым числом view
     */
    fun inflate(requestSize: Int = capacity) {
        itemViewPool.inflate(requestSize)
        dividerViewPool.inflate(requestSize)
    }

    /**
     * Очищает пул
     */
    fun flush() {
        itemViewPool.flush()
        dividerViewPool.flush()
    }

    companion object {

        /** @SelfDocumented */
        @JvmStatic
        fun createForItemsWithIcon(
            context: Context, capacity: Int
        ): SwipeMenuViewPool {
            val themedContext = getThemedContext(context)
            return SwipeMenuViewPool(DefaultMenuItemViewPool(themedContext, capacity), themedContext, capacity)
        }

        /** @SelfDocumented */
        fun createForItemsWithText(
            context: Context, capacity: Int
        ): SwipeMenuViewPool {
            val themedContext = getThemedContext(context)
            return SwipeMenuViewPool(TextMenuItemViewPool(themedContext, capacity), themedContext, capacity)
        }

        private fun getThemedContext(context: Context) = ThemeContextBuilder(
            context, defStyleAttr = R.attr.swipeableLayoutTheme, defaultStyle = R.style.DefaultSwipeableLayoutTheme
        ).build()
    }
}