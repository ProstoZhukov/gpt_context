package ru.tensor.sbis.design.buttons.arrow.api

import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.buttons.SbisArrowButton
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonBackgroundType
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonStyle

/**
 * Описание API для управления кнопкой [SbisArrowButton]
 *
 * Базовые настройки и стилизации находятся в [AbstractButtonApi][ru.tensor.sbis.design.buttons.base.api.AbstractButtonApi]
 *
 * @author mb.kruglova
 */
interface SbisArrowButtonApi {

    /** Режим кнопки. */
    val mode: HorizontalPosition

    /** Стиль кнопки пролистывания. */
    val arrowButtonStyle: SbisArrowButtonStyle

    /** Тип фона кнопки пролистывания. */
    val arrowButtonBackgroundType: SbisArrowButtonBackgroundType
}