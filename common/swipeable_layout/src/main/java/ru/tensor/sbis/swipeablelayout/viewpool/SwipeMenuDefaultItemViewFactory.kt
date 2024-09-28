package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.api.menu.TextItem
import kotlin.reflect.KClass

/**
 * Фабрика пунктов меню, поддерживающая создание элементов различных типов.
 *
 * @author us.bessonov
 */
internal class SwipeMenuDefaultItemViewFactory(context: Context) : SwipeMenuItemViewFactory {

    private val commonItemFactory = SwipeMenuLayoutItemViewFactory.createDefaultItemFactory(context)
    private val textItemFactory = SwipeMenuLayoutItemViewFactory.createTextItemFactory(context)

    override fun createView(clazz: KClass<out SwipeMenuItem>?): View {
        return when(clazz) {
            TextItem::class -> textItemFactory.createView()
            else -> commonItemFactory.createView()
        }
    }
}