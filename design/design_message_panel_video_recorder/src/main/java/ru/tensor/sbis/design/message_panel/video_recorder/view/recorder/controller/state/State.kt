package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state

import android.graphics.SurfaceTexture
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.RoundCameraView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraListener
import java.io.File

/**
 * Состояния компонента записи круглых видео [RoundCameraView].
 *
 * @author vv.chekurda
 */
internal sealed class RoundCameraState {

    data class DefaultState(
        val isInitialized: Boolean = false,
        val isAttached: Boolean = false
    ) : RoundCameraState()

    data class CameraPreparingState(
        val isFrontCamera: Boolean,
        val isWaitingSurface: Boolean = false,
        val isWaitingCamera: Boolean = false
    ) : RoundCameraState()

    data class RecorderPreparingState(
        val isWaitingRecorder: Boolean = false,
        val isWaitingCodec: Boolean = false
    ) : RoundCameraState()

    data class RecordingState(
        val isWaitingRecorder: Boolean = false,
        val isRecordRunning: Boolean = false
    ) : RoundCameraState()

    data class StopRecordingState(
        val isWaitingRecorder: Boolean = false,
        val byTimeOut: Boolean = false
    ) : RoundCameraState()

    data class CancelRecordingState(
        val isWaitingRecorder: Boolean = false
    ) : RoundCameraState()
}

internal sealed class RoundCameraEvent {

    sealed class Request : RoundCameraEvent() {
        data class RequestInitController(
            val fragment: Fragment,
            val fileFactory: MediaFileFactory,
            val listener: RoundCameraListener?
        ) : Request()
        data class RequestStartRecording(val isFrontCamera: Boolean) : Request()
        data class RequestStopRecording(val byTimeOut: Boolean) : Request()
        object RequestCancelRecording : Request()
        object RequestSwitchCamera : Request()
    }

    sealed class SurfaceEvent : RoundCameraEvent() {
        data class OnSurfaceAvailable(val surface: SurfaceTexture) : SurfaceEvent()
        object OnSurfaceDestroyed : SurfaceEvent()
    }

    sealed class CameraEvent : RoundCameraEvent() {
        object OnCameraOpened : CameraEvent()
        object OnCameraClosed : CameraEvent()
    }

    sealed class ControllerStateEvent : RoundCameraEvent() {
        object OnAttached: ControllerStateEvent()
        object OnDetached: ControllerStateEvent()
        object OnRelease : ControllerStateEvent()
    }

    sealed class ControllerAction : RoundCameraEvent() {
        object OpenCamera : ControllerAction()
        object PrepareRecorder : ControllerAction()
        object StartRecorder : ControllerAction()
        object StopRecorder : ControllerAction()
        object CancelRecorder : ControllerAction()
    }

    sealed class RecorderEvent : RoundCameraEvent() {
        data class OnRecorderPrepared(val codecSurface: CodecSurfaceDrawer) : RecorderEvent()
        object OnCodecSurfacePrepared : CameraEvent()
        object OnRecorderStarted : RecorderEvent()
        data class OnRecorderStopped(val file: File, val durationSeconds: Int) : RecorderEvent()
        object OnRecorderCancelled : RecorderEvent()
        data class OnRecorderError(val error: Exception) : RecorderEvent()
    }

    override fun toString(): String =
        javaClass.simpleName
}
