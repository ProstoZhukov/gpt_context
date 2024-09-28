package ru.tensor.sbis.business_card_host_decl.ui

/**
 * Предоставление внешней функциональности модуля визиток для других модулей
 */
interface BusinessCardHostFeature : BusinessCardHostFragmentProvider {

    /**
     * Флаг доступности визитки
     */
    suspend fun isBusinessCardAvailable(): Boolean
}