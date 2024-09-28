package ru.tensor.sbis.mobile_services_decl

/**
 * Результат подключения к мобильным сервисам
 *
 * @author am.boldinov
 */
enum class ServiceConnectionResult {
    SUCCESS,
    NETWORK_ERROR,
    INTERNAL_ERROR,
    SERVICE_VERSION_UPDATE_REQUIRED,
    SERVICE_DISABLED,
    SERVICE_MISSING
}