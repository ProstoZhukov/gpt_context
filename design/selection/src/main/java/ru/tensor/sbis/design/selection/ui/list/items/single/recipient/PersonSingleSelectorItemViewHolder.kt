package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import javax.inject.Provider

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о персоне в компоненте выбора.
 *
 * @author vv.chekurda
 */
internal open class PersonSingleSelectorItemViewHolder(
    protected val personItemView: PersonSelectorItemView,
    iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    activityProvider: Provider<FragmentActivity>?,
    private val photo: PersonView = personItemView.findViewById(R.id.selection_person_photo)
) : RecyclerView.ViewHolder(personItemView) {

    /**
     * Данные, которые отображаются во вью-холдере.
     */
    protected lateinit var data: PersonSelectorItemModel
        private set

    init {
        if (iconClickListener != null && activityProvider?.get() != null) {
            photo.setOnClickListener {
                iconClickListener.onClicked(activityProvider.get(), data)
            }
        }
    }

    /**
     * Установка данных из [PersonSelectorItemModel].
     */
    @CallSuper
    open fun bind(data: PersonSelectorItemModel) {
        this.data = data
        personItemView.setData(data)
        photo.setHasActivityStatus(true)
    }
}