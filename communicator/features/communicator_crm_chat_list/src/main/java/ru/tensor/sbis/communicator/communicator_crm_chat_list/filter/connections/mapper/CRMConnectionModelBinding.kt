package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper

import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * Модель данных, представление ChannelListViewModel.
 * Необходима для упрощения доступа к полям исходной ChannelListViewModel, а также для возможности расширения.
 *
 * @author da.zhukov
 */
data class CRMConnectionModelBinding(
    val id: UUID,
    val icon: String,
    val iconColor: Int,
    val label: CharSequence,
    val groupType: Short,
    val onSelectedAction: (Pair<UUID, String>) -> Unit = { _: Pair<UUID, String> -> },
    val selectedItems: MutableLiveData<List<UUID>>
) : ComparableItem<CRMConnectionModelBinding> {

    val isChecked: MutableLiveData<Boolean>
        get() = MutableLiveData(selectedItems.value?.contains(id))

    override fun areTheSame(otherItem: CRMConnectionModelBinding): Boolean = id == otherItem.id

    fun onCheckboxClick() {
        onSelectedAction(this.id to this.label.toString())
        if (selectedItems.value?.contains(id) == true) {
            selectedItems.postValue(selectedItems.value?.toMutableList()?.apply { remove(id) })
        } else {
            selectedItems.postValue(selectedItems.value?.toMutableList()?.apply { add(id) })
        }
    }
}