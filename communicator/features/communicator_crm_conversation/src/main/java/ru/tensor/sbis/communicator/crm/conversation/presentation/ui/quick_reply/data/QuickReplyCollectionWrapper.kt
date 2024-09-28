package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import ru.tensor.sbis.consultations.generated.CollectionOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfQuickReplyAnchor
import ru.tensor.sbis.consultations.generated.QuickReplyFilter
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author dv.baranov
 */
internal interface QuickReplyCollectionWrapper :
    Wrapper<CollectionOfQuickReplyViewModel,
        QuickReplyCollectionObserver,
        QuickReplyFilter,
        PaginationOfQuickReplyAnchor,
        ItemWithIndexOfQuickReplyViewModel,
        QuickReplyViewModel,>
