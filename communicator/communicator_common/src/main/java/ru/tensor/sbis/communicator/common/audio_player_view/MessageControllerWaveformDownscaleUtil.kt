package ru.tensor.sbis.communicator.common.audio_player_view

import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil

/**
 * Реализация утилиты для сжатия осциллограм аудиофайлов.
 *
 * @author rv.krohalev
 */
class MessageControllerWaveformDownscaleUtil : WaveformDownscaleUtil {

    /** @SelfDocumented */
    override fun downscaleWaveform(waveform: ByteArray, outCount: Int): ByteArray =
        checkExecutionTime("MessageController.downsampleOscillogram") {
            MessageController.instance().downsampleOscillogram(waveform, outCount)
        }
}