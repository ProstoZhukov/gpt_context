package ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface

import android.opengl.EGLContext
import android.view.Surface

/**
 * Кодек для переноса [TextureData] на [Surface].
 *
 * @author vv.chekurda
 */
internal interface CodecSurfaceDrawer {

    /**@SelfDocumented*/
    fun init(sharedContext: EGLContext)

    /**@SelfDocumented*/
    fun draw(data: TextureData)
}