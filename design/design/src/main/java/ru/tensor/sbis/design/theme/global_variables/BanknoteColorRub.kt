package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BanknoteColorModelRub

/**
 * Линейка цветов банкнот из глобальных переменных.
 * Предназначена для RU региона.
 *
 * Реализует [BanknoteColorModelRub].
 *
 * @author mb.kruglova
 */
enum class BanknoteColorRub(
    @AttrRes private val colorAttrRes: Int
) : BanknoteColorModelRub {

    RUB100(R.attr.rub100BackgroundColor),
    RUB200(R.attr.rub200BackgroundColor),
    RUB500(R.attr.rub500BackgroundColor),
    RUB1000(R.attr.rub1000BackgroundColor),
    RUB2000(R.attr.rub2000BackgroundColor),
    RUB5000(R.attr.rub5000BackgroundColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}