package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.mapper

import android.content.Context
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.crud.sbis.pricing.model.map
import ru.tensor.sbis.pricing.generated.ListResultOfLinkedPricelistModelMapOfStringString

internal class LinkedPriceListModelListMapper(context: Context) :
        BaseModelMapper<ListResultOfLinkedPricelistModelMapOfStringString, PagedListResult<LinkedPriceList>>(context) {

    override fun apply(rawList: ListResultOfLinkedPricelistModelMapOfStringString): PagedListResult<LinkedPriceList> =
            PagedListResult(rawList.result.map { it.map() }, rawList.haveMore)
}