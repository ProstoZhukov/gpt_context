package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.crud.payment_settings.model.PaymentSettings
import ru.tensor.sbis.crud.payment_settings.model.map
import ru.tensor.devices.settings.generated.SalesPointSettings as ControllerSalesPointSettings

/**
 * Модель с настройками точки продаж
 *
 * @param salesPointId String - Идентификатор точки продаж
 * @param timeZoneName String - Название временной зоны
 * @param mdlpCode String - Код МДЛП (Мониторинг Движения Лекарственных Препаратов)
 * @param alcoholSaleSettings [AlcoholSaleSettings] - Настройки продажи алкоголя
 * @param paymentSettings [PaymentSettings] - Настройки оплаты
 * @param catalogSettings [CatalogSettings] - Настройки каталога
 */
@Parcelize
data class SalesPointSettings(
    val salesPointId: String,
    val timeZoneName: String?,
    val mdlpCode: String?,
    val alcoholSaleSettings: AlcoholSaleSettings,
    val paymentSettings: PaymentSettings,
    val catalogSettings: CatalogSettings,
    val utms: List<UtmSettings>,
    val alcoModes: List<AlcoMode>
) : Parcelable {
    companion object {
        fun stub(): SalesPointSettings = SalesPointSettings(
            "",
            null,
            null,
            AlcoholSaleSettings.stub(),
            PaymentSettings.stub(),
            CatalogSettings.stub(),
            emptyList(),
            emptyList()
        )
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSalesPointSettings.map(): SalesPointSettings = SalesPointSettings(
    identifier,
    timeZoneName,
    mdlpCode,
    alcoholSaleSettings.map(),
    paymentSettings.map(),
    catalogSettings.map(),
    utms.map { it.toAndroidType() },
    alcoModes.map { it.map() }
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SalesPointSettings.map(): ControllerSalesPointSettings = ControllerSalesPointSettings(
    salesPointId,
    timeZoneName,
    mdlpCode,
    alcoholSaleSettings.map(),
    paymentSettings.map(),
    catalogSettings.map(),
    ArrayList(utms.map { it.toControllerType() }),
    ArrayList(alcoModes.map { it.map() })
)