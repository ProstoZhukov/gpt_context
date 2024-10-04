package ru.tensor.sbis.design.logo.api

import ru.tensor.sbis.design.logo.SbisLogoView
import ru.tensor.sbis.design.logo.utils.SbisLogoStyle
import ru.tensor.sbis.design.theme.zen.ZenThemeSupport

/**
 * API [SbisLogoView].
 *
 * @author da.zolotarev
 */
interface SbisLogoApi : ZenThemeSupport {
    /**
     * Тип логотипа.
     */
    var type: SbisLogoType

    /**
     * Стиль логотипа.
     */
    var style: SbisLogoStyle
}