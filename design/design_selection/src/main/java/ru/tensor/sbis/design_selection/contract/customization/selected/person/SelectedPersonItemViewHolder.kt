package ru.tensor.sbis.design_selection.contract.customization.selected.person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.design_selection.databinding.DesignSelectionSelectedPersonItemBinding

/**
 * Вью-холдер выбранной персоны в компоненте выбора.
 *
 * @property clickDelegate делегат обработки кликов по ячейке.
 *
 * @author vv.chekurda
 */
class SelectedPersonItemViewHolder(
    parentView: ViewGroup,
    private val clickDelegate: SelectedItemClickDelegate,
    personClickListener: PersonClickListener?
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.design_selection_selected_person_item, parentView, false)
) {
    private val binding = DesignSelectionSelectedPersonItemBinding.bind(itemView)
    private lateinit var data: SelectionPersonItem

    init {
        binding.selectedPersonCloseIcon.setOnClickListener {
            clickDelegate.onUnselectClicked(data)
        }
        binding.selectedPersonPhoto.setOnClickListener { view ->
            val listener = personClickListener ?: return@setOnClickListener
            data.photoData.uuid?.also { listener.onPersonClicked(view.context, it) }
        }
    }

    /**
     * Привязать данные [data] к ячейке.
     */
    fun bind(data: SelectionPersonItem) {
        this.data = data
        with(binding) {
            selectedPersonPhoto.setData(data.photoData)

            val personName = data.personName
            val hasLastName = personName.lastName.isNotBlank()
            val (lastNameHighlights, firstNameHighlights) =
                data.titleHighlights.parseHighlights(hasLastName, personName.lastName.length)

            selectedPersonLastName.apply {
                if (hasLastName) {
                    setTextWithHighlight(personName.lastName, lastNameHighlights)
                } else {
                    setTextWithHighlight(personName.firstName, firstNameHighlights)
                }
            }

            selectedPersonFirstName.apply {
                setTextWithHighlight(personName.firstName, firstNameHighlights)
                isVisible = personName.firstName.isNotBlank() && hasLastName
            }
        }
    }

    private fun List<SearchSpan>.parseHighlights(
        hasLastName: Boolean,
        lastNameLength: Int
    ): Pair<List<SearchSpan>, List<SearchSpan>> {
        val lastNameHighlights = mutableListOf<SearchSpan>()
        val firstNameHighlights = mutableListOf<SearchSpan>()
        forEach { searchSpan ->
            if (hasLastName && searchSpan.start < lastNameLength) {
                lastNameHighlights.add(searchSpan)
            } else {
                val firstNameSpaceIndex = if (hasLastName) 1 else 0
                val firstNameSearchSpan = searchSpan.copy(
                    start = (searchSpan.start - lastNameLength - firstNameSpaceIndex).coerceAtLeast(0),
                    end = (searchSpan.end - lastNameLength - firstNameSpaceIndex).coerceAtLeast(0)
                )
                firstNameHighlights.add(firstNameSearchSpan)
            }
        }
        return lastNameHighlights to firstNameHighlights
    }
}