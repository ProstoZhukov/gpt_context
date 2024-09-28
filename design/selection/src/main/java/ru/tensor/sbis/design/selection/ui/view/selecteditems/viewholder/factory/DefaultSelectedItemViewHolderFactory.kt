package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory

import android.view.ViewGroup
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedTextItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.SelectedItemTextViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder

/**
 * Реализация [SelectedItemViewHolderFactory] по умолчанию, поддерживающая создание вьюхолдера только для
 * [SelectedTextItem]
 *
 * @param container требуется для генерации [ViewGroup.LayoutParams] из xml атрибутов
 *
 * @author us.bessonov
 */
internal class DefaultSelectedItemViewHolderFactory(private val container: ViewGroup) :
    SelectedItemViewHolderFactory {

    override fun createViewHolder(
        itemType: Class<out SelectedItem>,
        @Px
        maxWidth: Int
    ): SelectedItemViewHolder<*> {
        require(itemType == SelectedTextItem::class.java) {
            "Default factory can create viewHolder only for ${SelectedTextItem::class}"
        }
        return SelectedItemTextViewHolder(container, maxWidth)
    }
}