package ru.tensor.sbis.design.logo.api

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.logo.R
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisDrawable
import ru.tensor.sbis.design.R as RDesign

/**
 * Тип логотипа.
 *
 * @author da.zolotarev
 */
sealed interface SbisLogoType {

    /** Название бренда (saby). */
    val brandName: PlatformSbisString
        get() = EMPTY_SBIS_STRING

    /** Название приложения бренда (clients, get ...). */
    val appName: PlatformSbisString
        get() = EMPTY_SBIS_STRING

    /** Позиция расположения иконки бренда относительно названий. */
    val iconPosition: HorizontalPosition?
        get() = DEFAULT_ICON_POSITION

    /**
     * Иконка бренда.
     */
    val iconImage: SbisDrawable?
        get() = null

    /**
     * Логотип вида: {Иконка(Птичка)}{Название бренда(brandName)}.
     */
    object TextIcon : SbisLogoType {
        override val iconPosition = HorizontalPosition.RIGHT
        override val brandName = DEFAULT_SBIS_BRAND_NAME_TRANSLATABLE
    }

    /**
     * Логотип вида: {Название бренда(brandName)}{Иконка(Птичка)}.
     */
    object IconText : SbisLogoType {
        override val iconPosition = HorizontalPosition.LEFT
        override val brandName = DEFAULT_SBIS_BRAND_NAME_TRANSLATABLE
    }

    /**
     * Логотип вида: {Иконка(Птичка)?}{Название бренда(brandName)}{Название приложения(appName)}{Иконка(Птичка)?}.
     *
     * Если параметр [iconImage] не задан ни через [SbisLogoType], ни через xml атрибут [R.attr.SbisLogo_logo], тогда
     * иконка логотипа по умолчанию берётся из глобального атрибута [RDesign.attr.logoViewDefaultIcon].
     */
    class TextIconAppName(
        override val appName: PlatformSbisString = EMPTY_SBIS_STRING,
        override val brandName: PlatformSbisString = DEFAULT_SBIS_BRAND_NAME,
        override val iconPosition: HorizontalPosition = DEFAULT_ICON_POSITION,
        override val iconImage: SbisDrawable? = null
    ) : SbisLogoType

    /**
     * Логотип вида: {Иконка(Птичка)}.
     */
    object Icon : SbisLogoType

    /**
     * Пустой логотип.
     */
    object Empty : SbisLogoType {
        override val iconPosition: HorizontalPosition? = null
    }

    companion object {
        /** @SelfDocumented */
        internal const val TEXT_ICON_TYPE = 1

        /** @SelfDocumented */
        internal const val ICON_TEXT_TYPE = 2

        /** @SelfDocumented */
        internal const val ICON = 3

        /** @SelfDocumented */
        internal const val TEXT_ICON_APP_NAME = 4

        private val EMPTY_SBIS_STRING = PlatformSbisString.Value(StringUtils.EMPTY)

        private val DEFAULT_SBIS_BRAND_NAME = PlatformSbisString.Res(R.string.design_logo_sbis)

        private val DEFAULT_SBIS_BRAND_NAME_TRANSLATABLE =
            PlatformSbisString.Res(R.string.design_logo_sbis_translatable)

        private val DEFAULT_ICON_POSITION = HorizontalPosition.LEFT
    }
}
