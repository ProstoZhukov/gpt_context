package ru.tensor.sbis.crud.payment_settings.crud

import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.SalesPointFacade
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.retail_settings.generated.PaymentSettings
import javax.inject.Inject

/** @SelfDocumented */
class PaymentSettingsRepository @Inject constructor(private val controller: DependencyProvider<SalesPointFacade>) {

    /**
     * Получение настроек оплаты
     *
     * @return [PaymentSettings]
     */
    fun getCurrentPaymentSettings(): PaymentSettings = controller.get().getCurrentPaymentSettings()

    /**
     * Подписка на изменение настроек
     *
     * @param callback на обновление точек продаж
     * @return [Subscription]
     */
    fun subscribeDataRefreshedEvent(callback: DataRefreshedSalesPointFacadeCallback): Subscription =
        controller.get().dataRefreshed().subscribe(callback)

    /**
     * Изменение настроек оплаты
     *
     * @param currentPaymentSettings изменённые настройки оплаты
     */
    fun setCurrentPaymentSettings(currentPaymentSettings: PaymentSettings) {
        controller.get().setCurrentPaymentSettings( currentPaymentSettings )
    }
}
