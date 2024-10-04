package ru.tensor.sbis.design.audio_player_view.view.player.data

import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.AudioSource

/**
 * Модель с данными для отображения и проигрывания аудиофайлов
 * в [ru.tensor.sbis.design.audio_player_view.view.player.AudioPlayerView].
 *
 * @property audioSource источник для получения аудиофайла.
 * @property durationSeconds продолжительность в секундах.
 * @property waveform осцилограмма сообщения. Ожидаемый размер [WAVEFORM_SIZE].
 *
 * @author vv.chekurda
 */
data class AudioPlayerViewData internal constructor(
    val audioSource: AudioSource,
    val durationSeconds: Int,
    val waveform: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioPlayerViewData

        if (audioSource != other.audioSource) return false
        if (durationSeconds != other.durationSeconds) return false
        if (!waveform.contentEquals(other.waveform)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audioSource.hashCode()
        result = 31 * result + durationSeconds
        result = 31 * result + waveform.contentHashCode()
        return result
    }
}