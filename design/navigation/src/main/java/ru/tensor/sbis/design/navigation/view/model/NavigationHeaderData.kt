package ru.tensor.sbis.design.navigation.view.model

import ru.tensor.sbis.design.logo.api.SbisLogoType

/**
 * Данные для отображения в шапке аккордеона.
 *
 * @author da.zolotarev
 */
sealed class NavigationHeaderData(
    val counter: NavigationCounter?,
    val counters: NavigationCounters? = null
) {
    /** @SelfDocumented */
    class LogoData(
        val logo: SbisLogoType,
        counter: NavigationCounter? = null,
        counters: NavigationCounters? = null
    ) : NavigationHeaderData(counter, counters)
}
