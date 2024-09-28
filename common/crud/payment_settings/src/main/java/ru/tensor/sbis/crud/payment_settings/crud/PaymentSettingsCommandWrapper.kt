package ru.tensor.sbis.crud.payment_settings.crud

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.sbis.crud.payment_settings.model.NomenclatureSelect
import ru.tensor.sbis.crud.payment_settings.model.PaymentSettings
import ru.tensor.sbis.crud.payment_settings.model.map
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.platform.generated.Subscription
import javax.inject.Inject

/** @SelfDocumented */
class PaymentSettingsCommandWrapper @Inject constructor(private val repository: PaymentSettingsRepository) :
    BaseInteractor() {

    /**
     * Подписка на изменение настроек
     *
     * @param callback на обновление точек продаж
     * @return подписка [Observable]
     */
    fun subscribeDataRefreshedEvent(callback: DataRefreshedSalesPointFacadeCallback): Observable<Subscription> =
        Observable.fromCallable { repository.subscribeDataRefreshedEvent(callback) }

    /**
     * Получение настроек оплаты
     *
     * @return [Single]
     */
    fun getCurrentPaymentSettings(): Single<PaymentSettings> {
        return Single.fromCallable { repository.getCurrentPaymentSettings().map() }
            .compose(getSingleBackgroundSchedulers())
    }

    /** Получение настроек оплаты. */
    suspend fun currentPaymentSettings(): PaymentSettings = withContext(Dispatchers.IO) {
        repository.getCurrentPaymentSettings().map()
    }

    /**
     * Получение индивидуальной настройки обнуления наличных при закрытии смены
     */
    fun getShiftClosingWithdraw(): Single<Boolean> {
        return Single.fromCallable { repository.getCurrentPaymentSettings().saleSettings.shiftClosingWithdraw }
            .compose(getSingleBackgroundSchedulers())
    }

    /**
     * Изменение настроек оплаты
     *
     * @param currentPaymentSettings изменённые настройки оплаты
     * @return [Completable]
     */
    fun setCurrentPaymentSettings(currentPaymentSettings: PaymentSettings): Completable {
        return Completable.fromCallable { repository.setCurrentPaymentSettings(currentPaymentSettings.map()) }
            .compose(completableBackgroundSchedulers)
    }

    /**
     * Получение настройки откуда разрешено выбирать номенклатуры.
     *
     * @return [Single]
     */
    fun getNomenclatureSelectSetting(): Single<NomenclatureSelect> =
        Single.fromCallable { repository.getCurrentPaymentSettings().saleSettings.nomenclatureSelect.map() }
            .compose(getSingleBackgroundSchedulers())
}