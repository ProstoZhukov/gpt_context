package ru.tensor.sbis.design.theme.models

import android.content.Context
import androidx.annotation.Dimension

/**
 * Абстрактная модель линейки размеров компонента из глобальных переменных.
 *
 * @author ra.geraskin
 */
interface AbstractHeight : AbstractHeightModel {

    /**
     * @see Context.getDimen
     */
    @Dimension
    fun getDimen(context: Context): Float

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    fun getDimenPx(context: Context): Int

}