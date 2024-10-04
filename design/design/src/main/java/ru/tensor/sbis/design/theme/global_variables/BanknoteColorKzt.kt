package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BanknoteColorModelKzt

/**
 * Линейка цветов банкнот из глобальных переменных.
 * Предназначена для KZ региона.
 *
 * Реализует [KZTBanknoteColorModel].
 *
 * @author da.pavlov1
 */
enum class BanknoteColorKzt(
    @AttrRes private val colorAttrRes: Int
) : BanknoteColorModelKzt {

    KZT200(R.attr.kzt200BackgroundColor),
    KZT500(R.attr.kzt500BackgroundColor),
    KZT1000(R.attr.kzt1000BackgroundColor),
    KZT2000(R.attr.kzt2000BackgroundColor),
    KZT5000(R.attr.kzt5000BackgroundColor),
    KZT10000(R.attr.kzt10000BackgroundColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}