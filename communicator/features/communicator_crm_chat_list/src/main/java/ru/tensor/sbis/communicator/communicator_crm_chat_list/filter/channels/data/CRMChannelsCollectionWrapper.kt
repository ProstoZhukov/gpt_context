package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data

import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionFilter
import ru.tensor.sbis.consultations.generated.ChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.CollectionOfChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfChannelFolderAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author da.zhukov
 */
internal interface CRMChannelsCollectionWrapper :
        Wrapper<CollectionOfChannelHierarchyViewModel,
                CRMChannelsCollectionObserver,
                ChannelHierarchyCollectionFilter,
                PaginationOfChannelFolderAnchor,
                ItemWithIndexOfChannelHierarchyViewModel,
                ChannelHierarchyViewModel>