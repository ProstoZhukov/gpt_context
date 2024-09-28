package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import ru.tensor.sbis.communicator.generated.CollectionOfLinkViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfLinkViewModel
import ru.tensor.sbis.communicator.generated.LinkFilter
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfLinkAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author dv.baranov
 */
internal interface ConversationLinksListCollectionWrapper :
    Wrapper<CollectionOfLinkViewModel,
        ConversationLinksListCollectionObserver,
        LinkFilter,
        PaginationOfLinkAnchor,
        ItemWithIndexOfLinkViewModel,
        LinkViewModel,>
