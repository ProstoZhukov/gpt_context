package ru.tensor.sbis.crud.devices.settings.crud.workplace

import ru.tensor.devices.settings.generated.WorkplaceFacade
import ru.tensor.sbis.common.data.DependencyProvider

class DeviceIdRepository(private val workplaceFacade: DependencyProvider<WorkplaceFacade>) {

    fun getDeviceId() = workplaceFacade.get().getCurrentDeviceId()
}
