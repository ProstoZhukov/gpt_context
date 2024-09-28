package ru.tensor.sbis.crud4.view

import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Фабрика заглушки по-умолчанию.
 */
class DefaultStubViewContentFactory : StubFactory {

    override fun create(type: StubType): StubViewContent {
        return when (type) {
            StubType.NO_DATA -> StubViewCase.NO_DATA.getContent()
            StubType.BAD_FILTER -> StubViewCase.NO_FILTER_RESULTS.getContent()
            StubType.NO_NETWORK -> StubViewCase.NO_CONNECTION.getContent()
            StubType.SERVER_TROUBLE -> StubViewCase.SERVICE_UNAVAILABLE.getContent()
        }
    }
}