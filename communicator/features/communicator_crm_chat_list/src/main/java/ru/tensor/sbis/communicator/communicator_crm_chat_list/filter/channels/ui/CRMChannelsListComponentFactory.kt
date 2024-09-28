package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data.CRMChannelsCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.mapper.CRMChannelsMapper
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionFilter
import ru.tensor.sbis.consultations.generated.ChannelHierarchyViewModel
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика списоного компонента.
 *
 * @author da.zhukov
 */
internal class CRMChannelsListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: CRMChannelsCollectionWrapper,
    private val mapper: CRMChannelsMapper,
    case: CrmChannelListCase,
    private val listViewSectionFactory: CrmChannelsListViewSectionFactory
) {

    private val isCrmChannelsForOperator =
        case is CrmChannelListCase.CrmChannelFilterCase && case.type == CrmChannelFilterType.OPERATOR

    private val stubFactory = StubFactory { type ->
        when (type) {
            StubType.NO_DATA -> StubViewCase.NO_DATA.getContent()
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<ChannelHierarchyCollectionFilter, ChannelHierarchyViewModel, AnyItem>? =
        null

    /** @SelfDocumented */
    fun create(view: ListComponentView) = view.inject(
        viewModelStoreOwner = viewModelStoreOwner,
        wrapper = lazy { wrapper },
        mapper = lazy { mapper },
        stubFactory = lazy { stubFactory },
        firstItemFactory = if (isCrmChannelsForOperator) lazy { listViewSectionFactory } else null
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}