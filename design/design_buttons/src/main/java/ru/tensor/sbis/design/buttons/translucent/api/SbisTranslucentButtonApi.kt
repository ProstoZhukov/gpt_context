package ru.tensor.sbis.design.buttons.translucent.api

import ru.tensor.sbis.design.buttons.translucent.models.SbisTranslucentButtonStyle

/**
 * Api для кнопок с поддержкой полупрозрачного оформления.
 *
 * @author mb.kruglova
 */
interface SbisTranslucentButtonApi {

    /**
     * Стиль прозрачности.
     */
    var translucentStyle: SbisTranslucentButtonStyle
}