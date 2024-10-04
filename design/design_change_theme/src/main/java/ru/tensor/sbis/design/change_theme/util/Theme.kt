package ru.tensor.sbis.design.change_theme.util

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import kotlinx.parcelize.Parcelize

/**
 * Класс содержащий информацию о теме.
 *
 * @param id - уникальный ID объекта, нужен для сохранения в SharedPreferences.
 * @param globalTheme - ссылка на ресурс ГЛОБАЛЬНОЙ темы, например DefaultLightTheme.
 * @param appTheme - ссылка на ресурс темы ПРИЛОЖЕНИЯ, например CommunicatorAppTheme.
 * @param previewRes - ссылка на превью темы для экрана смены тем в настройках.
 */
@Parcelize
data class Theme(
    val id: Int,
    @StyleRes val globalTheme: Int,
    @StyleRes val appTheme: Int,
    @DrawableRes val previewRes: Int
) : Parcelable