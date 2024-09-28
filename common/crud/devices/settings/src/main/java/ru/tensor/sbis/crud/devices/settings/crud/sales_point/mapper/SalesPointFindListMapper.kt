package ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper

import android.content.Context
import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.R
import ru.tensor.sbis.crud.devices.settings.model.BreadCrumb
import ru.tensor.sbis.crud.devices.settings.model.SalesItem
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.devices.settings.generated.SalesPoint as ControllerSalesPoint

/**
 * Маппер для преобразования нескольких точек продаж, пришедших с контроллера, в Android модели.
 * Работает с хлебными крошками - например, в режим поиска по точкам продаж
 */
internal class SalesPointFindListMapper(context: Context) :
        BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>>(context) {

    override fun apply(rawList: ListResultOfSalesPointMapOfStringString): PagedListResult<BaseItem<SalesItem>> {

        val result = PagedListResult(mutableListOf<BaseItem<SalesItem>>(), rawList.haveMore)
        val dataList = result.dataList

        val points = rawList.result.filter { !it.isFolder }.partition { !it.folder.isNullOrEmpty() }
        val nonRootPoints = points.first.sortedBy { it.folder }
        val rootPoints = points.second
        val folders = rawList.result.filter { it.isFolder }.toMutableList()

        nonRootPoints.forEach { point ->
            if (dataList.isEmpty() || (dataList.last().data as? SalesPoint)?.parentFolderId != point.folder) {
                folders.find { it.identifier == point.folder }?.let { folder ->
                    val breadCrumb = BreadCrumb(name = folder.name, id = folder.identifier, crumbs = point.createBreadCrumbs(folders))
                    dataList.add(BaseItem(data = breadCrumb, type = R.id.settings_sales_point_bread_crumb_id))
                }
            }
            dataList.add(BaseItem(data = point.map() as SalesItem, type = R.id.settings_sales_point_item_id))
        }

        rootPoints.forEach { point ->
            dataList.add(BaseItem(data = point.map() as SalesItem, type = R.id.settings_sales_point_item_id))
        }
        return result
    }

    private fun ControllerSalesPoint.createBreadCrumbs(folders: List<ControllerSalesPoint>): List<String> {
        val crumbs = mutableListOf<String>()
        var folder = folders.find { it.identifier == this.folder }
        crumbs.add(folder?.name ?: "")

        while (folder?.folder != null) {
            folder = folders.find { it.identifier == folder?.folder }
            crumbs.add(folder?.name ?: "")
        }
        return crumbs
    }
}