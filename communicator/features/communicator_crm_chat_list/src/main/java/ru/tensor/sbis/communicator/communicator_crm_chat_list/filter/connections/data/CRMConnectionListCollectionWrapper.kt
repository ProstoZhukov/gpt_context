package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data

import ru.tensor.sbis.consultations.generated.ChannelListViewModel
import ru.tensor.sbis.consultations.generated.CollectionOfChannelListViewModel
import ru.tensor.sbis.consultations.generated.ConnectionFilter
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfChannelListViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfConnectionAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author da.zhukov
 */
internal interface CRMConnectionListCollectionWrapper :
        Wrapper<CollectionOfChannelListViewModel,
                CRMConnectionListCollectionObserver,
                ConnectionFilter,
                PaginationOfConnectionAnchor,
                ItemWithIndexOfChannelListViewModel,
                ChannelListViewModel>