package ru.tensor.sbis.red_button.repository.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.red_button.data.RedButtonStubType

/**
 * Маппер целочисленного значения [Int] в [RedButtonStubType]
 *
 * @author ra.stepanov
 */
class RedButtonStubMapper : Function<Int, RedButtonStubType> {

    override fun apply(value: Int): RedButtonStubType {
        return when (value) {
            1 -> RedButtonStubType.CLOSE_STUB
            0 -> RedButtonStubType.OPEN_STUB
            else -> RedButtonStubType.NO_STUB
        }
    }

}