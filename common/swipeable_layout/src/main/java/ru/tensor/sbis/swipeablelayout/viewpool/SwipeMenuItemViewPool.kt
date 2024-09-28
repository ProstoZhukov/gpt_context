package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import ru.tensor.sbis.objectpool.impl.ViewFactoryConcurrentObjectPool
import ru.tensor.sbis.swipeablelayout.DEFAULT_SWIPE_MENU_ITEM_LAYOUT

/**
 * Пул для создания view пунктов свайп-меню с иконкой.
 *
 * @param capacity изначальное макс. количество view в пуле
 *
 * @author us.bessonov
 */
internal class DefaultMenuItemViewPool(
    context: Context, capacity: Int
) : ViewFactoryConcurrentObjectPool<View>(
    SwipeMenuLayoutItemViewFactory.createDefaultItemFactory(context), capacity
)

/**
 * Пул для создания view пунктов свайп-меню с текстом.
 *
 * @param capacity изначальное макс. количество view в пуле
 *
 * @author us.bessonov
 */
internal class TextMenuItemViewPool(
    context: Context, capacity: Int
) : ViewFactoryConcurrentObjectPool<View>(
    SwipeMenuLayoutItemViewFactory.createTextItemFactory(context), capacity
)
