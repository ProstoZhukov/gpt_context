package ru.tensor.sbis.communication_decl.communicator.media.waveform

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс утилиты для получения сжатой версии осциллограммы для отображения в [CloudAudioMessageView].
 */
interface WaveformDownscaleUtil {

    /**
     * Получить сжатую версию осциллограммы.
     *
     * [outCount] количество до которого будет произведено сжатие. Должно быть меньше размера [waveform].
     */
    fun downscaleWaveform(waveform: ByteArray, outCount: Int): ByteArray

    /**
     * Поставщик [WaveformDownscaleUtil].
     */
    fun interface Provider : Feature {

        fun provideWaveformDownscaleUtil(): WaveformDownscaleUtil
    }
}