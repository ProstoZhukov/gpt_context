package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.stub

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider
import javax.inject.Inject

/**
 * Поставщик фабрики для заглушек в списке статусов прочитанности
 * @see [StubContentProvider]
 *
 * @property liveData параметры состояния вью-модели
 *
 * @author vv.chekurda
 */
internal class ReadStatusStubContentProvider @Inject constructor(
    private val liveData: ReadStatusListVMLiveData
) : StubContentProvider<ReadStatusListResult> {

    override fun provideStubViewContentFactory(result: ReadStatusListResult?): StubViewContentFactory =
        ReadStatusStubContent(
            liveData.searchFilter.value!!,
            result?.metadata
        )
}
