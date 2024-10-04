/**
 * Шейдеры для записи видео посредством OpenGL.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.message_panel.video_recorder.recorder.gl

import android.opengl.GLES20
import timber.log.Timber

internal val VERTEX_SHADER: String by lazy {
    StringBuilder()
        .appendLine("uniform mat4 $U_MVP_MATRIX_KEY;")
        .appendLine("uniform mat4 $U_ST_MATRIX_KEY;")
        .appendLine("attribute vec4 $A_POSITION_KEY;")
        .appendLine("attribute vec4 $A_TEXTURE_COORD_KEY;")
        .appendLine("varying vec2 vTextureCoord;")
        .appendLine("void main() {")
        .appendLine("   gl_Position = $U_MVP_MATRIX_KEY * $A_POSITION_KEY;")
        .appendLine("   vTextureCoord = ($U_ST_MATRIX_KEY * $A_TEXTURE_COORD_KEY).xy;")
        .appendLine("}")
        .toString()
}

internal val FRAGMENT_SHADER: String by lazy {
    StringBuilder()
        .appendLine("#extension GL_OES_EGL_image_external : require")
        .appendLine("precision lowp float;")
        .appendLine("uniform samplerExternalOES sTexture;")
        .appendLine("varying vec2 vTextureCoord;")
        .appendLine("void main() {")
        .appendLine("   gl_FragColor = texture2D(sTexture, vTextureCoord);")
        .appendLine("}")
        .toString()
}

internal fun loadShader(type: Int, shaderCode: String): Int =
    GLES20.glCreateShader(type).run {
        GLES20.glShaderSource(this, shaderCode)
        GLES20.glCompileShader(this)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(this, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Timber.e("CameraGLDrawer: Failed to compile shader $shaderCode: ${GLES20.glGetShaderInfoLog(this)}")
            GLES20.glDeleteShader(this)
            0
        } else {
            this
        }
    }

internal const val A_POSITION_KEY = "aPosition"
internal const val A_TEXTURE_COORD_KEY = "aTextureCoord"
internal const val U_MVP_MATRIX_KEY = "uMVPMatrix"
internal const val U_ST_MATRIX_KEY = "uSTMatrix"
