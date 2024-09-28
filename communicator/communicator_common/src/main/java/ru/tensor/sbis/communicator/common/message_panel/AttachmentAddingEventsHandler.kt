package ru.tensor.sbis.communicator.common.message_panel

import java.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.models.property.MAX_PROGRESS
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsServiceEvents
import ru.tensor.sbis.diskmobile.generated.OperationErrorCode
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.ID
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.ON_UPLOAD_OPERATION_FAILED
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.ON_UPLOAD_OPERATION_PROGRESS
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.ON_UPLOAD_OPERATION_SUCCESS
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.PROGRESS
import ru.tensor.sbis.diskmobile.generated.OperationEvents.Companion.STATUS
import ru.tensor.sbis.platform.generated.EventCallback
import ru.tensor.sbis.platform.generated.EventManagerService
import timber.log.Timber

/**
 * Подписка на обновление прогресса загрузки у вложений
 *
 * @author vv.chekurda
 */
internal class AttachmentAddingEventsHandler(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : AttachmentsServiceEvents {

    /*
     TODO: не использовать сервис напрямую. Устранить утечки subscription в
      EventManagerServiceSubscriberImpl и использовать его
     */
    private val eventManagerService by lazy {
        EventManagerService.instance()
    }

    override val uploadingProgress = merge(
        callbackFlow {
            val subscription = eventManagerService.addEventCallback(
                ON_UPLOAD_OPERATION_SUCCESS,
                object : EventCallback() {
                    override fun onEvent(eventType: String, params: HashMap<String, String>) {
                        launch(dispatcher) {
                            if (eventType != ON_UPLOAD_OPERATION_SUCCESS) return@launch
                            val id = params.diskUuid() ?: return@launch
                            send(id to MAX_PROGRESS)
                        }
                    }
                }
            )
            awaitClose { subscription.disable() }
        },
        callbackFlow {
            val subscription = eventManagerService.addEventCallback(
                ON_UPLOAD_OPERATION_PROGRESS,
                object : EventCallback() {
                    override fun onEvent(eventType: String, params: HashMap<String, String>) {
                        launch(dispatcher) {
                            if (eventType != ON_UPLOAD_OPERATION_PROGRESS) return@launch
                            val id = params.diskUuid() ?: return@launch
                            val progress = params[PROGRESS]?.toInt() ?: return@launch
                            send(id to progress)
                        }
                    }
                }
            )
            awaitClose { subscription.disable() }
        }
    )

    override val uploadingError = callbackFlow {
        val subscription = eventManagerService.addEventCallback(
            ON_UPLOAD_OPERATION_FAILED,
            object : EventCallback() {
                override fun onEvent(eventType: String, params: HashMap<String, String>) {
                    launch(dispatcher) {
                        if (eventType != ON_UPLOAD_OPERATION_FAILED) return@launch
                        val errorCode: Int = params[STATUS]?.toInt() ?: -1
                        val errorType = OperationErrorCode.values().getOrNull(errorCode)
                        Timber.w("File uploading failed with params = %s", params)
                        val id = params.diskUuid() ?: return@launch
                        send(id to errorType?.name.toString())
                    }
                }
            }
        )
        awaitClose { subscription.disable() }
    }

    private fun Map<String, String>.diskUuid(): UUID? {
        val ids: List<String> = get(ID)?.split('_') ?: return null
        return UUIDUtils.fromString(ids.firstOrNull())
    }
}