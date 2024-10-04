package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract

/**
 * Состояния view панели записи и отправки аудиосообщения.
 *
 * @author vv.chekurda
 */
enum class AudioRecordSendViewState {

    /**
     * Запись + выбор смайлов для отправки.
     */
    RECORDER,

    /**
     * Проигрывание записи + выбор смайлов для отправки.
     */
    PLAYER
}