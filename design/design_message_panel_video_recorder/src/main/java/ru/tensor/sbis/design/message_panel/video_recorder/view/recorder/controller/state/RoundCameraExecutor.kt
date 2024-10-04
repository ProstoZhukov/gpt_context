package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.controller.state

import android.graphics.SurfaceTexture
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract.RoundCameraListener
import java.io.File

/**
 * Исполнитель для обработки состояний [RoundCameraStateController].
 *
 * @author vv.chekurda
 */
internal interface RoundCameraExecutor {
    fun initController(fragment: Fragment, fileFactory: MediaFileFactory, listener: RoundCameraListener?)
    fun releaseController()
    fun initRecorder()
    fun releaseRecorder()
    fun withPermissions(action: () -> Unit)
    fun getSurface(): SurfaceTexture?

    fun openCamera(surfaceTexture: SurfaceTexture, isFrontCamera: Boolean)
    fun closeCamera()
    fun switchCamera()
    fun changeCameraStubVisibility(isVisible: Boolean)

    fun setCodecSurface(codecSurface: CodecSurfaceDrawer)
    fun prepareRecorder()
    fun startRecorder()
    fun stopRecorder()
    fun cancelRecorder()

    fun onRecordStarted()
    fun onRecordStopped(file: File, durationSeconds: Int, byTimeOut: Boolean)
    fun onRecordCancelled()
    fun onRecordError(error: Exception)

    fun addTexture()
    fun clearState()

}