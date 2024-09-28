package ru.tensor.sbis.person_decl.status.model

/** Статус ошибки сервиса */
enum class ServiceAccessErrorStatus {
    /** Нет соединения */
    NO_CONNECTION,

    /** Другая ошибка */
    OTHER_ERROR,

    /** Сервис заблокирован */
    SERVICE_BLOCKED,
}