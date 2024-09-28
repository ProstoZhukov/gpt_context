package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import ru.tensor.sbis.design.view_factory.AbstractViewFactory
import ru.tensor.sbis.objectpool.impl.ViewFactoryConcurrentObjectPool

/**
 * Пул, использующий [SwipeMenuDividerViewFactory] для создания view разделителей пунктов свайп-меню
 *
 * @param dividerColor ресурс цвета разделителя
 * @param capacity изначальное макс. количество view в пуле
 *
 * @author us.bessonov
 */
internal class SwipeMenuDividerViewPool(
    @ColorRes private val dividerColor: Int, context: Context, capacity: Int
) : ViewFactoryConcurrentObjectPool<View>(SwipeMenuDividerViewFactory(dividerColor, context), capacity)

/**
 * Выполняет создание view разделителей пунктов свайп-меню
 *
 * @param dividerColor ресурс цвета разделителя
 */
internal class SwipeMenuDividerViewFactory(
    @ColorRes var dividerColor: Int, context: Context
) : AbstractViewFactory<View>(context) {

    override fun createView() = View(context).apply {
        setBackgroundResource(dividerColor)
    }
}
