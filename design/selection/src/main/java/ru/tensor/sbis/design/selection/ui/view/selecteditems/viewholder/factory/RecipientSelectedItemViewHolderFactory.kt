package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory

import android.view.ViewGroup
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonCollageLineViewPool
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.*
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.*
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder

/**
 * Реализация [SelectedItemViewHolderFactory], поддерживающая создание вьюхолдеров для адресатов в панели выбранных
 * элементов (сотрудников, подразделений, рабочих групп)
 *
 * @param container требуется для генерации [ViewGroup.LayoutParams] из xml атрибутов
 *
 * @author us.bessonov
 */
internal class RecipientSelectedItemViewHolderFactory(
    private val container: ViewGroup
) : SelectedItemViewHolderFactory {

    private val personViewManager = PersonCollageLineViewPool(container.context)

    override fun createViewHolder(
        itemType: Class<out SelectedItem>,
        @Px
        maxWidth: Int
    ): SelectedItemViewHolder<*> {
        return when (itemType) {
            SelectedItemWithImage::class.java -> SelectedItemWithImageViewHolder(container, maxWidth)
            SelectedFolderItem::class.java -> SelectedFolderItemViewHolder(container, maxWidth)
            SelectedPersonItem::class.java -> SelectedPersonItemViewHolder(container, maxWidth)
            SelectedCompanyItem::class.java -> SelectedCompanyItemViewHolder(container, maxWidth)
            else -> error(
                "Cannot create viewHolder for ${SelectedItem::javaClass} " +
                    "(you cannot use this type for recipient selection)"
            )
        }
    }

}
