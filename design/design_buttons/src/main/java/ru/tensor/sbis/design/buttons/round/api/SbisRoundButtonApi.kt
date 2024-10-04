package ru.tensor.sbis.design.buttons.round.api

import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.utils.theme.AbstractHeightCompatibilityView

/**
 * Описание API для управления кнопкой [SbisRoundButton].
 *
 * Базовые настройки и стилизации находятся в [AbstractButtonApi][ru.tensor.sbis.design.buttons.base.api.AbstractButtonApi].
 *
 * @author ma.kolpakov
 */
interface SbisRoundButtonApi : AbstractHeightCompatibilityView<SbisRoundButtonSize> {

    var icon: SbisButtonIcon

    /**
     * Тип кнопки.
     */
    var type: SbisRoundButtonType

    /**
     * Скругление кнопки.
     */
    var cornerRadiusValue: Float

    /**
     * Значение счётчика.
     *
     * ВАЖНО! Для корректного отображения счётчика необходим для родителя кнопки выставить флаг clipChildren = false.
     */
    var counter: SbisButtonCounter?

    /**
     * Позиционирование счётчика.
     */
    var counterPosition: HorizontalPosition
}
