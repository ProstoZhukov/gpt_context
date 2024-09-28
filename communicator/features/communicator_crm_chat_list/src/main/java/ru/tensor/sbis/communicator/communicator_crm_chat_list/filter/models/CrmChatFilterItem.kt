package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models

import androidx.annotation.StringRes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMFilterType
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Элемент фильтра.
 *
 * @author da.zhukov
 */
internal sealed interface CrmChatFilterItem : ComparableItem<CrmChatFilterItem> {
    val type: CRMFilterType
    @get:StringRes
    val titleRes: Int
    val isSelected: Boolean
    val clickAction: ((CRMFilterType) -> Unit)
    var value: String

    override fun areTheSame(otherItem: CrmChatFilterItem): Boolean = titleRes == otherItem.titleRes
}

/**
 * Ячейка в фильтре с маркером.
 *
 * @author da.zhukov
 */
internal data class SelectableFilterItem(
    override val type: CRMRadioButtonFilterType,
    @StringRes override val titleRes: Int,
    override val isSelected: Boolean,
    override val clickAction: ((CRMFilterType) -> Unit)
) : CrmChatFilterItem {
    override var value: String = StringUtils.EMPTY

    /** SelfDocumented */
    fun onClick() {
        DebounceActionHandler.INSTANCE.handle {
            clickAction(type)
        }
    }
}

/**
 * Ячейка в фильтре со стрелкой.
 *
 * @author da.zhukov
 */
internal data class OpenableFilterItem(
    override val type: CRMOpenableFilterType,
    @StringRes override val titleRes: Int,
    override val clickAction: ((CRMFilterType) -> Unit),
    override var value: String = StringUtils.EMPTY
) : CrmChatFilterItem {
    override val isSelected: Boolean = false

    /** SelfDocumented */
    fun onClick() {
        DebounceActionHandler.INSTANCE.handle {
            clickAction(type)
        }
    }
}

/**
 * Ячейка в фильтре с галочкой выбора.
 *
 * @author da.zhukov
 */
internal data class CheckableFilterItem(
    override val type: CRMCheckableFilterType,
    @StringRes override val titleRes: Int,
    override val isSelected: Boolean,
    override val clickAction: ((CRMFilterType) -> Unit)
) : CrmChatFilterItem {
    override var value: String = StringUtils.EMPTY

    /** SelfDocumented */
    fun onClick() {
        DebounceActionHandler.INSTANCE.handle {
            clickAction(type)
        }
    }
}