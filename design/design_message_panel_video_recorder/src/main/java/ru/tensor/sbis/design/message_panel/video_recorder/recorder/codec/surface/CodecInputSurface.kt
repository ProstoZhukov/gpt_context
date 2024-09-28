package ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface

import android.opengl.EGLContext
import android.view.Surface
import androidx.annotation.WorkerThread
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DispatchQueue
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.gl.EncodeGLDrawer

/**
 * OpenGL обертка над [Surface] для кодека записи видео.
 *
 * @author vv.chekurda
 */
internal class CodecInputSurface(
    private val surface: Surface,
    private val dispatcher: DispatchQueue
) : CodecSurfaceDrawer {

    private var encodeGLDrawer = EncodeGLDrawer()

    var surfaceChangeListener: CodecSurfaceChangeListener? = null

    @Volatile
    var isEnabled: Boolean = false

    override fun init(sharedContext: EGLContext) {
        dispatcher.post {
            encodeGLDrawer.init(surface, sharedContext)
        }
    }

    override fun draw(data: TextureData) {
        if (!isEnabled) return
        dispatcher.post {
            surfaceChangeListener?.onSurfaceChanged(data.surfaceTimestampNanos)
            encodeGLDrawer.draw(data)
        }
    }

    /**
     * Высвобождение ресурсов.
     */
    fun release() {
        isEnabled = false
        surfaceChangeListener = null
        encodeGLDrawer.clear()
        surface.release()
    }
}

internal fun interface CodecSurfaceChangeListener {

    @WorkerThread
    fun onSurfaceChanged(surfaceTimestampNanos: Long)
}