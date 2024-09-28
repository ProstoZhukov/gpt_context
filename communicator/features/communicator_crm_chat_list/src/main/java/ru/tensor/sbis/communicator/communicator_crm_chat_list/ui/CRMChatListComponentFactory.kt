package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communication_decl.crm.CRMChatListDefaultParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListParams
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListFolderViewSectionFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.mapper.CRMChatListMapper
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.design.stubview.R as RStubView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.design.stubview.ImageStubContent

/** @SelfDocumented */
internal class CRMChatListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: CRMChatListCollectionWrapper,
    private val mapper: CRMChatListMapper,
    private val folderViewSectionFactory: CommunicatorBaseListFolderViewSectionFactory,
    crmChatListParams: CRMChatListParams
) {

    private val isDefMode = crmChatListParams is CRMChatListDefaultParams

    private val stubFactory = StubFactory { type ->
        val badFilterContent = if (isDefMode) {
            ImageStubContent(
                imageType = StubViewCase.NO_FILTER_RESULTS.imageType,
                messageRes = RStubView.string.design_stub_view_no_filter_results_message,
                detailsRes = R.string.communicator_crm_chat_list_bad_filter_list_stub_details
            )
        } else {
            ImageStubContent(
                imageType = StubViewCase.NO_FILTER_RESULTS.imageType,
                messageRes = R.string.communicator_crm_chat_list_empty_list_stub_message,
                details = null
            )
        }

        when (type) {
            StubType.NO_DATA -> ImageStubContent(
                imageType = StubViewCase.TECHNICAL_SUPPORT_CHATS.imageType,
                messageRes = R.string.communicator_crm_chat_list_empty_list_stub_message,
                detailsRes = R.string.communicator_crm_chat_list_empty_list_stub_details
            )
            StubType.BAD_FILTER -> badFilterContent
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<ConsultationListFilter, ConsultationListElementModel, AnyItem>? =
        null

    /** @SelfDocumented */
    fun create(view: ListComponentView) = view.inject(
        viewModelStoreOwner,
        lazy { wrapper },
        lazy { mapper },
        lazy { stubFactory },
        firstItemFactory = if (isDefMode) lazy { folderViewSectionFactory } else null
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}