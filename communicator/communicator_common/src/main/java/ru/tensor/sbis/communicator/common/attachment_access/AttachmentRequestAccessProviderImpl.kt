package ru.tensor.sbis.communicator.common.attachment_access

import io.reactivex.Single
import ru.tensor.sbis.attachments.decl.action.AttachmentRequestAccessProvider
import ru.tensor.sbis.attachments.decl.action.data.SendRightAccessRequestResult
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.generated.MessageController

/**
 * Класс-реализация поставщика запроса прав на просмотр.
 */
class AttachmentRequestAccessProviderImpl(
    private val controllerProvider: DependencyProvider<MessageController>
) : AttachmentRequestAccessProvider {

    /** @SelfDocumented */
    override fun sendRightAccessRequest(attachmentDiskObjectId: String): Single<SendRightAccessRequestResult> =
        Single.fromCallable { controllerProvider.get().sendAccessRequest(attachmentDiskObjectId) }
            .map { status ->
                if (status.errorCode == ErrorCode.SUCCESS) {
                    SendRightAccessRequestResult.Success
                } else {
                    SendRightAccessRequestResult.Failure(
                        errorCode = status.errorCode.name,
                        userFriendlyErrorMessage = status.errorMessage
                    )
                }
            }
}