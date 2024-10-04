package ru.tensor.sbis.design_selection.contract.stubs

import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * Стандартная реализация фабрики заглушек компонента выбора.
 *
 * @author vv.chekurda
 */
class DefaultSelectionStubFactory : StubFactory {

    override fun create(type: StubType) =
        when (type) {
            StubType.BAD_FILTER -> StubViewCase.NO_SEARCH_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
            StubType.NO_DATA -> ImageStubContent(
                imageType = StubViewCase.NO_SEARCH_RESULTS.imageType,
                messageRes = R.string.design_selection_all_selected_stub_title,
                details = null
            )
        }
}