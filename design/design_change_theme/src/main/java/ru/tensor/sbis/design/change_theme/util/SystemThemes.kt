package ru.tensor.sbis.design.change_theme.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель системных тем, которые применяются в режиме системной темизации (в зависимости от
 *      установленной системной темы).
 *
 * @author ra.geraskin
 */
@Parcelize
data class SystemThemes(

    /**
     * Тема используемая с системной ДНЕВНОЙ(светлой) темой.
     */
    val dayTheme: Theme,

    /**
     * Тема используемая с системной НОЧНОЙ(тёмной) темой.
     */
    val nightTheme: Theme
) : Parcelable