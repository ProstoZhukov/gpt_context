package ru.tensor.sbis.communication_decl.communicator.media

/**
 * Обработчик событий связанных с медиа сообщениями.
 *
 * @author da.zhukov
 */
interface MediaMessage {

    /**
     * Установить [MediaPlayer].
     */
    fun setMediaPlayer(mediaPlayer: MediaPlayer)

    /**
     * Слушатель действий над медиа сообщениями.
     */
    interface ActionListener : MediaPlaybackListener {

        /**
         * Обработать нажатие на кнопку для раскрытия/сворачивания расшифровки.
         * @param expanded состояние развернутости.
         *
         * @return необходимость подскрола во время анимации.
         */
        fun onExpandClicked(expanded: Boolean): Boolean
    }
}