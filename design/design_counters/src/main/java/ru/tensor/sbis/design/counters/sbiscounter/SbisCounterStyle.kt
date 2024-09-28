package ru.tensor.sbis.design.counters.sbiscounter

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.counters.R

/**
 * Стили счетчиков.
 *
 * @author ma.kolpakov
 */
sealed class SbisCounterStyle(
    @AttrRes private val styleAttrRegular: Int,
    @StyleRes private val styleResRegular: Int,
    @AttrRes private val styleAttrNavigation: Int,
    @StyleRes private val styleResNavigation: Int
) {
    /** Получить атрибут стиля в зависимости от места использования. */
    @AttrRes
    internal fun getAttrRes(useCase: SbisCounterUseCase) = when (useCase) {
        SbisCounterUseCase.REGULAR -> styleAttrRegular
        SbisCounterUseCase.NAVIGATION -> styleAttrNavigation
    }

    /** Получить ресурс стиля в зависимости от места использования. */
    @StyleRes
    internal fun getStyleRes(useCase: SbisCounterUseCase) = when (useCase) {
        SbisCounterUseCase.REGULAR -> styleResRegular
        SbisCounterUseCase.NAVIGATION -> styleResNavigation
    }
}

/** Акцентнаый стиль. */
object PrimarySbisCounterStyle : SbisCounterStyle(
    R.attr.primaryRegularSbisCounterTheme,
    R.style.SbisCounterRegularDefaultPrimaryTheme,
    R.attr.primaryNavigationSbisCounterTheme,
    R.style.SbisCounterNavigationDefaultPrimaryTheme
)

/** Неакцентный стиль. */
object InfoSbisCounterStyle : SbisCounterStyle(
    R.attr.infoRegularSbisCounterTheme,
    R.style.SbisCounterRegularDefaultInfoTheme,
    R.attr.infoNavigationSbisCounterTheme,
    R.style.SbisCounterNavigationDefaultInfoTheme
)
