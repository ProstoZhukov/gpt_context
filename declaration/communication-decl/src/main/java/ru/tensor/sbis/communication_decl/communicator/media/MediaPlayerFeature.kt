package ru.tensor.sbis.communication_decl.communicator.media

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича создания плеера для проигрывания аудио и видео сообщений.
 *
 * @author da.zhukov
 */
interface MediaPlayerFeature : Feature {

    /**
     * Получить [MediaPlayer].
     */
    fun getMediaPlayer(): MediaPlayer

    /**
     * Получить [ProximityHelper].
     */
    fun getProximityHelper(): ProximityHelper

    /**
     * Получить вспомогательную реализацию для настройки сессии проигрывания.
     */
    fun getMediaPlayerSessionHelper(): MediaPlayerSessionHelper
}