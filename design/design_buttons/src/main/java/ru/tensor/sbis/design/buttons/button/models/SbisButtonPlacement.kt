package ru.tensor.sbis.design.buttons.button.models

import android.content.Context
import androidx.annotation.Dimension
import ru.tensor.sbis.design.buttons.SbisButtonGroup
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Вариант размещения кнопки.
 *
 * @author ma.kolpakov
 */
internal enum class SbisButtonPlacement(
    val offset: Offset
) {

    /**
     * Кнопка является самостоятельной.
     */
    STANDALONE(Offset.L),

    /**
     * Кнопка вложена в группу [SbisButtonGroup].
     */
    NESTED(Offset.XS);

    /**
     * @see Offset.getDimen
     */
    @Dimension
    fun getDimen(context: Context) = offset.getDimen(context)
}