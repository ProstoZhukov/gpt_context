package ru.tensor.sbis.design.whats_new.model

import ru.tensor.sbis.design.buttons.base.models.style.BrandButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle

/**
 * Тип кнопки начать на экране "Что нового".
 *
 * @author ps.smirnyh
 */
enum class SbisWhatsNewButtonStyle(internal val style: SbisButtonStyle) {

    /** @SelfDocumented */
    PRIMARY(PrimaryButtonStyle),

    /** @SelfDocumented */
    BRAND(BrandButtonStyle)
}