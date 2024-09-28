package ru.tensor.sbis.crud.devices.settings.crud.device_type.mapper

import android.content.Context
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.R
import ru.tensor.sbis.crud.devices.settings.exception.UnsupportedConnectionTypeException
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.crud.devices.settings.model.map
import timber.log.Timber
import ru.tensor.devices.settings.generated.DeviceType as ControllerDeviceType

/**
 * Маппер для преобразования списка подключаемого оборудования, пришедшего с контроллера, в список Android моделей
 */
internal class DeviceTypeListMapper(context: Context) :
        BaseModelMapper<ListResultOfDeviceTypeMapOfStringString, PagedListResult<BaseItem<DeviceType>>>(context) {

    override fun apply(rawList: ListResultOfDeviceTypeMapOfStringString): PagedListResult<BaseItem<DeviceType>> =
            PagedListResult(getList(rawList), rawList.haveMore)

    private fun getList(rawList: ListResultOfDeviceTypeMapOfStringString): List<BaseItem<DeviceType>> =
            rawList.result.mapNotNull {
                try {
                    getCatalogItem(it)
                } catch (e: UnsupportedConnectionTypeException) {
                    Timber.w(e)
                    null
                }
            }

    private fun getCatalogItem(item: ControllerDeviceType): BaseItem<DeviceType> =
            if (item.isFolder) BaseItem(R.id.settings_device_type_folder_id, data = item.map())
            else BaseItem(R.id.settings_device_type_item_id, data = item.map())
}