package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.crud.payment_settings.model.PaymentSettings
import ru.tensor.sbis.crud.payment_settings.model.CrudSubscriptionWrapper

/**
 * Интерфейс для получения настроек продажи
 */
interface RetailSaleSettingsCommandWrapper : RetailSettingsCommandWrapper {
    /**
     * Возвращает [Single], который получает информацию о максимально возможных значениях в продаже.
     * Пытается получить информацию из сети.
     * Подписка на [Schedulers.io]. Наблюдение на [AndroidSchedulers.mainThread].
     */
    fun listMaxValuesForSale(): Single<PaymentSettings>

    /**
     * Возвращает [Single], который подписывается на события обновления информации о максимально
     * возможных значения в продаже и возвращает подписку. Подписка на [Schedulers.io].
     * Наблюдение на [AndroidSchedulers.mainThread].
     * @param action действие при получении оповещения об обновлении информации
     */
    fun setSettingsRefreshCallback(action: () -> Unit): Single<CrudSubscriptionWrapper>

    /**
     * Возвращает [Single], который получает информацию о настройках оплаты (только на основе локальных данных).
     */
    fun getPaymentSettings(): Single<PaymentSettings>
}