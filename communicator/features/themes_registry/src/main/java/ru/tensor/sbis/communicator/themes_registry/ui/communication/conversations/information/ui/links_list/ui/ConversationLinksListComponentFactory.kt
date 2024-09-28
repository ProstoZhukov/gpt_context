package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.generated.LinkFilter
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListCollectionWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.mappers.ConversationLinksListMapper
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Фабрика для создания компонента списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: ConversationLinksListCollectionWrapper,
    private val mapper: ConversationLinksListMapper,
) {

    private val stubFactory = StubFactory { type ->
        when (type) {
            StubType.NO_DATA -> {
                StubViewCase.NO_DATA.getContent(
                    image = StubViewImageType.EMPTY_STUB_IMAGE,
                    message = ResourcesCompat.ID_NULL,
                    details = RCommunicatorDesign.string.communicator_conversation_links_list_empty_stub_subtitle
                )
            }
            StubType.BAD_FILTER -> {
                // TODO с контроллера должно приходить NO_DATA
                // StubViewCase.NO_FILTER_RESULTS.getContent()
                StubViewCase.NO_DATA.getContent(
                    image = StubViewImageType.EMPTY_STUB_IMAGE,
                    message = ResourcesCompat.ID_NULL,
                    details = RCommunicatorDesign.string.communicator_conversation_links_list_empty_stub_subtitle
                )
            }
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<LinkFilter, LinkViewModel, AnyItem>? =
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
