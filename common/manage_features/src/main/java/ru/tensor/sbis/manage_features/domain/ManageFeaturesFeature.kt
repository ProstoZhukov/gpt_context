package ru.tensor.sbis.manage_features.domain

import androidx.fragment.app.Fragment
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesFragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича модуля проверки функционала
 */
interface ManageFeaturesFeature : Feature {

    /**
     * Получить основной фрагмент модуля
     * @return основной фрагмент модуля [ManageFeaturesFragment]
     */
    fun getManageFeatureFragment(): Fragment

    /**
     * Проверить доступна ли пользователю проверка доступности функционала по переданным идентификаторам пользователя и клиента
     * По умолчанию используется идентификатор функционала test3xfeat
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (компании)
     * @return true если проверка доступна, иначе false
     */
    fun isManageFeaturesEnabled(userID: Int, clientID: Int): Boolean

    /**
     * Проверить доступен ли пользователю функционал с переданным идентификатором
     * @param featureName идентификатор функционала
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (компании)
     * @return true если функционал доступен, иначе false
     */
    fun isManageFeaturesEnabled(featureName: String, userID: Int, clientID: Int): Boolean

    /**
     * Проверить доступен ли функционал с переданным идентификатором (без привязки к пользователю).
     * @param featureName идентификатор функционала.
     * @return true, если функционал доступен, иначе false.
     */
    fun isManageFeaturesEnabled(featureName: String): Boolean

    /**
     * Получение значения идентификатора функционала.
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке).
     */
    fun getManageFeature(featureName: String): String
}