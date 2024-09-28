package ru.tensor.sbis.communication_decl.communicator.media

/**
 * Интерфейс для получения событий при воспроизведении аудиосообщений.
 *
 * @author da.zhukov
 */
interface MediaPlaybackListener {

    /**
     * Произошла ошибка воспроизведения
     */
    fun onMediaPlaybackError(error: Throwable)
}