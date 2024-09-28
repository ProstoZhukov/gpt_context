package ru.tensor.sbis.design.buttons.toggle.round

import ru.tensor.sbis.design.buttons.SbisToggleRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon

/**
 * Описание API для управления кнопкой [SbisToggleRoundButton].
 *
 * @author mb.kruglova
 */
interface SbisToggleRoundButtonApi {

    /**
     * Иконка в выбранном состоянии.
     * Если null, то параметр не настроен и иконка не будет реагировать на состояние "выбран".
     */
    var iconSelected: SbisButtonIcon?

    /**
     * Цвет иконки в выбранном состоянии.
     * Если null, то параметр не настроен и цвет иконки не будет реагировать на состояние "выбран".
     */
    var iconColorSelected: Int?

    /**
     * Цвет кнопки в выбранном состоянии.
     * Если null, то параметр не настроен и цвет кнопки не будет реагировать на состояние "выбран".
     */
    var backgroundColorSelected: Int?
}