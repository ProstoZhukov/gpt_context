package ru.tensor.sbis.communicator.contacts_declaration.model.result

/**
 * Статус запроса контактов
 *
 * @author vv.chekurda
 */
enum class GetStatus {
    SUCCESS_LOCAL_CACHE,
    SUCCESS_CLOUD,
    DB_ERROR,
    CLOUD_ERROR
}