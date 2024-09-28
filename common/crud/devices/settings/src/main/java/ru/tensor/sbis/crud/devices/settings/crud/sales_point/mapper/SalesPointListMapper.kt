package ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper

import android.content.Context
import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.R
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.crud.devices.settings.model.map

/**
 * Маппер для преобразования нескольких точек продаж, пришедших с контроллера, в Android модели
 */
internal class SalesPointListMapper(context: Context) :
        BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>>(context) {

    override fun apply(rawList: ListResultOfSalesPointMapOfStringString): PagedListResult<BaseItem<SalesPoint>> =
            PagedListResult(rawList.result.map {
                if (it.isFolder) BaseItem(data = it.map(), type = R.id.settings_sales_point_folder_id)
                else BaseItem(data = it.map(), type = R.id.settings_sales_point_item_id)
            }, rawList.haveMore)
}