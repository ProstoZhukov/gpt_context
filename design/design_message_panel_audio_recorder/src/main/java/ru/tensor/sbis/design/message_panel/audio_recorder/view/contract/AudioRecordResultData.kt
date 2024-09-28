package ru.tensor.sbis.design.message_panel.audio_recorder.view.contract

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import java.io.File

/**
 * Результат записи аудиосообщения.
 *
 * @param audioFile аудиофайл.
 * @param duration продолжительность записи в секундах.
 * @param waveform осциллограмма.
 * @param emotion эмоция сообщения.
 *
 * @author vv.chekurda
 */
data class AudioRecordResultData(
    val audioFile: File,
    val duration: Int,
    val waveform: ByteArray,
    val emotion: AudioMessageEmotion?,
) {
    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is AudioRecordResultData -> false
            else -> EqualsBuilder()
                .append(audioFile, other.audioFile)
                .append(duration, other.duration)
                .append(emotion, other.emotion)
                .build()
        }

    override fun hashCode(): Int =
        HashCodeBuilder()
            .append(audioFile)
            .append(duration)
            .append(emotion)
            .build()
}

/**
 * Состояние панели записи аудиосообщения.
 *
 * @property isRecording признак процесса записи аудиосообщения.
 * @property isLockedViewState признак записи через закрепленное состоянии без возможности управления кнопкой.
 * @property isSendPreparing признак процесса подготовки к отправке.
 * @property requireSendOnStop признак необходимости отправки сообщения сразу после завершения записи.
 * @property pickedEmotion выбранная эмоция для отправки сообщения.
 *
 * @author vv.chekurda
 */
data class AudioRecordViewState(
    val isRecording: Boolean = false,
    val isLockedViewState: Boolean = false,
    val isSendPreparing: Boolean = false,
    val requireSendOnStop: Boolean = false,
    val pickedEmotion: AudioMessageEmotion? = null
) {
    /**
     * Признак видимости панели записи.
     */
    val isVisible: Boolean
        get() = isRecording || isSendPreparing
}