package ru.tensor.sbis.design.view_factory

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Простейшая реализация [XmlViewFactory] для создания [View] с заданным [layoutId]
 *
 * @author us.bessonov
 */
class SimpleXmlViewFactory<V : View>(
    @LayoutRes
    private val layoutId: Int,
    context: Context
) : XmlViewFactory<V>(context) {

    override fun getLayoutRes(): Int = layoutId
}