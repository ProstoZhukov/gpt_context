package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data.CRMConnectionListCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper.CRMConnectionListMapper
import ru.tensor.sbis.consultations.generated.ChannelListViewModel
import ru.tensor.sbis.consultations.generated.ConnectionFilter
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.DefaultStubViewContentFactory
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика списоного компонента.
 *
 * @author da.zhukov
 */
internal class CRMConnectionListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: CRMConnectionListCollectionWrapper,
    private val mapper: CRMConnectionListMapper
) {

    private var listComponent: ListComponent<ConnectionFilter, ChannelListViewModel, AnyItem>? =
        null

    /** @SelfDocumented */
    fun create(view: ListComponentView) = view.inject(
        viewModelStoreOwner,
        lazy { wrapper },
        lazy { mapper },
        lazy { DefaultStubViewContentFactory() },
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}