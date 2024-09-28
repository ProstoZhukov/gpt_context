package ru.tensor.sbis.communicator.common.conversation.data

/** Результат создания задачи **/
enum class TaskCreationResult {
    /**
     * Успешно.
     */
    SUCCESS,

    /**
     * Отменено.
     */
    CANCELLED,

    /**
     * Что-то пошло не так.
     */
    ERROR,
}
