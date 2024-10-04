package ru.tensor.sbis.design.message_panel.video_recorder.recorder.gl

import android.graphics.SurfaceTexture
import android.opengl.*
import android.view.Surface
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.TextureData

/**
 * OpenGL декодер для переноса изображения [TextureData] на [Surface].
 *
 * @author vv.chekurda
 */
internal class EncodeGLDrawer {

    private var eglDisplay = EGL14.EGL_NO_DISPLAY
    private var eglContext = EGL14.EGL_NO_CONTEXT
    private var eglSurface = EGL14.EGL_NO_SURFACE
    private var eglConfig: EGLConfig? = null

    private var drawProgram: Int = 0
    private var positionHandle: Int = 0
    private var textureHandle: Int = 0
    private var vertexMatrixHandle: Int = 0
    private var textureMatrixHandle: Int = 0

    private var lastTimestampNanos: Long = 0L
    private var currentTimestampNanos: Long = 0L
    private var isPrepared = false

    /**
     * Проинициализировать декодер.
     */
    fun init(inputSurface: Surface, sharedEglContext: EGLContext) {
        clear()
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        check(EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            eglDisplay = null
            "Unable to initialize EGL14"
        }

        val configAttrs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL_RECORDABLE_ANDROID, 1,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        check(EGL14.eglChooseConfig(eglDisplay, configAttrs, 0, configs, 0, configs.size, numConfigs, 0)) {
            eglDisplay = null
            "Unable to find a suitable EGLConfig"
        }
        val clientAttrs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], sharedEglContext, clientAttrs, 0)
        eglConfig = configs[0]

        val values = IntArray(1)
        EGL14.eglQueryContext(eglDisplay, eglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0)

        val surfaceAttrs = intArrayOf(EGL14.EGL_NONE)
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, inputSurface, surfaceAttrs, 0)
        check(eglSurface != null) { "Surface was null" }

        check(EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) { "eglMakeCurrent failed" }
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

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
                GLES20.glDeleteProgram(drawProgram)
                drawProgram = 0
            }
        }
        isPrepared = true
    }

    /**
     * Отриисовать кадр изображения, который доступен для [SurfaceTexture].
     */
    fun draw(data: TextureData) {
        with(data) {
            if (!isPrepared) return
            if (lastTimestampNanos == 0L) {
                lastTimestampNanos = surfaceTimestampNanos
                currentTimestampNanos = 0L
            }
            val dt = surfaceTimestampNanos - lastTimestampNanos
            currentTimestampNanos += dt
            lastTimestampNanos = surfaceTimestampNanos

            GLES20.glUseProgram(drawProgram)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)
            GLES20.glEnableVertexAttribArray(textureHandle)

            GLES20.glUniformMatrix4fv(vertexMatrixHandle, 1, false, mVPMatrix, 0)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, sTMatrix, 0)

            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexture)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(textureHandle)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexture)
            GLES20.glUseProgram(0)

            EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, currentTimestampNanos)
            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
        }
    }

    /**
     * Очистить декодер.
     */
    fun clear() {
        lastTimestampNanos = 0L
        currentTimestampNanos = 0L
        if (drawProgram > 0) {
            GLES20.glDeleteProgram(drawProgram)
            drawProgram = 0
        }

        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
            eglSurface = EGL14.EGL_NO_SURFACE
        }
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroyContext(eglDisplay, eglContext)
            eglContext = EGL14.EGL_NO_CONTEXT

            EGL14.eglReleaseThread()
            EGL14.eglTerminate(eglDisplay)
            eglDisplay = EGL14.EGL_NO_DISPLAY
        }
        eglConfig = null
        
        isPrepared = false
    }
}

private const val EGL_RECORDABLE_ANDROID = 0x3142