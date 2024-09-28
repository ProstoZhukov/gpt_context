/**
 * Набор стандартных тем приложений.
 * В конструктор необходимо передавать тему вашего приложения НЕ наследуюущуюся от глобальной темы,
 * чтобы подхватить атрибуты, специфичные для приложения.
 * Также есть возможность передать иную глобальную тему, если есть необходимость.
 * Стандарт - https://www.figma.com/file/aoDaE5WOM1bcQMqXhz3uIH/Тема-оформления-по-умолчанию?node-id=10969-10924&t=6flQ8OlKj7ZMMa2l-0
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.change_theme.util

import androidx.annotation.StyleRes
import ru.tensor.sbis.design.change_theme.R
import ru.tensor.sbis.design.R as RDesign

/** @SelfDocumented */
fun getDefaultTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureDefaultLightTheme
) = Theme(
    DEFAULT_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_default
)

/** @SelfDocumented */
fun getDefaultAccordionLightTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureDefaultLightNavigationLight
) = Theme(
    ACC_LIGHT_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_default_accordeon_light
)

/** @SelfDocumented */
fun getPinkTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureDefaultPinkTheme
) = Theme(
    PINK_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_pink
)

/** @SelfDocumented */
fun getColaTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureDefaultСolaTheme
) = Theme(
    COLA_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_cola
)

/** @SelfDocumented */
fun getUbrrTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureDefaultUbrrTheme
) = Theme(
    UBBR_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_ubrr
)

/** @SelfDocumented */
fun getLargeDarkTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureLargeDarkTheme
) = Theme(
    LARGE_DARK_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_large_dark
)

/** @SelfDocumented */
fun getBlackBlueTheme(
    @StyleRes appTheme: Int,
    @StyleRes globalTheme: Int = RDesign.style.FeatureBlackBlueTheme
) = Theme(
    BLACK_BLUE_THEME_ID,
    globalTheme,
    appTheme,
    R.drawable.preview_black_blue
)

const val DEFAULT_THEME_ID = 0
const val ACC_LIGHT_THEME_ID = 1
const val PINK_THEME_ID = 2
const val COLA_THEME_ID = 3
const val UBBR_THEME_ID = 4
const val LARGE_DARK_THEME_ID = 5
const val BLACK_BLUE_THEME_ID = 6
