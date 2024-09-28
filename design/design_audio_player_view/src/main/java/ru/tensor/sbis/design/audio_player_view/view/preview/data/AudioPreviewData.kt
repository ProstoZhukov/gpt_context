package ru.tensor.sbis.design.audio_player_view.view.preview.data

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Модель с данными для отображения превью аудиосообщения.
 *
 * @property waveform осциллограмма.
 * @property durationSeconds время записи аудио.
 * @property recognizedText распознанное сообщение.
 *
 * @author dv.baranov
 */
data class AudioPreviewData(
    val waveform: ByteArray,
    val durationSeconds: Int,
    val recognizedText: String? = null
) {

    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is AudioPreviewData -> false
            else -> EqualsBuilder()
                .append(durationSeconds, other.durationSeconds)
                .append(recognizedText, other.recognizedText)
                .build()
        }

    override fun hashCode(): Int =
        HashCodeBuilder()
            .append(durationSeconds)
            .append(recognizedText)
            .build()
}