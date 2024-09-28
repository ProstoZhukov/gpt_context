package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.crud.payment_settings.crud.PaymentSettingsCommandWrapper
import ru.tensor.sbis.crud.payment_settings.model.PaymentSettings
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.crud.payment_settings.model.CrudSubscriptionWrapper
import ru.tensor.sbis.retail_settings.generated.DataRefreshedCallback
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Класс для получения настроек продажи
 */
internal class RetailSaleSettingsCommandWrapperImpl @Inject constructor(
    private val retailSettingsCommandWrapper: RetailSettingsCommandWrapper,
    private val paymentSettingsCommandWrapper: PaymentSettingsCommandWrapper
) : RetailSaleSettingsCommandWrapper, RetailSettingsCommandWrapper by retailSettingsCommandWrapper {

    override fun listMaxValuesForSale(): Single<PaymentSettings> {
        return listCommand.list(SettingsFilter())
            .map { it.dataList.first().paymentSettings }
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun setSettingsRefreshCallback(action: () -> Unit): Single<CrudSubscriptionWrapper> {
        val weakAction = WeakReference(action)
        return listCommand
            .subscribeDataRefreshedEvent(object : DataRefreshedCallback() {
                override fun onEvent() {
                    weakAction.get()?.invoke()
                }
            })
            .map {
                it.map()
            }
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getPaymentSettings() = paymentSettingsCommandWrapper.getCurrentPaymentSettings()

    private fun Subscription.map() = CrudSubscriptionWrapper(this)
}
