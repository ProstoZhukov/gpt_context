package ru.tensor.sbis.design.selection.ui.contract.listeners

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import java.io.Serializable

/**
 * Пользовательский обработчик нажатий на области ячейки
 *
 * @author ma.kolpakov
 */
interface ItemClickListener<DATA : SelectorItemModel, ACTIVITY : FragmentActivity> : Serializable {

    /**
     * Короткое или долгое нажатие в зависимости от места применения
     *
     * @see SelectorItemListeners
     */
    fun onClicked(activity: ACTIVITY, item: DATA)
}