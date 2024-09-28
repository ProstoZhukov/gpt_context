package ru.tensor.sbis.design.audio_player_view.view.player.children

/**
 * Делегат для обработки перемотки аудио.
 *
 * @author vv.chekurda
 */
interface SeekBarDelegate {

    /** Перемотка аудио. */
    fun onSeekBarDragged(progress: Float)

    /**
     * Пользователь в процессе перемотки.
     */
    fun onSeekBarDragging(progress: Float)
}