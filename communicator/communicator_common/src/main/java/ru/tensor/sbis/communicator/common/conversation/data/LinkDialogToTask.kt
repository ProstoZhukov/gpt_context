package ru.tensor.sbis.communicator.common.conversation.data

/**
 * Опции прикрепления диалога к задаче
 */
enum class LinkDialogToTask {
    /**
     * Не показывать диалог и связать задачу с перепиской
     */
    LINK,

    /**
     * Не показывать диалог и добавить задачу к переписке
     */
    APPEND,

    /**
     * Показать диалог с двумя действиями, описанными в [LINK] и [APPEND].
     */
    ASK
}
