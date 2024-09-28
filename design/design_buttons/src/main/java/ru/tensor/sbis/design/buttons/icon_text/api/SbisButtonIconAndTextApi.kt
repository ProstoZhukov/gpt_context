package ru.tensor.sbis.design.buttons.icon_text.api

import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.models.AbstractHeightModel
import ru.tensor.sbis.design.utils.theme.AbstractHeightCompatibilityView

/**
 * Описание API для управления кнопкой [AbstractSbisIconAndTextButton][ru.tensor.sbis.design.buttons.icon_text.AbstractSbisIconAndTextButton].
 *
 * Базовые настройки и стилизации находятся в [AbstractButtonApi][ru.tensor.sbis.design.buttons.base.api.AbstractButtonApi].
 *
 * @author mb.kruglova
 */
interface SbisButtonIconAndTextApi<SIZE : AbstractHeightModel> : AbstractHeightCompatibilityView<SIZE> {
    /**
     * Модель внешнего вида кнопки.
     */
    var model: SbisButtonModel

    /**
     * Вид выравнивания кнопки (если она в контейнере) и ее содержимого.
     */
    var align: HorizontalAlignment
}