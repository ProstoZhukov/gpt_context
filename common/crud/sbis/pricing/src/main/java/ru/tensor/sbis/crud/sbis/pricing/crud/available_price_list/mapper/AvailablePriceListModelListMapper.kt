package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.mapper

import android.content.Context
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.crud.sbis.pricing.model.map
import ru.tensor.sbis.pricing.generated.ListResultOfAvailablePricelistModelMapOfStringString

internal class AvailablePriceListModelListMapper(context: Context) :
        BaseModelMapper<ListResultOfAvailablePricelistModelMapOfStringString, PagedListResult<AvailablePriceList>>(context) {

    override fun apply(rawList: ListResultOfAvailablePricelistModelMapOfStringString): PagedListResult<AvailablePriceList> =
            PagedListResult(rawList.result.map { it.map() }, rawList.haveMore)
}