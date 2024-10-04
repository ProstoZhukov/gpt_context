package ru.tensor.sbis.design.logo.utils

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.logo.R
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/**
 * Стиль логотипа.
 * @param logoStyleAttr атрибут конкретного стиля логотипа
 * @param defaultLogoTheme тема конкретного стиля логотипа
 *
 * @author da.zolotarev
 */
sealed class SbisLogoStyle(
    @AttrRes private val logoStyleAttr: Int = ResourcesCompat.ID_NULL,
    @StyleRes private val defaultLogoTheme: Int = ResourcesCompat.ID_NULL
) {

    /**
     * Стиль "На странице".
     */
    object Page : SbisLogoStyle(
        logoStyleAttr = R.attr.sbisLogoPageStyle,
        defaultLogoTheme = R.style.SbisLogoPageDefaultTheme
    )

    /**
     * Стиль "В аккордеоне".
     */
    object Navigation : SbisLogoStyle(
        logoStyleAttr = R.attr.sbisLogoNavigationStyle,
        defaultLogoTheme = R.style.SbisLogoNavigationDefaultTheme
    )

    /**
     * Стиль "Чб для терминалов".
     */
    object Terminal : SbisLogoStyle(
        logoStyleAttr = R.attr.sbisLogoTerminalStyle,
        defaultLogoTheme = R.style.SbisLogoTerminalDefaultTheme
    )

    /**
     * Стиль "Чб для терминалов (выворотка)".
     */
    object TerminalReversed : SbisLogoStyle(
        logoStyleAttr = R.attr.sbisLogoTerminalReversedStyle,
        defaultLogoTheme = R.style.SbisLogoTerminalReversedDefaultTheme
    )

    internal class Zen(val themeModel: ZenThemeModel) : SbisLogoStyle() {
        override fun getSingleNameColor(context: Context) = themeModel.elementsColors.defaultColor.getColor(context)
        override fun getBrandNameColor(context: Context) = themeModel.elementsColors.defaultColor.getColor(context)
        override fun getAppNameColor(context: Context) = themeModel.elementsColors.defaultColor.getColor(context)
        override fun getIconColor(context: Context) = themeModel.elementsColors.defaultColor.getColor(context)
    }

    /**
     * Стиль-костыль.
     *
     * TODO убрать после https://dev.sbis.ru/opendoc.html?guid=61701bd5-f0d3-4e00-9e71-321233b35101&client=3
     */
    object BlackCustom : SbisLogoStyle(
        logoStyleAttr = R.attr.sbisLogoPageStyle,
        defaultLogoTheme = R.style.SbisLogoPageDefaultTheme
    ) {
        override fun getSingleNameColor(context: Context) = Color.BLACK
    }

    /**
     * Цвета названия бренда, если название состоит из одного слова(SABY/СБИС или брендовое).
     */
    open fun getSingleNameColor(context: Context) = context.getStyledColor(R.attr.sbisLogoSingleNameColor)

    /**
     * Цвет названия бренда в составном названии.
     */
    open fun getBrandNameColor(context: Context) = context.getStyledColor(R.attr.sbisLogoBrandNameColor)

    /**
     * Цвет названия приложения в составном названии.
     */
    open fun getAppNameColor(context: Context) = context.getStyledColor(R.attr.sbisLogoAppNameColor)

    /**
     * Цвет стандартной иконки (птицы).
     */
    open fun getIconColor(context: Context) = context.getStyledColor(R.attr.sbisLogoIconColor)

    /**
     * Получение цвета атрибута.
     */
    private fun Context.getStyledColor(@AttrRes colorAttr: Int) =
        ContextThemeWrapper(this, getDataFromAttrOrNull(logoStyleAttr) ?: defaultLogoTheme)
            .getColorFromAttr(colorAttr)

    companion object {
        /** @SelfDocumented */
        internal const val PAGE_TYPE = 1

        /** @SelfDocumented */
        internal const val NAVIGATION_TYPE = 2

        /** @SelfDocumented */
        internal const val TERMINAL_TYPE = 3

        /** @SelfDocumented */
        internal const val TERMINAL_REVERSED_TYPE = 4
    }
}