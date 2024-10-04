package ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.os.ExecutorCompat
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DispatchQueue
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera.SurfaceCameraManager.CameraState.*
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.gl.CameraGLDrawer

/**
 * Менеджер для работы с камерами с выводом изображения на [Surface].
 *
 * @author vv.chekurda
 */
internal class SurfaceCameraManager(private val context: Context) {

    internal data class CameraInfo(
        val id: String,
        val isFront: Boolean,
        val previewSize: Size,
        val rotation: Int
    )

    internal enum class CameraState {
        PREPARING,
        OPENED,
        CLOSED
    }

    internal fun interface CameraStateChangeListener {

        fun onCameraStateChanged(state: CameraState)
    }

    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val windowService = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val cameraThread = DispatchQueue(CAMERA_MANAGER_THREAD_NAME)
        .apply { priority = Thread.MAX_PRIORITY }
    private val glDrawer = CameraGLDrawer()
    private var cameraSurface: SurfaceTexture? = null
    private var previewSession: CameraCaptureSession? = null

    private var cameraDevice: CameraDevice? = null
    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startCaptureSession(camera)
        }

        override fun onDisconnected(camera: CameraDevice) {
            if (cameraDevice == camera) closeCamera()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            if (cameraDevice == camera) closeCamera()
        }
    }

    private val rotation: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            windowService.defaultDisplay
        }.let { display ->
            display?.rotation ?: Surface.ROTATION_0
        }

    /**
     * Состояние камеры.
     */
    var cameraState: CameraState = CLOSED
        private set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) mainHandler.post {
                stateChangeListener?.onCameraStateChanged(value)
            }
        }

    /**
     * Признак фронтальной камеры.
     */
    var isFrontCamara: Boolean = true
        private set

    /**
     * Слушатель изменения состояния камеры.
     */
    var stateChangeListener: CameraStateChangeListener? = null

    /**
     * Открыть камеру.
     */
    fun openCamera(surfaceTexture: SurfaceTexture, surfaceSize: Size, isFront: Boolean) {
        if (cameraState != CLOSED) return
        cameraState = PREPARING
        cameraThread.post {
            releaseCamera()
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraState = CLOSED
                return@post
            }
            val cameraInfo = getCameraInfo(isFront, surfaceSize) ?: run {
                cameraState = CLOSED
                return@post
            }
            isFrontCamara = isFront
            cameraSurface = glDrawer.prepareCameraSurface(surfaceTexture, surfaceSize, cameraInfo)?.apply {
                setDefaultBufferSize(cameraInfo.previewSize.width, cameraInfo.previewSize.height)
            } ?: return@post

            cameraManager.openCamera(cameraInfo.id, cameraStateCallback, cameraThread.requireHandler())
        }
    }

    /**
     * Сменить камеру на фронтальную/основную.
     */
    fun switchCamera(surfaceSize: Size): Boolean =
        if (cameraState == OPENED) {
            cameraState = PREPARING
            cameraThread.post {
                closeCaptureSession()
                cameraDevice?.close()
                cameraDevice = null
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    cameraState = CLOSED
                    return@post
                }
                val cameraInfo = getCameraInfo(!isFrontCamara, surfaceSize) ?: run {
                    cameraState = CLOSED
                    return@post
                }
                isFrontCamara = !isFrontCamara
                cameraSurface?.setDefaultBufferSize(cameraInfo.previewSize.width, cameraInfo.previewSize.height)
                cameraManager.openCamera(cameraInfo.id, cameraStateCallback, cameraThread.requireHandler())
            }
            true
        } else {
            false
        }

    /**
     * Сменить камеру на фронтальную/основную.
     */
    fun setCodecSurface(codecSurface: CodecSurfaceDrawer, callback: (() -> Unit)? = null) {
        cameraThread.post {
            glDrawer.setCodecSurface(codecSurface)
            callback?.also { mainHandler.post { callback() } }
        }
    }

    /**
     * Закрыть камеру.
     */
    fun closeCamera() {
        cameraState = CLOSED
        releaseCamera()
    }

    /**
     * Высвобождение ресурсов.
     */
    fun release() {
        closeCamera()
        stateChangeListener = null
        cameraThread.recycle()
    }

    private fun startCaptureSession(camera: CameraDevice) {
        closeCaptureSession()
        val cameraSurface = cameraSurface ?: return
        val previewSurface = Surface(cameraSurface)
        val captureCallback = object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                previewSession = session
                val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
                    addTarget(previewSurface)
                    set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                }
                val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureStarted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        timestamp: Long,
                        frameNumber: Long
                    ) {
                        if (cameraState == PREPARING) {
                            cameraState = OPENED
                        }
                    }
                }
                session.setRepeatingRequest(
                    captureRequestBuilder.build(), captureCallback, cameraThread.requireHandler()
                )
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                session.close()
                previewSurface.release()
                previewSession = null
                cameraState = CLOSED
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val sessionConfig = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    mutableListOf(OutputConfiguration(previewSurface)),
                    ExecutorCompat.create(cameraThread.requireHandler()),
                    captureCallback
                )
                camera.createCaptureSession(sessionConfig)
            } else {
                @Suppress("DEPRECATION")
                camera.createCaptureSession(listOf(previewSurface), captureCallback, cameraThread.requireHandler())
            }
        } catch (ignore: CameraAccessException) {
            // Полное закрытие сессии происходит в колбеке [cameraStateCallback].
        }
    }

    private fun closeCaptureSession() {
        try {
            previewSession?.close()
        } catch (ignore: CameraAccessException) {
            // Бывает, что при блокировке экрана камера отключается и сессия автоматически завершается.
        } finally {
            previewSession = null
        }
    }

    private fun releaseCamera() {
        cameraSurface?.setOnFrameAvailableListener(null)
        glDrawer.release()
        closeCaptureSession()
        cameraDevice?.close()
        cameraDevice = null
        cameraSurface?.release()
        cameraSurface = null
    }

    private fun getCameraInfo(isFront: Boolean, surfaceSize: Size): CameraInfo? {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val isFrontCamera = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
            if (isFrontCamera != isFront) continue

            val configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?: continue
            val outputSized = configurationMap.getOutputSizes(SurfaceTexture::class.java)
            val aspectRatio = chooseAspectRatio(outputSized)
            val previewSize = chooseOptimalSize(outputSized, surfaceSize, aspectRatio)
            return CameraInfo(cameraId, isFront, previewSize, rotation)
        }
        return null
    }

    private fun chooseOptimalSize(
        choices: Array<Size>,
        surfaceSize: Size,
        aspectRatio: Size
    ): Size {
        val bigEnoughWithAspectRatio: MutableList<Size> = ArrayList()
        val bigEnough: MutableList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (a in choices.indices) {
            val option = choices[a]
            if (option.height == option.width * h / w && option.width >= surfaceSize.width && option.height >= surfaceSize.height) {
                bigEnoughWithAspectRatio.add(option)
            } else if (option.height * option.width <= surfaceSize.width * surfaceSize.height * 4) {
                bigEnough.add(option)
            }
        }
        val comparator = Comparator<Size> { size1, size2 ->
            java.lang.Long.signum(size1.width.toLong() * size1.height - size2.width.toLong() * size2.height)
        }
        return when {
            bigEnoughWithAspectRatio.size > 0 -> {
                bigEnoughWithAspectRatio.minOfWith(comparator) { it }
            }
            bigEnough.size > 0 -> {
                bigEnough.minOfWith(comparator) { it }
            }
            else -> {
                choices.maxOfWith(comparator) { it }
            }
        }
    }

    private fun chooseAspectRatio(choices: Array<Size>): Size =
        choices.find { 1920 == it.width && 1080 == it.height }
            ?: choices.find { it.width == it.height * 4 / 3 && it.width <= 1080 }
            ?: choices.last()
}

private const val CAMERA_MANAGER_THREAD_NAME = "surfaceCameraManagerQueue"