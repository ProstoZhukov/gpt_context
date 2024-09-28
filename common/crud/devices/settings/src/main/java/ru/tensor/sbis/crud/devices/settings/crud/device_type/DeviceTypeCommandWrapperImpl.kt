package ru.tensor.sbis.crud.devices.settings.crud.device_type

import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.crud.devices.settings.crud.device_type.command.CatalogDeviceTypeListCommand
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand

/** @see DeviceTypeCommandWrapper */
internal class DeviceTypeCommandWrapperImpl(override val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback>) :
        DeviceTypeCommandWrapper,
        BaseInteractor() {

    override fun getParentFolderItem(): Pair<String, String>? = (listCommand as CatalogDeviceTypeListCommand).parentFolderItem
}
