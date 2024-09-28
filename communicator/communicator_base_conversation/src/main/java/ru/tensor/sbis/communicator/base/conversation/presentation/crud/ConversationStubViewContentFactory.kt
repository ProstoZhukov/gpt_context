package ru.tensor.sbis.communicator.base.conversation.presentation.crud

import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Используется моделью представления списочного компонента для предоставления заглушки, соответствующей полученному от
 * микросервиса типу заглушки для показа.
 *
 * @author da.zhukov
 */
class ConversationStubViewContentFactory(
    isChannel: Boolean
) : StubFactory {

    private val noDataMessageRes by lazy {
        if (isChannel) {
            RCommunicatorDesign.string.communicator_no_messages_is_channel
        } else {
            RCommunicatorDesign.string.communicator_stub_view_dialog_conversation_no_messages
        }
    }

    override fun create(type: StubType): StubViewContent = when (type) {
        StubType.NO_DATA -> ImageStubContent(
            imageType = StubViewImageType.NO_MESSAGES,
            messageRes = noDataMessageRes,
            details = null
        )
        StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
        StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
        StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
    }
}

