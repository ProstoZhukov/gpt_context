package ru.tensor.sbis.communicator_support_consultation_list.presentation

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.communicator_support_consultation_list.R
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType

/**
 * Фабрика заглушек
 */
internal class StubFactory : StubFactory {
    override fun create(type: StubType) =
        when (type) {
            StubType.NO_NETWORK -> {
                StubViewCase.NO_CONNECTION.getContent()
            }
            else -> {
                ImageStubContent(
                    imageType = StubViewImageType.EMPTY,
                    messageRes = R.string.communicator_support_consultation_list_no_data,
                    detailsRes = ResourcesCompat.ID_NULL
                )
            }
        }
}
