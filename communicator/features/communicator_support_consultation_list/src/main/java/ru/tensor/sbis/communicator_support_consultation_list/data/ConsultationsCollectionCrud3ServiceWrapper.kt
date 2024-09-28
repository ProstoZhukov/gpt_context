package ru.tensor.sbis.communicator_support_consultation_list.data

import ru.tensor.sbis.consultations.generated.CollectionOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.PaginationOfConsultationListAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper
 * @see Wrapper
 */
internal interface ConsultationsCollectionCrud3ServiceWrapper :
    Wrapper<CollectionOfConsultationListElementModel,
            SupportConsultationsListCollectionObserver,
            ConsultationListFilter,
            PaginationOfConsultationListAnchor,
            ItemWithIndexOfConsultationListElementModel,
            ConsultationListElementModel>