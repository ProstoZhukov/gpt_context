package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state

import android.os.Looper
import android.view.View
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraState.DefaultState
import timber.log.Timber

/**
 * Контроллер состояний камеры для записи круглых видео.
 *
 * @author vv.chekurda
 */
internal class RoundCameraStateController(
    private val view: View,
    private val eventHandler: EventHandler
) {

    constructor(view: View, executor: RoundCameraExecutor) : this(view, RoundCameraEventHandler(executor))

    interface EventHandler {

        fun handleEvent(
            state: RoundCameraState,
            event: RoundCameraEvent,
        ): Pair<RoundCameraState, RoundCameraEvent?>
    }

    var state: RoundCameraState = DefaultState()
        private set

    fun produceEvent(event: RoundCameraEvent) {
        // https://online.sbis.ru/opendoc.html?guid=4f4accb9-8662-4671-ad40-c28c360191d0&client=3
        if (Looper.myLooper() == Looper.getMainLooper()) {
            eventHandler.handleEvent(state, event).also { (newState, sideEffect) ->
                state = newState
                sideEffect?.let(::produceEvent)
            }
        } else {
            view.post { produceEvent(event) }
            Timber.e(IllegalStateException("RoundCameraStateController.produceEvent onWorkerThread $event"))
        }
    }
}
