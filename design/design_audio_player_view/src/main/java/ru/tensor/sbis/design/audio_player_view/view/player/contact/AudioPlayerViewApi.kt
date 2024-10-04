package ru.tensor.sbis.design.audio_player_view.view.player.contact

import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlaybackListener
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewData
import ru.tensor.sbis.design.audio_player_view.view.player.AudioPlayerView

/**
 * API компонента проигрывания аудио файлов [AudioPlayerView].
 *
 * @author vv.chekurda
 */
interface AudioPlayerViewApi :
    MediaMessage {

    /**
     * Задать данные для проигрывания.
     * @see AudioPlayerViewData
     */
    var data: AudioPlayerViewData?

    /**
     * Вид отображения компонента.
     */
    var viewMode: AudioPlayerView.ViewMode

    /**
     * Задать слушателя событий воспроизведения.
     */
    fun setListener(listener: MediaPlaybackListener?)

    /**
     * Очистить состояние.
     */
    fun clearState()

    /**
     * Очистить состояние view для переиспользования.
     */
    fun recycle()
}