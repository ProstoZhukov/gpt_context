package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.view_factory.XmlViewFactory
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.api.menu.TextItem
import kotlin.reflect.KClass

/**
 * Выполняет создание view пунктов свайп-меню с указанным макетом.
 *
 * @param layout ресурс разметки пункта меню
 */
internal class SwipeMenuLayoutItemViewFactory private constructor(
    @LayoutRes private val layout: Int, context: Context
) : XmlViewFactory<View>(context), SwipeMenuItemViewFactory {

    override fun getLayoutRes() = layout

    override fun createView(clazz: KClass<out SwipeMenuItem>?) = createView()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SwipeMenuLayoutItemViewFactory

        return layoutRes == other.layoutRes
    }

    override fun hashCode(): Int = layoutRes

    companion object {

        /** @SelfDocumented */
        fun createDefaultItemFactory(context: Context) =
            SwipeMenuLayoutItemViewFactory(SwipeMenuItem.DEFAULT_ITEM_LAYOUT_RES, context)

        /** @SelfDocumented */
        fun createTextItemFactory(context: Context) = SwipeMenuLayoutItemViewFactory(TextItem.ITEM_LAYOUT_RES, context)

        /** @SelfDocumented */
        @Deprecated("Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3")
        fun createCustomItemFactory(@LayoutRes layout: Int, context: Context) =
            SwipeMenuLayoutItemViewFactory(layout, context)
    }
}