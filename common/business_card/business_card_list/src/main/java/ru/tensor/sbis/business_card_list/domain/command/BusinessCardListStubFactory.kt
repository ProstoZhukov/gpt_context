package ru.tensor.sbis.business_card_list.domain.command

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.viper.R
import ru.tensor.sbis.viper.ui.SabyClientsListView

/**
 * Фабрика заглушек для списка визиток
 */
internal class BusinessCardListStubFactory(private val resourceProvider: ResourceProvider) : StubFactory {

    private val noDataStub =
        SabyClientsListView.getStubContent(resourceProvider, titleResId = R.string.viper_no_items_placeholder)

    override fun create(type: StubType): StubViewContent =
        when (type) {
            StubType.NO_DATA -> noDataStub
            StubType.BAD_FILTER -> noDataStub
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
}