package ru.tensor.sbis.communicator.crm.conversation.presentation.ui

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.StubType
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Фабрика заглушек переписки CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationStubViewContentFactory(
    private val crmConsultationCase: CRMConsultationCase
) : StubFactory {

    override fun create(type: StubType): StubViewContent {
        return when (type) {
            StubType.NO_DATA -> prepareNoDataStubContent()
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private fun prepareNoDataStubContent(): StubViewContent =
        when (crmConsultationCase) {
            is CRMConsultationCase.Client -> {
                ResourceImageStubContent(
                    icon = ResourcesCompat.ID_NULL,
                    messageRes = R.string.communicator_no_messages_to_display,
                    detailsRes = ResourcesCompat.ID_NULL
                )
            }
            is CRMConsultationCase.SalePoint -> {
                if (crmConsultationCase.isBrand) {
                    ResourceImageStubContent(
                        icon = ru.tensor.sbis.communicator.crm.conversation.R.drawable.communicator_crm_theme_stub_empty,
                        messageRes = ru.tensor.sbis.communicator.crm.conversation.R.string.communicator_crm_brand_stub_message,
                        detailsRes = ResourcesCompat.ID_NULL
                    )
                } else {
                    ResourceImageStubContent(
                        icon = ru.tensor.sbis.communicator.crm.conversation.R.drawable.communicator_crm_theme_stub_empty,
                        messageRes = ru.tensor.sbis.communicator.crm.conversation.R.string.communicator_crm_sabyget_stub_message,
                        detailsRes = ru.tensor.sbis.communicator.crm.conversation.R.string.communicator_crm_conversation_stub_subtitle
                    )
                }
            }
            else -> {
                ResourceImageStubContent(
                    icon = ResourcesCompat.ID_NULL,
                    messageRes = ru.tensor.sbis.communicator.crm.conversation.R.string.communicator_crm_chat_cant_view_permisshion,
                    detailsRes = ResourcesCompat.ID_NULL
                )
            }
        }
}