package ru.tensor.sbis.design.navigation.view.model

import androidx.annotation.StringRes
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Описание названия элемента меню.
 *
 * @param default название обычной длины.
 * @param short сокращённая форма названия.
 * @param labelForRightAlignment текст, по правому краю которого должно выравниваться название элемента меню,
 * при истинном значении [isAlignedRight].
 * @param isAlignedRight должно ли название выравниваться по правому краю [labelForRightAlignment].
 *
 * @author ma.kolpakov
 */
data class NavigationItemLabel(
    val default: SbisString,
    val short: SbisString = default,
    val labelForRightAlignment: SbisString = default,
    val isAlignedRight: Boolean = false
) {
    constructor(
        @StringRes default: Int,
        @StringRes short: Int = default,
        @StringRes labelForRightAlignment: Int = default,
        isAlignedRight: Boolean = false
    ) : this(
        PlatformSbisString.Res(default),
        PlatformSbisString.Res(short),
        PlatformSbisString.Res(labelForRightAlignment),
        isAlignedRight
    )
}