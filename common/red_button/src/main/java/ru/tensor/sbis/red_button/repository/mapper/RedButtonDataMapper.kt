package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.red_button.data.RedButtonData
import ru.tensor.sbis.red_button_service.generated.RedButtonOnData

/**
 * Маппер объекта контроллера [RedButtonOnData] в обертку [RedButtonData]
 *
 * @author ra.stepanov
 */
class RedButtonDataMapper : Function<RedButtonOnData, RedButtonData> {

    override fun apply(t: RedButtonOnData) = RedButtonData().apply {
        this.operationUuid = t.uuid
        this.phone = t.hidePhoneNumber
        this.pin = t.pin
    }
}