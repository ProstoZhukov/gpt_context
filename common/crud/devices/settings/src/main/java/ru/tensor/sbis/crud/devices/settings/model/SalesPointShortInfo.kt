package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tensor.devices.settings.generated.SalesPointShortInfo as ControllerSalesPointShortInfo

/**
 * Модель c краткой информацией о точке продаж
 *
 * @param identifier String - Идентификатор точки продаж
 * @param name String - Наименование точки продаж
 */
@Parcelize
data class SalesPointShortInfo(
    val identifier: String,
    val name: String
) : Parcelable {

    companion object {
        fun stub(): SalesPointShortInfo = SalesPointShortInfo("", "")
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSalesPointShortInfo.map(): SalesPointShortInfo = SalesPointShortInfo(identifier, name)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SalesPointShortInfo.map(): ControllerSalesPointShortInfo = ControllerSalesPointShortInfo(identifier, name)