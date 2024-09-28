package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.red_button.BuildConfig
import ru.tensor.sbis.red_button.data.RedButtonState
import timber.log.Timber

/**
 * Маппер целочисленного значения красной кнопки [Int] в обертку [RedButtonState]
 *
 * @author ra.stepanov
 */
class RedButtonIntToStateMapper : Function<Int, RedButtonState> {

    override fun apply(item: Int): RedButtonState {
        return when (item) {
            0 -> RedButtonState.ACCESS_DENIED
            1 -> RedButtonState.ACCESS_LOCK
            2 -> RedButtonState.CLICK
            3 -> RedButtonState.NOT_CLICK
            4 -> RedButtonState.CLOSE_IN_PROGRESS
            5 -> RedButtonState.OPEN_IN_PROGRESS
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