package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller

import android.graphics.SurfaceTexture
import android.media.MediaMuxer
import android.util.Size
import android.view.TextureView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MediaMuxerWrapper
import ru.tensor.sbis.design.message_panel.recorder_common.utils.RecordingDeviceHelper
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordResultData
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.SurfaceVideoRecorder
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera.SurfaceCameraManager
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera.SurfaceCameraManager.CameraState.*
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera.SurfaceCameraManager.CameraStateChangeListener
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.listener.SurfaceVideoRecordListener
import ru.tensor.sbis.design.message_panel.video_recorder.utils.VideoPermissionsHelper
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraApi
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.RoundCameraView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children.CameraFlickerStubView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children.RoundProgressView
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraListener
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.CameraEvent.*
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.ControllerStateEvent.OnRelease
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.RecorderEvent.*
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.Request.*
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraEvent.SurfaceEvent.*
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraExecutor
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state.RoundCameraStateController
import java.io.File

/**
 * View-контроллер компонента записи круглых видео [RoundCameraView].
 * @see RoundCameraApi
 *
 * @author vv.chekurda
 */
internal class RoundCameraController : RoundCameraApi, LifecycleObserver {

    private lateinit var cameraView: RoundCameraView
    private lateinit var textureView: TextureView
    private lateinit var progressView: RoundProgressView
    private lateinit var cameraStubView: CameraFlickerStubView

    private lateinit var cameraManager: SurfaceCameraManager
    private lateinit var videoRecorder: SurfaceVideoRecorder

    private var permissionsHelper: VideoPermissionsHelper? = null
    private var deviceHelper: RecordingDeviceHelper? = null
    private var videoFileFactory: MediaFileFactory? = null
    private var listener: RoundCameraListener? = null

