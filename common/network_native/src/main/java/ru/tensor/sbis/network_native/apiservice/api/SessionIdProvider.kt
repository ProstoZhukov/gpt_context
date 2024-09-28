package ru.tensor.sbis.network_native.apiservice.api

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс провайдера идентификатора сессии
 */
interface SessionIdProvider : Feature {

    /**
     * Предоставить идентификатор сессии
     */
    fun getTokenId(): String?
}