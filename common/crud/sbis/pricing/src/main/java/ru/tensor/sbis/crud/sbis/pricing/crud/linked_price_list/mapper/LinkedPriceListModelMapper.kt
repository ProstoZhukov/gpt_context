package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.crud.sbis.pricing.model.map
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel

internal class LinkedPriceListModelMapper(context: Context) :
        BaseModelMapper<LinkedPricelistModel, LinkedPriceList>(context) {

    override fun apply(rawData: LinkedPricelistModel): LinkedPriceList = rawData.map()
}