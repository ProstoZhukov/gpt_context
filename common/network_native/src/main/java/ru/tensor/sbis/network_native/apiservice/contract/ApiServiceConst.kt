package ru.tensor.sbis.network_native.apiservice.contract

/**
 * Класс с набором констант для работы с [ApiService]
 * (Константы вынесены из класса [ru.tensor.sbis.network_native.apiservice.api.ApiServiceImpl])
 *
 * @author da.pavlov1
 * */

/** Дефолтный таймаут (в секундах) */
internal const val TIMEOUT_SECS: Int = 10

/** Дефолтный таймаут для Fresco (в секундах) */
internal const val FRESCO_READ_TIMEOUT_SECS: Int = 60

const val SERVICE = "service/"

internal const val SERVER_CONNECT_ERROR = "Ошибка соединения с сервером"
internal const val NETWORK_ERROR = "Не удалось загрузить данные. Проверьте подключение к интернету."
internal const val UNKNOWN_ERROR = "Неизвестная ошибка"