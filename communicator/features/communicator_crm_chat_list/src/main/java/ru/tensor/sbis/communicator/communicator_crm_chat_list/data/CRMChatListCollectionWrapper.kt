package ru.tensor.sbis.communicator.communicator_crm_chat_list.data

import ru.tensor.sbis.consultations.generated.CollectionOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.PaginationOfConsultationListAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author da.zhukov
 */
internal interface CRMChatListCollectionWrapper :
    Wrapper<CollectionOfConsultationListElementModel,
            CRMChatListCollectionObserver,
            ConsultationListFilter,
            PaginationOfConsultationListAnchor,
            ItemWithIndexOfConsultationListElementModel,
            ConsultationListElementModel>