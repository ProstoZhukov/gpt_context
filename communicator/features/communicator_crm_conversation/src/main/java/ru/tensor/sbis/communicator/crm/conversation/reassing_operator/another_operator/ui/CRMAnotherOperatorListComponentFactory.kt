package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data.CRMAnotherOperatorCollectionWrapper
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.mapper.CRMAnotherOperatorMapper
import ru.tensor.sbis.consultations.generated.OperatorCollectionFilter
import ru.tensor.sbis.consultations.generated.OperatorViewModel
import ru.tensor.sbis.crud3.ListComponent
import ru.tensor.sbis.crud3.ListComponentView
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.view.item.AnyItem

/** @SelfDocumented */
internal class CRMAnotherOperatorListComponentFactory(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val wrapper: CRMAnotherOperatorCollectionWrapper,
    private val mapper: CRMAnotherOperatorMapper
) {

    private val stubFactory = StubFactory { type ->
        when (type) {
            StubType.NO_DATA -> StubViewCase.NO_DATA.getContent()
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }

    private var listComponent: ListComponent<OperatorCollectionFilter, OperatorViewModel, AnyItem>? = null

    /** @SelfDocumented */
    fun create(view: ListComponentView) = view.inject(
        viewModelStoreOwner,
        lazy { wrapper },
        lazy { mapper },
        lazy { stubFactory }
    ).also { listComponent = it }

    /** @SelfDocumented */
    fun get() = listComponent
}