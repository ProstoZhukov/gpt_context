package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedCompanyItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView

/**
 * Реализация [SelectedItemViewHolder] для выбранного элемента с изображением компании и текстом
 *
 * @author us.bessonov
 */
internal class SelectedCompanyItemViewHolder(
    parentView: ViewGroup,
    @Px
    maxItemWidth: Int,
) : SelectedItemViewHolder<SelectedCompanyItem>(
    maxItemWidth,
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.selection_selected_company_item, parentView, false) as SelectedItemView
) {

    private val photoWidth = parentView.resources.getDimensionPixelSize(PhotoSize.XS.photoSize)

    private val title = itemView.findViewById<TextView>(R.id.title)
    private val photo = itemView.findViewById<PersonView>(R.id.photo)
    private val closeIcon = itemView.findViewById<View>(R.id.close_icon)

    override fun bind() {
        photo.setData(item.companyData)

        title.apply {
            text = item.title
        }
        closeIcon.setOnClickListener { item.onClick() }
    }
}