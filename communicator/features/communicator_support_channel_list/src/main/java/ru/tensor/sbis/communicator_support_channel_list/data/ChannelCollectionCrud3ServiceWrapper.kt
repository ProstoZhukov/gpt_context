package ru.tensor.sbis.communicator_support_channel_list.data

import ru.tensor.sbis.consultations.generated.CollectionOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfSupportChatsListAnchor
import ru.tensor.sbis.consultations.generated.SupportChatsListFilter
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper
 * @see Wrapper
 */
internal interface ChannelCollectionCrud3ServiceWrapper :
    Wrapper<CollectionOfSupportChatsViewModel,
            SupportChannelsListCollectionObserver,
            SupportChatsListFilter,
            PaginationOfSupportChatsListAnchor,
            ItemWithIndexOfSupportChatsViewModel,
            SupportChatsViewModel>