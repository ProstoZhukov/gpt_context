package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.crud.sbis.pricing.model.map
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel

internal class AvailablePriceListModelMapper(context: Context) :
        BaseModelMapper<AvailablePricelistModel, AvailablePriceList>(context) {

    override fun apply(rawData: AvailablePricelistModel): AvailablePriceList = rawData.map()
}