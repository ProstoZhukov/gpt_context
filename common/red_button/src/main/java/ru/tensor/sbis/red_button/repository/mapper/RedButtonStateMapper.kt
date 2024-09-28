package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.red_button.BuildConfig
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button_service.generated.RedButtonTypeState
import timber.log.Timber

/**
 * Маппер объекта контроллера [RedButtonTypeState] в обертку [RedButtonState]
 *
 * @author ra.stepanov
 */
class RedButtonStateMapper : Function<RedButtonTypeState, RedButtonState> {

    override fun apply(item: RedButtonTypeState): RedButtonState {
        return when (item) {
            RedButtonTypeState.ACCESS_DENIED -> RedButtonState.ACCESS_DENIED
            RedButtonTypeState.ACCESS_LOCK -> RedButtonState.ACCESS_LOCK
            RedButtonTypeState.CLICK -> RedButtonState.CLICK
            RedButtonTypeState.NOT_CLICK -> RedButtonState.NOT_CLICK
            RedButtonTypeState.CLOSES -> RedButtonState.CLOSE_IN_PROGRESS
            RedButtonTypeState.OPENS -> RedButtonState.OPEN_IN_PROGRESS
            else -> {
                val message = "Некорректное состояние красной кнопки!"
                if (BuildConfig.DEBUG) throw IllegalStateException(message) else Timber.e(
                    IllegalStateException(),
                    message
                )
                RedButtonState.ACCESS_DENIED
            }
        }
    }
}