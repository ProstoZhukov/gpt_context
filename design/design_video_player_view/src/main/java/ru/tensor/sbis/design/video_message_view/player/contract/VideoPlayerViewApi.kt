package ru.tensor.sbis.design.video_message_view.player.contract

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.video_message_view.player.children.StateListener
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewData

/**
 * API компонента проигрывания круглых видео.
 *
 * @author vv.chekurda
 */
interface VideoPlayerViewApi {

    /**
     * Установить данные для проигрывания.
     * @see VideoPlayerViewData
     */
    var data: VideoPlayerViewData?

    /**
     * Установка проигрывателя для видео
     */
    fun setMediaPlayer(mediaPlayer: MediaPlayer)

    /**
     * Установка слушателя изменения состояния проигрывания
     */
    fun setStateListener(listener: StateListener)

    /**
     * Показать превью видеосообщения.
     */
    fun showPreview(show: Boolean)

    /**
     * Показать загрузку видеосообщения.
     */
    fun showBuffering(show: Boolean)

    /**
     * Изменить видимость контролов для управления проигрыванием.
     */
    fun changeControlVisibility(isVisible: Boolean)

    /**
     * Подготовить первый кадр.
     */
    fun prepareFirstFrame()

    /**
     * Очистить состояние.
     */
    fun clearState()
}