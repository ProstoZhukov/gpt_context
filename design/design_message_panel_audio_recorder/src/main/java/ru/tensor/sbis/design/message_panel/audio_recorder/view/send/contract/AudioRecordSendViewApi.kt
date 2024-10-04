package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract

import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record.AudioRecordControlViewApi
import java.io.File

/**
 * API компонента подготовки отправки аудиосообщения с возможностью управлению записью.
 * @see AudioRecordSendView
 *
 * @author vv.chekurda
 */
internal interface AudioRecordSendViewApi : AudioRecordControlViewApi {

    /**
     * Обработчик событий копмпонента отправки аудиосообщения.
     */
    var eventsHandler: SendEventsHandler

    /**
     * Состояние компонента.
     */
    var viewState: AudioRecordSendViewState

    /**
     * Присоединить плеер.
     *
     * @param mediaPlayer плеер для проигрывания аудиофайлов.
     */
    fun attachPlayer(mediaPlayer: MediaPlayer)

    /**
     * Установить данные записанного аудиофайла.
     *
     * @param audioFile аудиофайл.
     * @param duration продолжительность записи в секундах.
     * @param waveform осциллограмма.
     */
    fun setAudioData(audioFile: File, duration: Int, waveform: ByteArray)

    /**
     * Очистить состояние.
     */
    fun clear()
}

/**
 * Обработчик событий [AudioRecordSendViewEvent].
 */
internal typealias SendEventsHandler = (AudioRecordSendViewEvent) -> Unit