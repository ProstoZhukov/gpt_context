package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.helper

import ru.tensor.sbis.communicator.generated.AnchorReadStatus
import ru.tensor.sbis.communicator.generated.ListResultOfMessageReceiverReadStatusMapOfStringString
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.META_IS_FIRST_PAGE
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusResultHelper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.isNetworkError
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.base.data.ResultHelper
import javax.inject.Inject

/**
 * Реализация вспомогательного класса для обработки результата контроллера статусов прочитанности сообщения
 * @see [ResultHelper]
 *
 * @author vv.chekurda
 */
internal class ReadStatusResultHelperImpl @Inject constructor(
    private val liveData: ReadStatusListVMLiveData
) : ReadStatusResultHelper,
    ReadStatusAnchorDelegate {

    override fun isEmpty(result: ListResultOfMessageReceiverReadStatusMapOfStringString): Boolean =
        (result.result.isEmpty() && !result.metadata.isNetworkError).also {
            result.checkNetworkError()
        }

    override fun isStub(result: ReadStatusListResult): Boolean =
        (result.isFirstPage() && result.result.isEmpty()).also {
            result.checkNetworkError()
        }

    override fun hasNext(result: ListResultOfMessageReceiverReadStatusMapOfStringString): Boolean =
        result.haveMore

    override fun getAnchorForNextPage(result: ListResultOfMessageReceiverReadStatusMapOfStringString): AnchorReadStatus =
        createNextPageAnchor(result.metadata)

    override fun getAnchorForPreviousPage(result: ListResultOfMessageReceiverReadStatusMapOfStringString): AnchorReadStatus =
        createPreviousPageAnchor()

    private fun ListResultOfMessageReceiverReadStatusMapOfStringString.isFirstPage(): Boolean =
        metadata?.get(META_IS_FIRST_PAGE.first) == META_IS_FIRST_PAGE.second

    private fun ListResultOfMessageReceiverReadStatusMapOfStringString.checkNetworkError() {
        if (metadata.isNetworkError) {
            liveData.dispatchNetworkError()
        }
    }
}