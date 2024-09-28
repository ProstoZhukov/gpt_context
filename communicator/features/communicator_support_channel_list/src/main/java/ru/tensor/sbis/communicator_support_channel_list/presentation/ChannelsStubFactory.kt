package ru.tensor.sbis.communicator_support_channel_list.presentation

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType
import ru.tensor.sbis.design.stubview.ResourceImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewImageType

/**
 * Фабрика заглушек
 */
internal class ChannelsStubFactory(private val isSabyGet: Boolean) : StubFactory {
    override fun create(type: StubType) = when (type) {
        StubType.NO_NETWORK -> {
            StubViewCase.NO_CONNECTION.getContent()
        }
        else -> {
            if (isSabyGet) {
                ResourceImageStubContent(
                    icon = R.drawable.communicator_support_channel_sabyget_stub_empty,
                    messageRes = R.string.communicator_support_channel_sabyget_message,
                    detailsRes = R.string.communicator_support_channel_sabyget_stub_subtitle
                )
            } else {
                ImageStubContent(
                    imageType = StubViewImageType.EMPTY,
                    messageRes = R.string.communicator_support_channel_list_no_data,
                    detailsRes = ResourcesCompat.ID_NULL
                )
            }
        }
    }
}
