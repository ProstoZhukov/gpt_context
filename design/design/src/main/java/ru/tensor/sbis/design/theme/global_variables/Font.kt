package ru.tensor.sbis.design.theme.global_variables

/**
 * Параметры шрифта без категории из глобальных переменных.
 *
 * @author mb.kruglova
 */

import android.content.Context
import androidx.annotation.FontRes
import androidx.annotation.IntegerRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider

private val fontWeightAttrRes = androidx.core.R.attr.fontWeight

private val iconFontFamilyAttrRes = R.attr.iconFontFamily

/**
 * @SelfDocumented
 */
@IntegerRes
fun getFontWeight(context: Context) = ThemeTokensProvider.getInteger(context, fontWeightAttrRes)

/**
 * @SelfDocumented
 */
@FontRes
fun getIconFontFamily(context: Context) = ThemeTokensProvider.getFontRes(context, iconFontFamilyAttrRes)