package ru.tensor.sbis.retail_decl

import ru.tensor.sbis.plugin_struct.feature.Feature

/** Проверка состояний фиче тоглов и локально установленных настроек. */
interface RetailFeatureToggleManager : Feature {

    /** 
     *  Проверка состояния фичи "Режим ресторана".
     *  Если false - режим столовой, true - режим ресторана. 
     */
    fun isRestaurantModeEnabled(): Boolean
    /** Вкл\выкл "Режим ресторана" через экран "настройки" в debug. */
    fun setRestaurantModeEnabledLocal(enabled: Boolean)
    /** Проверка состояния фичи "Режим ресторана" установленной локально. */
    fun isRestaurantModeEnabledLocal(): Boolean

    /** Проверка состояния фичи "Новый экран продажи". */
    fun isNewSaleScreenEnabled(): Boolean
    /** Вкл\выкл "Новый экран продажи" через экран "настройки" в debug. */
    fun setNewSaleScreenEnabledLocal(enabled: Boolean)
    /** Проверка состояния фичи "Новый экран продажи" установленной локально. */
    fun isNewSaleScreenEnabledLocal(): Boolean
}