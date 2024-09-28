package ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface

import android.view.Surface
import java.nio.FloatBuffer

/**
 * Модель текстуры для записи изображения на [Surface].
 *
 * @author vv.chekurda
 */
internal data class TextureData(
    val cameraTexture: Int,
    val mVPMatrix: FloatArray,
    val sTMatrix: FloatArray,
    val vertexBuffer: FloatBuffer,
    val textureBuffer: FloatBuffer,
    val surfaceTimestampNanos: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextureData

        if (cameraTexture != other.cameraTexture) return false
        if (!mVPMatrix.contentEquals(other.mVPMatrix)) return false
        if (!sTMatrix.contentEquals(other.sTMatrix)) return false
        if (vertexBuffer != other.vertexBuffer) return false
        if (textureBuffer != other.textureBuffer) return false
        if (surfaceTimestampNanos != other.surfaceTimestampNanos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cameraTexture
        result = 31 * result + mVPMatrix.contentHashCode()
        result = 31 * result + sTMatrix.contentHashCode()
        result = 31 * result + vertexBuffer.hashCode()
        result = 31 * result + textureBuffer.hashCode()
        result = 31 * result + surfaceTimestampNanos.hashCode()
        return result
    }
}