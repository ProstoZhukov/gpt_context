package ru.tensor.sbis.design_dialogs.movablepanel

import android.os.Parcelable
import androidx.annotation.DimenRes
import androidx.annotation.FloatRange
import kotlinx.parcelize.Parcelize

/**
 * Модель для задания высоты панели
 *
 * @author ga.malinskiy
 */
sealed class MovablePanelPeekHeight : Parcelable {

    /**
     * Задается высота абсолютным значением из ресурсов
     */
    @Parcelize
    data class Dimen(@DimenRes val value: Int) : MovablePanelPeekHeight()

    /**
     * Задается высота, равная проценту от высоты всей вью
     */
    @Parcelize
    data class Percent(@FloatRange(from = 0.0, to = 1.0) val value: Float) : MovablePanelPeekHeight()

    /**
     * Задается высота, равная высоте контента
     */
    @Parcelize
    class FitToContent : MovablePanelPeekHeight()

    /**
     * Задается высота абсолютным значением
     */
    @Parcelize
    data class Absolute(val value: Int) : MovablePanelPeekHeight()
}

/**
 * Являются ли объекты одинаковыми
 */
fun MovablePanelPeekHeight?.isEqual(height: MovablePanelPeekHeight): Boolean = when {
    this is MovablePanelPeekHeight.Percent && height is MovablePanelPeekHeight.Percent -> value == height.value
    this is MovablePanelPeekHeight.Dimen && height is MovablePanelPeekHeight.Dimen -> value == height.value
    this is MovablePanelPeekHeight.Absolute && height is MovablePanelPeekHeight.Absolute -> value == height.value
    this is MovablePanelPeekHeight.FitToContent && height is MovablePanelPeekHeight.FitToContent -> true
    else -> false
}

/**
 * Являются ли объекты разными
 */
fun MovablePanelPeekHeight?.isNotEqual(height: MovablePanelPeekHeight): Boolean = isEqual(height).not()