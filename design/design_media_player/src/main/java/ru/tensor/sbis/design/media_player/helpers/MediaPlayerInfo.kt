package ru.tensor.sbis.design.media_player.helpers

import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo

/**
 * Вспомогательный класс, для обмена общими данными.
 *
 * @author da.zhukov
 */
internal class MediaPlayerInfo {

    /**@SelfDocumented*/
    var isPlayingActive = false

    /**@SelfDocumented*/
    var currentMediaInfo: MediaInfo? = null
        set(value) {
            field = value
            seekToProgressPending = 0f
            value?.waitingActualProgress = false
        }

    /**@SelfDocumented*/
    var seekToProgressPending: Float = 0f

    /**@SelfDocumented*/
    fun clear() {
        isPlayingActive = false
        currentMediaInfo = null
        seekToProgressPending = 0f
    }
}