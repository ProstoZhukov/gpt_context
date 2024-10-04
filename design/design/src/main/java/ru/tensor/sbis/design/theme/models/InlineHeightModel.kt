package ru.tensor.sbis.design.theme.models

import ru.tensor.sbis.design.theme.global_variables.InlineHeight

/**
 * Модель объекта, который поставляет информацию о высоте строчного view.
 *
 * @author mb.kruglova
 */
interface InlineHeightModel : AbstractHeightModel {
    override val globalVar: InlineHeight
}