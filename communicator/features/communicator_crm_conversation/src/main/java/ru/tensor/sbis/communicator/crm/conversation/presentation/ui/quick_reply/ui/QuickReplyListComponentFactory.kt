package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyCollectionWrapper
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.mappers.QuickReplyMapper
import ru.tensor.sbis.consultations.generated.QuickReplyFilter
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика для создания компонена списка быстрых ответов.
 *
 * @author dv.baranov
 */
internal class QuickReplyListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: QuickReplyCollectionWrapper,
    private val mapper: QuickReplyMapper,
) {

    private val stubFactory = StubFactory { type ->
        when (type) {
            StubType.NO_DATA -> ImageStubContent(
                imageType = StubViewImageType.EMPTY_LIST,
                R.string.communicator_crm_quick_reply_empty_stub,
                null,
            )
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<QuickReplyFilter, QuickReplyViewModel, AnyItem>? =
        null

    /** @SelfDocumented */
    fun create(view: ListComponentView) = view.inject(
        viewModelStoreOwner,
        lazy { wrapper },
        lazy { mapper },
        lazy { stubFactory },
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}
