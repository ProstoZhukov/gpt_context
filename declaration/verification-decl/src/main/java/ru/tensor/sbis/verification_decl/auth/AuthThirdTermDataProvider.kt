package ru.tensor.sbis.verification_decl.auth

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер данных для дополнительного пользовательского соглашения.
 *
 * @author ga.malinskiy
 */
interface AuthThirdTermDataProvider : Feature {

    /**
     * Получение названия раздела.
     */
    fun getThirdTermTitle(): String?

    /**
     * Получение ссылки на документ.
     */
    fun getThirdTermUrl(): String?
}