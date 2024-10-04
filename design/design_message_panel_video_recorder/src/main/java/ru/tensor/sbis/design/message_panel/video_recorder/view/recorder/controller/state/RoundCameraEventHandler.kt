package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state

import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.RoundCameraController
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.*
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraState.*

/**
 * Обработчик событий [RoundCameraController].
 *
 * @author vv.chekurda
 */
internal class RoundCameraEventHandler(
    private val executor: RoundCameraExecutor
) : RoundCameraStateController.EventHandler {

    override fun handleEvent(
        state: RoundCameraState,
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> =
        when (state) {
            is DefaultState -> state.handleEvent(event)
            is CameraPreparingState -> state.handleEvent(event)
            is RecorderPreparingState -> state.handleEvent(event)
            is RecordingState -> state.handleEvent(event)
            is StopRecordingState -> state.handleEvent(event)
            is CancelRecordingState -> state.handleEvent(event)
        }

    private fun DefaultState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        var sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerStateEvent.OnAttached -> {
                if (!isAttached) {
                    executor.initRecorder()
                    newState = copy(isAttached = true)
                }
            }
            ControllerStateEvent.OnDetached -> {
                newState = copy(isAttached = false)
            }
            ControllerStateEvent.OnRelease -> {
                executor.releaseController()
                newState = copy(isInitialized = false)
            }
            CameraEvent.OnCameraClosed -> {
                executor.clearState()
            }
            is Request.RequestInitController -> {
                executor.initController(event.fragment, event.fileFactory, event.listener)
                newState = copy(isInitialized = true)
            }
            is Request.RequestStartRecording -> {
                if (isAttached && isInitialized) {
                    executor.withPermissions {
                        executor.changeCameraStubVisibility(isVisible = true)
                        newState = CameraPreparingState(event.isFrontCamera)
                        sideEffect = ControllerAction.OpenCamera
                    }
                } else {
                    illegalState { "${RoundCameraController::class.java.simpleName} is not prepared for start recording, state = $this" }
                }
            }
            else -> Unit
        }
        return newState to sideEffect
    }

    private fun CameraPreparingState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        var sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerAction.OpenCamera -> {
                if (!isWaitingCamera) {
                    val surface = executor.getSurface()
                    newState = if (surface != null) {
                        executor.openCamera(surface, isFrontCamera)
                        copy(isWaitingCamera = true)
                    } else {
                        executor.addTexture()
                        copy(isWaitingSurface = true)
                    }
                } else {
                    illegalState { "Second trying to open camera, state = $this" }
                }
            }
            is SurfaceEvent.OnSurfaceAvailable -> {
                if (isWaitingSurface) {
                    newState = copy(isWaitingSurface = false)
                    sideEffect = ControllerAction.OpenCamera
                }
            }
            is CameraEvent.OnCameraOpened -> {
                if (isWaitingCamera) {
                    newState = RecorderPreparingState()
                    sideEffect = ControllerAction.PrepareRecorder
                } else {
                    illegalState { "Camera is opened, but it does not requested, state = $this" }
                }
            }
            is SurfaceEvent.OnSurfaceDestroyed -> {
                executor.closeCamera()
                newState = DefaultState(
                    isInitialized = true,
                    isAttached = true
                )
            }
            is CameraEvent.OnCameraClosed -> {
                newState = DefaultState(
                    isInitialized = true,
                    isAttached = true
                )
            }
            is Request.RequestStopRecording,
            is Request.RequestCancelRecording,
            is RecorderEvent.OnRecorderError,
            is ControllerStateEvent.OnDetached,
            is ControllerStateEvent.OnRelease -> {
                newState = CancelRecordingState()
                sideEffect = ControllerAction.CancelRecorder
            }
            else -> Unit
        }
        return newState to sideEffect
    }

    private fun RecorderPreparingState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        var sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerAction.PrepareRecorder -> {
                if (!isWaitingRecorder) {
                    executor.prepareRecorder()
                    newState = copy(isWaitingRecorder = true)
                } else {
                    illegalState { "Second trying to prepare recorder, state = $this" }
                }
            }
            is RecorderEvent.OnRecorderPrepared -> {
                if (isWaitingRecorder) {
                    executor.setCodecSurface(event.codecSurface)
                    newState = copy(isWaitingRecorder = false, isWaitingCodec = true)
                } else {
                    illegalState { "Recorder is prepared, but it does not requested, state = $this" }
                }
            }
            RecorderEvent.OnCodecSurfacePrepared -> {
                if (isWaitingCodec) {
                    newState = RecordingState()
                    sideEffect = ControllerAction.StartRecorder
                } else {
                    illegalState { "Codec surface is prepared, but it does not requested, state = $this" }
                }
            }
            is Request.RequestStopRecording,
            is Request.RequestCancelRecording,
            is RecorderEvent.OnRecorderError,
            is SurfaceEvent.OnSurfaceDestroyed,
            is CameraEvent.OnCameraClosed,
            is ControllerStateEvent.OnDetached,
            is ControllerStateEvent.OnRelease -> {
                newState = CancelRecordingState()
                sideEffect = ControllerAction.CancelRecorder
            }
            else -> Unit
        }
        return newState to sideEffect
    }

    private fun RecordingState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        var sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerAction.StartRecorder -> {
                if (!isWaitingRecorder) {
                    executor.startRecorder()
                    newState = copy(isWaitingRecorder = true)
                } else {
                    illegalState { "Second trying to start recorder, state = $this" }
                }
            }
            RecorderEvent.OnRecorderStarted -> {
                if (isWaitingRecorder) {
                    executor.onRecordStarted()
                    executor.changeCameraStubVisibility(isVisible = false)
                    newState = copy(isWaitingRecorder = false, isRecordRunning = true)
                } else {
                    illegalState { "Record is started, but it does not requested, state = $this" }
                }
            }
            Request.RequestSwitchCamera -> {
                if (isRecordRunning) {
                    executor.switchCamera()
                }
            }
            is Request.RequestStopRecording -> {
                if (isRecordRunning) {
                    newState = StopRecordingState(byTimeOut = event.byTimeOut)
                    sideEffect = ControllerAction.StopRecorder
                } else {
                    newState = CancelRecordingState()
                    sideEffect = ControllerAction.CancelRecorder
                }
            }
            is Request.RequestCancelRecording,
            is RecorderEvent.OnRecorderError,
            is SurfaceEvent.OnSurfaceDestroyed,
            is ControllerStateEvent.OnDetached,
            is ControllerStateEvent.OnRelease -> {
                newState = CancelRecordingState()
                sideEffect = ControllerAction.CancelRecorder
            }
            else -> Unit
        }
        return newState to sideEffect
    }

    private fun StopRecordingState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        var sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerAction.StopRecorder -> {
                if (!isWaitingRecorder) {
                    executor.stopRecorder()
                    newState = copy(isWaitingRecorder = true)
                }
            }
            is RecorderEvent.OnRecorderStopped -> {
                if (isWaitingRecorder) {
                    executor.onRecordStopped(event.file, event.durationSeconds, byTimeOut)
                    newState = DefaultState(isAttached = true, isInitialized = true)
                } else {
                    illegalState { "Record is stopped, but it does not requested, state = $this" }
                }
            }
            is Request.RequestCancelRecording,
            is RecorderEvent.OnRecorderError,
            is SurfaceEvent.OnSurfaceDestroyed,
            is CameraEvent.OnCameraClosed,
            is ControllerStateEvent.OnDetached,
            is ControllerStateEvent.OnRelease -> {
                newState = CancelRecordingState()
                sideEffect = ControllerAction.CancelRecorder
            }
            else -> Unit
        }
        return newState to sideEffect
    }

    private fun CancelRecordingState.handleEvent(
        event: RoundCameraEvent
    ): Pair<RoundCameraState, RoundCameraEvent?> {
        var newState: RoundCameraState = this
        val sideEffect: RoundCameraEvent? = null
        when (event) {
            ControllerAction.CancelRecorder -> {
                executor.cancelRecorder()
                executor.onRecordCancelled()
                newState = DefaultState(isAttached = true, isInitialized = true)
            }
            is RecorderEvent.OnRecorderCancelled -> Unit
            else -> Unit
        }
        return newState to sideEffect
    }
}