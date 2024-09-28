package ru.tensor.sbis.crud.devices.settings.crud.device_type

import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand

/**
 * Wrapper команд для контроллера для работы с настройками.
 */
interface DeviceTypeCommandWrapper {

    /**@SelfDocumented */
    val listCommand: BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback>

    /**
     * Функция для получения айди родителя и имя родителя
     *
     * @return пара айди родителя и имя родителя
     */
    fun getParentFolderItem(): Pair<String, String>?
}
