package ru.tensor.sbis.feature_ctrl

import java.util.Date

/**
 * Базовый класс фичи
 *
 * @property feature - Идентификатор функционала.
 * @property client - Идентификатор клиента.
 * @property user - Идентификатор пользователя.
 * @property lastUpdate - Время, когда данные были записаны в эту структуру, используется для инвалидации кэша.
 * @property lastGenError - Время последней отправки сообщения об ошибке для функционала находящегося в архиве.
 * @property data - Значение функционала.
 * @property type - Тип действующей спецификации функционала.
 * @property typeV2 - Текущая спецификация для второй версии формата ответа
 * @property invalidation - Время инвалидации данных функционала, после истечения которого данные о функционале будут перечитаны из сервиса feature и актуализированы в кэше.
 * @property state - Состояние функционала включен/выключен.
 * @property needUpdate - Требуется ли обновить данные по спецификации из Облака.
 * @property sendEvent - Нужно ли отправлять событие.
 *
 * @author av.krymov
 */
data class SbisFeatureInfo(
    var feature: String,
    var client: Long? = null,
    var user: Long? = null,
    var lastUpdate: Date = Date(),
    var lastGenError: Date? = null,
    var data: String? = null,
    var type: String = "",
    var typeV2: String = "",
    var invalidation: Int? = null,
    var state: Boolean? = null,
    var needUpdate: Boolean? = null,
    var sendEvent: Boolean? = null
)