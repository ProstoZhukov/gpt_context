package ru.tensor.sbis.manage_features.data

import ru.tensor.sbis.common.util.runSafely
import ru.tensor.sbis.feature_service.generated.Feature
import javax.inject.Inject

/**
 * Класс-источник данных, содержит в себе логику взаимодейсвия с контроллером
 */
internal class DataSource @Inject constructor() {

    /**
     * Проверка доступен ли функционал с переданным названием
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке)
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (группы пользователей или же компании, у которой стоит CRM)
     */
    fun isManageFeaturesEnabled(featureName: String, userID: Int, clientID: Int): Boolean =
        runSafely("Cannot check feature availability of $featureName for userId $userID, clientId $clientID") {
            Feature.hasOn(featureName, clientID.toLong(), userID.toLong())
        } ?: false

    /**
     * Проверка доступен ли функционал с переданным названием.
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке).
     */
    fun isManageFeaturesEnabled(featureName: String): Boolean =
        runSafely("Cannot check feature availability of $featureName") {
            Feature.hasOn(featureName)
        } ?: false

    /**
     * Проверка доступен ли функционал с дефолтным названием для фичи [DEFAULT_FEATURE_NAME]
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (группы пользователей или же компании, у которой стоит CRM)
     */
    fun isManageFeaturesEnabled(userID: Int, clientID: Int) =
        isManageFeaturesEnabled(DEFAULT_FEATURE_NAME, userID, clientID)

    /**
     * Получение значения идентификатора функционала
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке)
     * @param userID идентификатор пользователя
     * @param clientID идентификатор клиента (группы пользователей или же компании, у которой стоит CRM)
     */
    fun getManageFeature(featureName: String, userID: Int, clientID: Int): String? =
        runSafely("Cannot get feature value of $featureName for userId $userID, clientId $clientID") {
            Feature.getValue(featureName, clientID.toLong(), userID.toLong())
        }

    /**
     * Получение значения идентификатора функционала.
     * @param featureName название проверяемого функционала (данное название захардкожено на облаке).
     */
    fun getManageFeature(featureName: String): String? =
        runSafely("Cannot get feature value of $featureName") {
            Feature.getValue(featureName)
        }

    companion object {

        /** Значение функционала по умолчанию */
        const val DEFAULT_FEATURE_NAME = "test3xfeat"
    }
}