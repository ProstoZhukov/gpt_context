package ru.tensor.sbis.design.buttons.base.models.title

import androidx.annotation.Discouraged
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.models.FontSizeModel

/**
 * Размер текста в кнопке.
 *
 * @author ma.kolpakov
 */
enum class SbisButtonTitleSize(
    override val globalVar: FontSize
) : FontSizeModel {

    XS(FontSize.X3S),
    M(FontSize.XS),
    XL(FontSize.L),
    X2L(FontSize.XL),
    X3L(FontSize.X2L),

    @Discouraged(message = "Используется только для SbisKeyboardButton, не используйте в SbisButton.")
    X5L(FontSize.X5L)
}