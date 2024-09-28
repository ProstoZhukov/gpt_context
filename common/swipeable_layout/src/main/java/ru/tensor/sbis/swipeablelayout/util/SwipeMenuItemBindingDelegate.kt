package ru.tensor.sbis.swipeablelayout.util

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.swipeablelayout.MenuItem

/**
 * Предназначен для привязки данных ко [View] пунктов свайп-меню
 *
 * @author us.bessonov
 */
@Deprecated(
    "Используйте актуальное API. " + "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3"
)
interface SwipeMenuItemBindingDelegate<in ITEM : MenuItem> {

    /** @SelfDocumented */
    fun bind(itemView: View, item: ITEM)
}

/**
 * Стандартная реализация [SwipeMenuItemBindingDelegate], использующая DataBinding
 *
 * @author us.bessonov
 */
@Deprecated(
    "Используйте актуальное API. " + "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3"
)
class SwipeMenuItemDataBindingDelegate<ITEM : MenuItem>(private val bindingId: Int) :
    SwipeMenuItemBindingDelegate<ITEM> {

    override fun bind(itemView: View, item: ITEM) {
        val binding: ViewDataBinding? = DataBindingUtil.getBinding(itemView) ?: DataBindingUtil.bind(itemView)
        binding?.setVariable(bindingId, item)
        binding?.executePendingBindings()
    }
}