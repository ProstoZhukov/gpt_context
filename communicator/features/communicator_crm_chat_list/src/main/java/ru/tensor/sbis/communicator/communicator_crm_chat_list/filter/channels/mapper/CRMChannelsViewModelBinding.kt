package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.mapper

import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.requireCastTo
import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * Модель данных, представление ChannelHierarchyViewModel.
 * Необходима для упрощения доступа к полям исходной ChannelHierarchyViewModel, а также для возможности расширения.
 *
 * @author da.zhukov
 */
data class CRMChannelsViewModelBinding(
    val id: UUID,
    val name: CharSequence,
    val parentId: UUID?,
    val rootName: String?,
    val itemType: ChannelHeirarchyItemType,
    val isGroup: Boolean,
    val arrowIconIsVisible: Boolean,
    val path: ArrayList<String>,
    val icon: String,
    val iconColor: Int,
    val groupType: ChannelGroupType,
    val onSuccessAction: (UUID, UUID?, String) -> Unit = { _: UUID, _: UUID?, _: String -> },
    val onCheckedAction: (UUID, String) -> Unit = { _: UUID, _: String -> },
    val case: CrmChannelListCase,
    val selectedItems: MutableLiveData<List<UUID>>,
    val isParentVisible: Boolean,
) : ComparableItem<CRMChannelsViewModelBinding> {

    override fun areTheSame(otherItem: CRMChannelsViewModelBinding): Boolean = id == otherItem.id

    override fun hasTheSameContent(otherItem: CRMChannelsViewModelBinding): Boolean = this == otherItem

    /** @SelfDocumented */
    fun onSuccessIconClick() {
        onSuccessAction.invoke(this.id, this.parentId, this.name.toString())
    }

    /** @SelfDocumented */
    fun onCheckedClick() {
        onCheckedAction(this.id, this.name.toString())
        if (selectedItems.value?.contains(id) == true) {
            selectedItems.postValue(selectedItems.value?.toMutableList()?.apply { remove(id) })
        } else {
            selectedItems.postValue(selectedItems.value?.toMutableList()?.apply { add(id) })
        }
    }

    /** @SelfDocumented */
    fun onClick() {
        if (isFilterRegistryCase() && !this.isGroup) {
            onCheckedClick()
        }
    }

    /** @SelfDocumented */
    fun isFilterRegistryCase(): Boolean =
        this.case is CrmChannelListCase.CrmChannelFilterCase && this.case.type == CrmChannelFilterType.REGISTRY

    /** @SelfDocumented */
    fun isFilterOperatorCase(): Boolean =
        this.case is CrmChannelListCase.CrmChannelFilterCase && this.case.type == CrmChannelFilterType.OPERATOR

    /** @SelfDocumented */
    fun isConsultationCase(): Boolean = this.case is CrmChannelListCase.CrmChannelConsultationCase

    /** @SelfDocumented */
    fun isReassignCase(): Boolean = this.case is CrmChannelListCase.CrmChannelReassignCase
}