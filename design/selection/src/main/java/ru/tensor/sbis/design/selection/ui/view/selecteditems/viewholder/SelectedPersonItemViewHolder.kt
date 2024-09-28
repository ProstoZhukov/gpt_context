package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.view.isVisible
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedPersonItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView

/**
 * Реализация [SelectedItemViewHolder] для выбранного сотрудника с фото, фамилией и именем
 *
 * @author us.bessonov
 */
internal class SelectedPersonItemViewHolder(
    parentView: ViewGroup,
    @Px
    maxItemWidth: Int
) : SelectedItemViewHolder<SelectedPersonItem>(
    maxItemWidth,
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.selection_selected_person_item, parentView, false) as SelectedItemView
) {

    private val photo = itemView.findViewById<PersonView>(R.id.photo)
    private val lastName = itemView.findViewById<TextView>(R.id.last_name)
    private val firstName = itemView.findViewById<TextView>(R.id.first_name)
    private val closeIcon = itemView.findViewById<View>(R.id.close_icon)

    override fun bind() {
        photo.setData(item.photoData)

        val hasLastName = item.lastName.isNotBlank()

        lastName.apply {
            text = item.lastName.takeIf { hasLastName }
                ?: item.firstName
        }

        firstName.apply {
            text = item.firstName
            isVisible = item.firstName.isNotBlank() && hasLastName
        }

        closeIcon.setOnClickListener { item.onClick() }
    }
}