package ru.tensor.sbis.viper.informer

import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
interface GlobalInformerInterface : Feature {

    /**
     * Метод для отображения [SbisPopupNotification]
     */
    fun showGlobalInformer(data: InformerData)

    /**
     * Метод для отображения информера об отсутствии сети
     */
    fun showNetworkErrorInformer()

    /**
     * Метод для отображения информера о недоступности сервиса
     */
    fun showServiceUnavailableErrorInformer()
}