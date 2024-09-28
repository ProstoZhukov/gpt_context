package ru.tensor.sbis.swipeablelayout.viewpool

import android.view.View
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import kotlin.reflect.KClass

internal interface SwipeMenuItemViewFactory {

    fun createView(clazz: KClass<out SwipeMenuItem>?): View
}

