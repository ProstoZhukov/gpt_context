package ru.tensor.sbis.manage_features.data

import androidx.fragment.app.Fragment
import ru.tensor.sbis.manage_features.domain.ManageFeaturesFeature
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesFragment

/**
 * Реализация фичей модуля, см. [ManageFeaturesFeature]
 */
class ManageFeaturesFeatureImpl : ManageFeaturesFeature {

    private val dataSource = DataSource()

    /**
     * Получить основной фрагмент модуля
     * @return основной фрагмент модуля [ManageFeaturesFragment]
     */
    override fun getManageFeatureFragment(): Fragment = ManageFeaturesFragment()

    /**
     * Проверить доступна ли пользователю проверка доступности функционала по переданным идентификаторам пользователя и клиента
     * По умолчанию используется идентификатор функционала test3xfeat
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (компании)
     * @return true если проверка доступна, иначе false
     */
    override fun isManageFeaturesEnabled(userID: Int, clientID: Int): Boolean =
        dataSource.isManageFeaturesEnabled(userID, clientID)

    /**
     * Проверить доступен ли пользователю функционал с переданным идентификатором
     * @param featureName идентификатор функционала
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (компании)
     * @return true если функционал доступен, иначе false
     */
    override fun isManageFeaturesEnabled(featureName: String, userID: Int, clientID: Int): Boolean =
        dataSource.isManageFeaturesEnabled(featureName, userID, clientID)

    /**
     * Проверить доступен ли функционал с переданным идентификатором (без привязки к пользователю).
     * @param featureName идентификатор функционала.
     * @return true, если функционал доступен, иначе false.
     */
    override fun isManageFeaturesEnabled(featureName: String): Boolean =
        dataSource.isManageFeaturesEnabled(featureName)

    /**
     * Получение значения идентификатора функционала.
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке).
     */
    override fun getManageFeature(featureName: String): String =
        dataSource.getManageFeature(featureName).orEmpty()
}