package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о контрагенте в компоненте выбора.
 *
 * @author us.bessonov
 */
internal open class ContractorSingleSelectorItemViewHolder(
    private val personItemView: PersonSelectorItemView
) : RecyclerView.ViewHolder(personItemView) {

    /**
     * Данные, которые отображаются во вью-холдере.
     */
    protected lateinit var data: ContractorSelectorItemModel
        private set

    /**
     * Установка данных из [ContractorSelectorItemModel].
     */
    @CallSuper
    open fun bind(data: ContractorSelectorItemModel) {
        this.data = data
        personItemView.setData(data)
    }
}