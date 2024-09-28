package ru.tensor.sbis.date_picker.items

import android.graphics.Typeface
import androidx.databinding.ObservableInt

/**
 * Базовый класс для элементов календаря с заголовком
 *
 * @author mb.kruglova
 */
abstract class NamedItemVM(
    open val label: String,
    val fontStyle: ObservableInt = ObservableInt(notSelected)
) {
    fun setSelected() {
        fontStyle.set(Typeface.BOLD)
    }

    fun setNoSelected() {
        fontStyle.set(notSelected)
    }
}

private const val notSelected = Typeface.NORMAL