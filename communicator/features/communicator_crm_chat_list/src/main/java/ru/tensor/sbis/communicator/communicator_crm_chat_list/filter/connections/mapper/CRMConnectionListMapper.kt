package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper

import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.consultations.generated.ChannelListViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import java.util.UUID
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.applySearchSpan
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.icon
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Реализация ItemMapper для crud3.
 *
 * @author da.zhukov.
 */
internal class CRMConnectionListMapper(
    private val context: SbisThemedContext,
    private val highlightsColorProvider: HighlightsColorProvider,
    private val onSelectedAction: (Pair<UUID, String>) -> Unit = { _: Pair<UUID, String> -> },
    private val selectedItems: MutableLiveData<List<UUID>>
) : ItemInSectionMapper<ChannelListViewModel, AnyItem>  {

    override fun map(
        item: ChannelListViewModel,
        defaultClickAction: (ChannelListViewModel) -> Unit
    ): AnyItem {
        val (icon, iconColor) = item.icon.icon(context)
        val label = item.searchResult?.let {
            item.label.applySearchSpan(it.start, it.end, highlightsColorProvider)
        } ?: item.label

        return BindingItem(
            data = CRMConnectionModelBinding(
                id = item.id,
                icon = icon,
                iconColor = iconColor,
                label = label,
                groupType = item.groupType,
                onSelectedAction = onSelectedAction,
                selectedItems = selectedItems
            ),
            dataBindingViewHolderHelper = DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_crm_connection_item)
            ),
            options = Options(
                clickAction = {
                    defaultClickAction(item)
                },
                customBackground = true,
                customSidePadding = true
            )
        )
    }
}