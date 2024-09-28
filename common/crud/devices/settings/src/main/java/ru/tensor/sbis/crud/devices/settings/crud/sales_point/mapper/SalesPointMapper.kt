package ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.devices.settings.generated.SalesPoint as ControllerSalesPoint

/**
 * Маппер для преобразования одной точки продаж, пришедшей с контроллера, в Android модель
 */
internal class SalesPointMapper(context: Context) :
        BaseModelMapper<ControllerSalesPoint, SalesPoint>(context) {

    override fun apply(rawData: ControllerSalesPoint): SalesPoint =
            rawData.map()
}
