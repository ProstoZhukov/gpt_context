package ru.tensor.sbis.communicator_support_channel_list.mapper

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListHostViewModel
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options


/**
 * Интерфейс для обработки клика по ячейке
 */
internal interface ChannelClickCommand {
    fun onClick(model: SupportChatsViewModel)
}

/**
 * Реализация ChannelClickCommand
 */
internal class ChannelClickCommandInMaster(private val supportChannelListHostViewModel: SupportChannelListHostViewModel) : ChannelClickCommand{
    override fun onClick(model: SupportChatsViewModel) {
        supportChannelListHostViewModel.viewModelScope.launch {
            supportChannelListHostViewModel.onChannelSelectedInMaster.emit(model)
        }
    }
}

/**
 * Фабрика для SupportChatsMapper
 */
@AssistedFactory
internal interface SupportChannelsMapperFactory {
    fun create(
        listDateFormatter: ListDateFormatter,
        clickAction: ChannelClickCommand
    ): SupportChannelsMapper
}

/**
 * Реализация ItemMapper дял crud3
 */
internal class SupportChannelsMapper @AssistedInject constructor(
    @Assisted private val listDateFormatter: ListDateFormatter,
    @Assisted val clickAction: ChannelClickCommand
) : ItemInSectionMapper<SupportChatsViewModel, AnyItem> {

    override fun map(item: SupportChatsViewModel, defaultClickAction: (SupportChatsViewModel) -> Unit): AnyItem {
        return BindingItem(
            SupportChatsViewDataBindingModel.create(listDateFormatter, item), DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_support_channel_list_item)
            ),
            options = Options(
                clickAction = {
                    clickAction.onClick(item)
                },
                customSidePadding = true,
                customBackground = true
            )
        )
    }
}