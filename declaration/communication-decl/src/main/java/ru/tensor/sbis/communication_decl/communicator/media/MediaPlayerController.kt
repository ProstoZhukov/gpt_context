package ru.tensor.sbis.communication_decl.communicator.media

import androidx.annotation.MainThread
import ru.tensor.sbis.communication_decl.communicator.media.data.PlaybackSpeed

/**
 * Контроллер для управления [MediaPlayer].
 *
 * @author da.zhukov
 */
interface MediaPlayerController {

    /**
     * Начать / восстановить проигрывание.
     */
    @MainThread
    fun play()

    /**
     * Приостановить проигрывание.
     */
    @MainThread
    fun pause()

    /**
     * Остановить проигрывание.
     */
    @MainThread
    fun stop()

    /**
     * Установить позицию для проигрывания в мс.
     */
    fun setPosition(position: Long)

    /**
     * Установить позицию для проигрывания в %.
     */
    fun seekToProgress(progress: Float, isSourceChanged: Boolean = false)

    /**
     * Изменить скорость воспроизведения.
     */
    fun setPlaybackSpeed(speed: PlaybackSpeed)
}