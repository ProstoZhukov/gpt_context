package ru.tensor.sbis.design.buttons.api

import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.SbisFloatingButtonPanel
import ru.tensor.sbis.design.theme.HorizontalAlignment

/**
 * Описание API для управления плавающим контейнером [SbisFloatingButtonPanel].
 *
 * @author ma.kolpakov
 */
interface SbisFloatingButtonPanelApi {

    var buttons: List<AbstractSbisButton<*, *>>

    var align: HorizontalAlignment
}
