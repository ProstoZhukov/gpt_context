package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.red_button.data.RedButtonActions

/**
 * Маппер приходящего действия из контроллера [Byte] в обертку [RedButtonActions]
 *
 * @author ra.stepanov
 */
class RedButtonActionsMapper : Function<Byte, RedButtonActions> {

    override fun apply(item: Byte): RedButtonActions {
        return if (item.toInt() == 0) RedButtonActions.HIDE_MANAGEMENT else RedButtonActions.EMPTY_CABINET
    }
}