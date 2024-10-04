package ru.tensor.sbis.design.design_menu.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.Px
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.design_menu.databinding.MenuTextDividerBinding
import ru.tensor.sbis.design.design_menu.dividers.TextDivider
import ru.tensor.sbis.design.design_menu.dividers.TextLineDivider
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.HorizontalAlignment.*
import ru.tensor.sbis.design.design_menu.utils.DividerViewTextPaddings.TEXT_DEFAULT
import ru.tensor.sbis.design.design_menu.utils.DividerViewTextPaddings.TEXT_ZERO
import ru.tensor.sbis.design.design_menu.utils.DividerViewTextPaddings.TEXT_EMPTY_START

/**
 * Модель для хранения значений падингов для текста в вёрстке разделителя в различных компоновках.
 * Отступы линии и паддинг с краёв контейнера разделителя для упрощения вынесены в [xml][MenuTextDividerBinding].
 *
 * @author ra.geraskin
 */
internal enum class DividerViewTextPaddings(private val offset: Int) {

    TEXT_DEFAULT(4),
    TEXT_ZERO(0),
    TEXT_EMPTY_START(32);

    @Px
    internal fun getDimen(context: Context) = context.dpToPx(offset)
}

/**
 * Значение правого отступа теста разделителя [с текстом и линиями][TextLineDivider].
 */
private val HorizontalAlignment.dividerTextMarginEnd
    get() = when (this) {
        LEFT -> TEXT_ZERO
        CENTER -> TEXT_ZERO
        RIGHT -> TEXT_DEFAULT
    }

/**
 * Обновить layoutParams текстового заголовка [разделителя с текстом и линиями][TextLineDivider], в зависимости от
 * расположения текста относительно линий.
 */
internal fun TextLineDivider.updateTextLayoutParams(textView: View) = textView.updateLayoutParams<MarginLayoutParams> {
    marginStart = TEXT_ZERO.getDimen(textView.context)
    marginEnd = textAlignment.dividerTextMarginEnd.getDimen(textView.context)
}

/**
 * Обновить layoutParams текстового заголовка [разделителя с текстом][TextDivider].
 */
internal fun TextDivider.updateTextLayoutParams(textView: View) = textView.updateLayoutParams<MarginLayoutParams> {
    marginStart = TEXT_EMPTY_START.getDimen(textView.context)
    marginEnd = TEXT_DEFAULT.getDimen(textView.context)
}

/*

    Преобразованная схема отступов элемента разделителя

       20   4 линия 4                           20
       20   4 линия 4    0 текст 0   4 линия 4  20
       20   4 линия 4    0 текст 4              20
       20                0 текст 0   4 линия 4  20
       20               32 текст 4              20

 */