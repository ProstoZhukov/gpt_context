package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory

import android.view.View
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder

/**
 * Фабрика, определяющая создание вьюхолдеров (а соответственно и [View]) для выбранных элементов конкретных
 * [SelectedItem]
 *
 * @author us.bessonov
 */
internal interface SelectedItemViewHolderFactory {

    /**
     * Создаёт вьюхолдеры для поддерживаемых типов [SelectedItem]
     */
    fun createViewHolder(
        itemType: Class<out SelectedItem>,
        @Px
        maxWidth: Int
    ): SelectedItemViewHolder<*>

}