    override var autoClearState: Boolean = true

    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            stateController.produceEvent(OnSurfaceAvailable(surface))
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            stateController.produceEvent(OnSurfaceDestroyed)
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) = Unit
    }

    private val cameraStateChangeListener: CameraStateChangeListener =
        CameraStateChangeListener { state ->
            when (state) {
                OPENED -> OnCameraOpened
                CLOSED -> OnCameraClosed
                else -> null
            }?.also(stateController::produceEvent)
        }

    private val recorderListener = object : SurfaceVideoRecordListener {
        override fun onPrepared(surfaceDrawer: CodecSurfaceDrawer) {
            stateController.produceEvent(OnRecorderPrepared(surfaceDrawer))
        }

        override fun onStarted() {
            stateController.produceEvent(OnRecorderStarted)
        }

        override fun onFinished(file: File, durationSeconds: Int) {
            stateController.produceEvent(OnRecorderStopped(file, durationSeconds))
        }

        override fun onCancelled() {
            stateController.produceEvent(OnRecorderCancelled)
        }

        override fun onError(error: Exception) {
            stateController.produceEvent(OnRecorderError(error))
        }

        override fun onVolumeAmplitudeChanged(amplitude: Float) {
            listener?.onVolumeAmplitudeChanged(amplitude)
        }
    }

    private val executor = object : RoundCameraExecutor {
        override fun initController(
            fragment: Fragment,
            fileFactory: MediaFileFactory,
            listener: RoundCameraListener?
        ) {
            fragment.lifecycle.addObserver(this@RoundCameraController)
            deviceHelper = RecordingDeviceHelper(fragment.requireActivity()).apply {
                isLockOrientationEnabled = false
            }
            permissionsHelper = VideoPermissionsHelper(fragment.requireActivity())
            videoFileFactory = fileFactory
            this@RoundCameraController.listener = listener
        }

        override fun releaseController() {
            deviceHelper?.configureDevice(false)
            deviceHelper = null
            permissionsHelper = null
            videoFileFactory = null
        }

        override fun initRecorder() {
            videoRecorder = SurfaceVideoRecorder().apply {
                recordListener = recorderListener
            }
            cameraManager = SurfaceCameraManager(cameraView.context).apply {
                stateChangeListener = cameraStateChangeListener
            }
        }

        override fun releaseRecorder() {
            videoRecorder.release()
            cameraManager.release()
        }

        override fun withPermissions(action: () -> Unit) {
            this@RoundCameraController.withPermissions(action)
        }

        override fun getSurface(): SurfaceTexture? =
            textureView.surfaceTexture

        override fun openCamera(surfaceTexture: SurfaceTexture, isFrontCamera: Boolean) {
            checkNotNull(deviceHelper).configureDevice(isStartRecording = true)
            cameraManager.openCamera(
                surfaceTexture,
                Size(textureView.width, textureView.height),
                isFrontCamera
            )
        }

        override fun switchCamera() {
            cameraManager.switchCamera(surfaceSize = Size(textureView.width, textureView.height))
        }

        override fun closeCamera() {
            deviceHelper?.configureDevice(isStartRecording = false)
            cameraManager.closeCamera()
        }

        override fun changeCameraStubVisibility(isVisible: Boolean) {
            cameraStubView.apply {
                if (isVisible) {
                    this.isVisible = true
                    start()
                } else {
                    hide()
                }
            }
        }

        override fun setCodecSurface(codecSurface: CodecSurfaceDrawer) {
            cameraManager.setCodecSurface(codecSurface) {
                stateController.produceEvent(OnCodecSurfacePrepared)
            }
        }

        override fun prepareRecorder() {
            val file = checkNotNull(videoFileFactory).createFile()
            val muxer = MediaMuxerWrapper(
                file,
                encodersCount = 2,
                outputFormat = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
            )
            videoRecorder.prepare(muxer)
        }

        override fun startRecorder() {
            videoRecorder.startRecording()
        }

        override fun stopRecorder() {
            videoRecorder.stopRecording()
        }

        override fun cancelRecorder() {
            videoRecorder.cancelRecording()
        }

        override fun onRecordStarted() {
            textureView.alpha = 1f
            progressView.start(VIDEO_MAX_RECORD_TIME_MS)
            listener?.onRecordStarted()
        }

        override fun onRecordStopped(file: File, durationSeconds: Int, byTimeOut: Boolean) {
            closeCamera()
            listener?.onRecordCompleted(VideoRecordResultData(file, durationSeconds), byTimeOut)
        }

        override fun onRecordCancelled() {
            closeCamera()
            listener?.onRecordCanceled()
        }

        override fun onRecordError(error: Exception) {
            closeCamera()
            listener?.onRecordError(error)
        }

        override fun addTexture() {
            if (textureView.parent == null) {
                cameraView.addView(textureView, 0)
            }
        }

        override fun clearState() {
            if (autoClearState) {
                this@RoundCameraController.clearState()
            }
        }
    }

    private val stateController: RoundCameraStateController by lazy {
        RoundCameraStateController(cameraView, executor)
    }

    fun attachViews(
        cameraView: RoundCameraView,
        textureView: TextureView,
        progressView: RoundProgressView,
        cameraStubView: CameraFlickerStubView
    ) {
        this.cameraView = cameraView
        this.textureView = textureView.apply {
            surfaceTextureListener = textureListener
        }
        this.progressView = progressView
        this.cameraStubView = cameraStubView
        progressView.onProgressFinishedListener = {
            stopRecordingByTimeOut()
        }
    }

    fun onAttachedToWindow() {
        stateController.produceEvent(RoundCameraEvent.ControllerStateEvent.OnAttached)
    }

    fun onDetachedFromWindow() {
        stateController.produceEvent(RoundCameraEvent.ControllerStateEvent.OnDetached)
    }

    override fun initController(
        fragment: Fragment,
        fileFactory: MediaFileFactory,
        listener: RoundCameraListener?
    ) {
        stateController.produceEvent(RequestInitController(fragment, fileFactory, listener))
    }

    override fun startRecording(useFrontCamera: Boolean) {
        stateController.produceEvent(RequestStartRecording(useFrontCamera))
    }

    override fun stopRecording() {
        stateController.produceEvent(RequestStopRecording(byTimeOut = false))
    }

    private fun stopRecordingByTimeOut() {
        stateController.produceEvent(RequestStopRecording(byTimeOut = true))
    }

    override fun cancelRecording() {
        stateController.produceEvent(RequestCancelRecording)
    }

    override fun switchCamera() {
        stateController.produceEvent(RequestSwitchCamera)
    }

    override fun clearState() {
        if (textureView.parent != null) {
            textureView.alpha = 0f
            cameraView.removeView(textureView)
        }
        progressView.stop()
        cameraStubView.clear()
    }

    override fun withPermissions(action: () -> Unit) {
        checkNotNull(permissionsHelper).withPermissions(action)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun release() {
        stateController.produceEvent(OnRelease)
    }
}

private const val VIDEO_MAX_RECORD_TIME_MS = 3 * 60 * 1000L