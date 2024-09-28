package ru.tensor.sbis.design.buttons.toggle.link

import ru.tensor.sbis.design.buttons.SbisToggleLinkButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon

/**
 * Описание API для управления кнопкой [SbisToggleLinkButton].
 *
 * @author mb.kruglova
 */
interface SbisToggleLinkButtonApi {

    /**
     * Текст в выбранном состоянии.
     * Если null, то параметр не настроен и текст не будет реагировать на состояние "выбран".
     */
    var titleSelected: String?

    /**
     * Иконка в выбранном состоянии.
     * Если null, то параметр не настроен и иконка не будет реагировать на состояние "выбран".
     */
    var iconSelected: SbisButtonIcon?

    /**
     * Цвет текста в выбранном состоянии.
     * Если null, то параметр не настроен и цвет текста не будет реагировать на состояние "выбран".
     */
    var titleColorSelected: Int?
}