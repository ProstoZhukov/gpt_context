package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data

import ru.tensor.sbis.consultations.generated.CollectionOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.OperatorCollectionFilter
import ru.tensor.sbis.consultations.generated.OperatorViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfOperatorAnchor
import ru.tensor.sbis.crud3.domain.Wrapper

/**
 * Параметризованный Wrapper.
 * @see Wrapper
 *
 * @author da.zhukov
 */
internal interface CRMAnotherOperatorCollectionWrapper :
    Wrapper<CollectionOfOperatorViewModel,
            CRMAnotherOperatorCollectionObserver,
            OperatorCollectionFilter,
            PaginationOfOperatorAnchor,
            ItemWithIndexOfOperatorViewModel,
            OperatorViewModel>