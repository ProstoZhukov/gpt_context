package ru.tensor.sbis.communication_decl.communicator.media

import android.view.TextureView
import androidx.annotation.MainThread
import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.UriResolver

/**
 * Плеер для проигрывания аудио и видео сообщений.
 *
 * @author da.zhukov
 */
interface MediaPlayer : MediaPlayerController {

    /**
     * Признак доступности плеера.
     * В случае установки недоступности выключается текущий проигрываемый контент
     * и заблокируются попытки нового воспроизведения.
     */
    var isEnabled: Boolean

    /**
     * Признак готовности плеера к воспроизведению.
     */
    var isPreparing: Boolean

    /**
     * Слушатель изменения динамика для проигрывания.
     */
    var audioRouteChangeListener: AudioRouteChangeListener?

    /**
     * Подготовить медиа источник к проигрыванию. Проигрывание начнется в зависимости от [playWhenReady].
     */
    @MainThread
    fun setMediaInfo(mediaInfo: MediaInfo, playWhenReady: Boolean = true)

    /**
     * Получить данные о текущем источнике проигрывания и состоянии проигрывания.
     */
    fun getMediaInfo(): MediaInfo?

    /**
     * Подписка на состояние проигрывания
     */
    fun playingState(): Observable<MediaInfo>?

    /**
     * Идёт воспроизведение в данный момент или нет.
     */
    fun isPlayingActive(): Boolean

    /**
     * Переключить динамик для проигрывания.
     */
    fun changeAudioRoute(useFrontSpeaker: Boolean)

    /**
     * См. [UriResolver].
     */
    fun setUriResolver(resolver: UriResolver?)

    /**
     * Установить текстуру для отображения видео.
     */
    fun setVideoTextureView(texture: TextureView?)

    /**
     * Установить слушатель изменения состояния проигрывания.
     */
    fun setPlayingListener(listener: PlayingStateListener?)

    /**
     * Проверить доступность плеера для начала проигрывания.
     * В случае налачия проблемы вернет false и ошибку в колбэк.
     */
    fun checkPlayingAvailability(mediaInfo: MediaInfo? = null): Boolean

    /**
     * Высвободить все задействованные ресурсы.
     */
    fun release()

    /**
     * Слушатель изменения динамика для проигрывания.
     */
    interface AudioRouteChangeListener {
        /**
         * Изменился динамик для проигрывания.
         */
        fun onAudioRouteChanged(frontSpeaker: Boolean)
    }

    /**
     * Слушатель изменения состояния проигрывания.
     */
    interface PlayingStateListener {
        /**
         * Состояние проигрывая изменилось.
         */
        fun playingStateChanged(isActive: Boolean)
    }
}