package ru.tensor.sbis.design.message_panel.video_recorder.recorder.gl

import android.graphics.SurfaceTexture
import android.opengl.*
import android.util.Size
import android.view.Surface
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.camera.SurfaceCameraManager.CameraInfo
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.TextureData
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

/**
 * Вспомогательная реализация для отрисовки изображения с камеры на [Surface].
 *
 * @author vv.chekurda
 */
internal class CameraGLDrawer {

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private val mVPMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
    }
    private val sTMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
    }

    private var drawProgram: Int = 0
    private var positionHandle: Int = 0
    private var textureHandle: Int = 0
    private var vertexMatrixHandle: Int = 0
    private var textureMatrixHandle: Int = 0

    private var egl10: EGL10? = null
    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglSurface: EGLSurface? = null

    private var scaleX = 1f
    private var scaleY = 1f

    private var rotation: Float = 0f

    private val cameraTexturesArray = IntArray(1)
    private val cameraTexture: Int
        get() = cameraTexturesArray[0]

    private var codecSurface: CodecSurfaceDrawer? = null

    @Volatile
    private var isReleased = false

    /**
     * Подготовить [SurfaceTexture] для камеры.
     */
    fun prepareCameraSurface(
        surfaceTexture: SurfaceTexture,
        surfaceSize: Size,
        cameraInfo: CameraInfo
    ): SurfaceTexture? {
        isReleased = false

        val egl = EGLContext.getEGL() as EGL10
        egl10 = egl
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            Timber.e("CameraGLDrawer: eglGetDisplay failed ${GLUtils.getEGLErrorString(egl.eglGetError())}")
            release()
            return null
        }

        val version = IntArray(2)
        if (!egl.eglInitialize(eglDisplay, version)) {
            Timber.e("CameraGLDrawer: eglInitialize failed ${GLUtils.getEGLErrorString(egl.eglGetError())}")
            release()
            return null
        }

        val configs = arrayOfNulls<EGLConfig>(1)
        val configSpec = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_WINDOW_BIT,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 0,
            EGL10.EGL_DEPTH_SIZE, 0,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_NONE
        )
        val configsCount = IntArray(1)
        val eglConfig: EGLConfig
        when {
            !egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount) -> {
                Timber.e("CameraGLDrawer: eglInitialize failed ${GLUtils.getEGLErrorString(egl.eglGetError())}")
                release()
                return null
            }
            configsCount[0] > 0 -> {
                eglConfig = configs.first()!!
            }
            else -> {
                Timber.e("CameraGLDrawer: eglConfig not initialized")
                release()
                return null
            }
        }

        val attributeList = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attributeList)
        if (eglContext == null) {
            Timber.e("CameraGLDrawer: eglCreateContext failed ${GLUtils.getEGLErrorString(egl.eglGetError())}")
            release()
            return null
        }

        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceTexture, null)
        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            Timber.e("CameraGLDrawer: eglMakeCurrent failed  ${GLUtils.getEGLErrorString(egl.eglGetError())}")
            release()
            return null
        }

        cameraInfo.previewSize.let {
            var width: Float = it.width.toFloat()
            var height: Float = it.height.toFloat()
            val scale = surfaceSize.width.toFloat() / minOf(width, height)
            width *= scale
            height *= scale
            if (width > height) {
                scaleX = 1f
                scaleY = width / surfaceSize.height.toFloat()
            } else {
                scaleX = height / surfaceSize.width.toFloat()
                scaleY = 1f
            }
        }

        val tX = 1f / scaleX / 2f
        val tY = 1f / scaleY / 2f
        val verticesData = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, 1f, 0f
        )
        val texData = floatArrayOf(
            0.5f - tX, 0.5f - tY,
            0.5f + tX, 0.5f - tY,
            0.5f - tX, 0.5f + tY,
            0.5f + tX, 0.5f + tY
        )
        vertexBuffer = ByteBuffer.allocateDirect(verticesData.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(verticesData)
                position(0)
            }
        textureBuffer = ByteBuffer.allocateDirect(texData.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(texData)
                position(0)
            }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        if (vertexShader != 0 && fragmentShader != 0) {
            drawProgram = GLES20.glCreateProgram().apply {
                GLES20.glAttachShader(this, vertexShader)
                GLES20.glAttachShader(this, fragmentShader)
                GLES20.glLinkProgram(this)
            }
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(drawProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != 0) {
                positionHandle = GLES20.glGetAttribLocation(drawProgram, A_POSITION_KEY)
                textureHandle = GLES20.glGetAttribLocation(drawProgram, A_TEXTURE_COORD_KEY)
                vertexMatrixHandle = GLES20.glGetUniformLocation(drawProgram, U_MVP_MATRIX_KEY)
                textureMatrixHandle = GLES20.glGetUniformLocation(drawProgram, U_ST_MATRIX_KEY)
            } else {
                Timber.e("CameraGLDrawer: Failed link shader")
                GLES20.glDeleteProgram(drawProgram)
                drawProgram = 0

                release()
                return null
            }
        } else {
            Timber.e("CameraGLDrawer: Failed creating shader")

            release()
            return null
        }

        GLES20.glGenTextures(1, cameraTexturesArray, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexture)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        applyRotation(cameraInfo)

        return SurfaceTexture(cameraTexture).apply {
            setOnFrameAvailableListener(::draw)
        }
    }

    private fun draw(cameraSurface: SurfaceTexture) {
        if (isReleased) return
        val egl10 = egl10 ?: return
        val eglContext = eglContext ?: return
        val eglSurface = eglSurface ?: return

        if ((eglContext != egl10.eglGetCurrentContext()
            || eglSurface != egl10.eglGetCurrentSurface(EGL10.EGL_DRAW))
            && !egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            Timber.e("CameraGLDrawer: eglMakeCurrent failed ${GLUtils.getEGLErrorString(egl10.eglGetError())}")
            return
        }
        cameraSurface.updateTexImage()
        codecSurface?.draw(
            TextureData(
                cameraTexture,
                mVPMatrix,
                sTMatrix,
                vertexBuffer,
                textureBuffer,
                cameraSurface.timestamp
            )
        )

        cameraSurface.getTransformMatrix(sTMatrix)

        GLES20.glUseProgram(drawProgram)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexture)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)
        GLES20.glEnableVertexAttribArray(textureHandle)

        GLES20.glUniformMatrix4fv(vertexMatrixHandle, 1, false, mVPMatrix, 0)
        GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, sTMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexture)
        GLES20.glUseProgram(0)

        try {
            egl10.eglSwapBuffers(eglDisplay, eglSurface)
        } catch (ignore: Exception) {}
    }

    /**
     * Установить кодек [CodecSurfaceDrawer].
     */
    fun setCodecSurface(codecSurface: CodecSurfaceDrawer) {
        this.codecSurface = codecSurface.apply {
            init(EGL14.eglGetCurrentContext())
        }
    }

    /**
     * Высвобождение ресурсов.
     */
    fun release() {
        isReleased = true
        codecSurface = null
        Matrix.rotateM(mVPMatrix, 0, -rotation, 0f, 0f, 1f)
        rotation = 0f
        val egl10 = egl10 ?: return
        eglSurface?.let {
            egl10.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
            egl10.eglDestroySurface(eglDisplay, it)
            eglSurface = null
        }
        eglContext?.let {
            egl10.eglDestroyContext(eglDisplay, it)
            eglContext = null
        }
        eglDisplay?.let {
            egl10.eglTerminate(it)
            eglDisplay = null
        }
        this.egl10 = null

        GLES20.glDeleteTextures(1, cameraTexturesArray, 0)
        if (drawProgram <= 0) return
        GLES20.glDeleteProgram(drawProgram)
        drawProgram = 0
    }

    private fun applyRotation(cameraInfo: CameraInfo) {
        with(cameraInfo) {
            val angle = if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                90f * rotation
            } else {
                0f
            }
            this@CameraGLDrawer.rotation = angle
            Matrix.rotateM(mVPMatrix, 0, angle, 0f, 0f, 1f)
        }
    }
}

private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098