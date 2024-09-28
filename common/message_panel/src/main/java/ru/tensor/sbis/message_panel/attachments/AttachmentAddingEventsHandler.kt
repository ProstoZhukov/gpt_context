package ru.tensor.sbis.message_panel.attachments

import io.reactivex.functions.Consumer
import ru.tensor.sbis.attachments.models.property.MAX_PROGRESS
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.diskmobile.generated.OperationEvents
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import java.util.*
import ru.tensor.sbis.diskmobile.generated.OperationErrorCode

internal typealias AttachmentProgressHandler = (diskUuid: UUID, progress: Int) -> Unit
internal typealias AttachmentErrorHandler = (diskUuid: UUID, errorType: String) -> Unit

/**
 * //TODO Дока
 * Подписка на обновление прогресса загрузки у вложений
 *
 * @author vv.chekurda
 * Создан 10/9/2019
 */
internal class AttachmentAddingEventsHandler(
    private val onProgress: AttachmentProgressHandler,
    private val onError: AttachmentErrorHandler
) : Consumer<EventData> {

    override fun accept(event: EventData) {
        when (event.name) {
            OperationEvents.ON_UPLOAD_OPERATION_SUCCESS ->
                onProgress(event.diskUuid() ?: return, MAX_PROGRESS)
            OperationEvents.ON_UPLOAD_OPERATION_PROGRESS ->
                onProgress(event.diskUuid() ?: return, event.progress() ?: return)
            OperationEvents.ON_UPLOAD_OPERATION_FAILED -> {
                val errorCode: Int = event.data[OperationEvents.STATUS]?.toInt()
                    ?: -1
                val errorType = OperationErrorCode.values().getOrNull(errorCode)?.name.toString()
                onError(event.diskUuid() ?: return, errorType)
            }
        }
    }

    private fun EventData.diskUuid(): UUID? {
        val ids: List<String> = data[OperationEvents.ID]?.split('_') ?: return null
        return UUIDUtils.fromString(ids.getOrNull(0))
    }

    private fun EventData.progress(): Int? = data[OperationEvents.PROGRESS]?.toInt()
}