package ru.tensor.sbis.design.person_suggest.suggest.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.design.person_suggest.R
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSelectionListener
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize

/**
 * Реализация [RecyclerView.ViewHolder] для отображения персоны в списке панели выбора персоны.
 *
 * @property personView вью фото персоны.
 * @property selectionListener слушатель выбранной персоны.
 *
 * @author vv.chekurda
 */
internal class PersonSuggestViewHolder(
    private val personView: PersonView,
    private val selectionListener: PersonSelectionListener
) : AbstractViewHolder<PersonSuggestData>(personView) {

    private lateinit var data: PersonSuggestData

    init {
        personView.setOnClickListener { selectionListener(data) }
    }

    override fun bind(data: PersonSuggestData) {
        super.bind(data)
        this.data = data
        personView.setSize(PhotoSize.M)
        personView.setFullNameForNodeInfo(data.name.fullName)
        personView.setData(data.personData)
        personView.setHasActivityStatus(true)
        personView.id = R.id.design_person_suggest_input_person_item_id
    }
}