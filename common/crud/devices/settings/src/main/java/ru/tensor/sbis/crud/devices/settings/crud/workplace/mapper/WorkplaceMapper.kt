package ru.tensor.sbis.crud.devices.settings.crud.workplace.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.crud.devices.settings.model.map
import ru.tensor.devices.settings.generated.Workplace as ControllerWorkplace

/**
 * Маппер для преобразования одного рабочего места, пришедшего с контроллера, в Android модель
 */
internal class WorkplaceMapper(context: Context) :
        BaseModelMapper<ControllerWorkplace, Workplace>(context) {

    override fun apply(rawData: ControllerWorkplace): Workplace =
            rawData.map()
}
