package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumPlacementType.*

/**
 * Положение кванта относительно других квантов.
 *
 * @property left есть ли слева от текущего кванта другие кванты, входящие в выбранный период.
 * @property top есть ли сверху от текущего кванта другие кванты, входящие в выбранный период.
 * @property right есть ли справа от текущего кванта другие кванты, входящие в выбранный период.
 * @property bottom есть ли снизу от текущего кванта другие кванты, входящие в выбранный период.
 *
 * @author mb.kruglova
 */
internal data class QuantumPosition(
    var left: Boolean = false,
    var top: Boolean = false,
    var right: Boolean = false,
    var bottom: Boolean = false
) {

    /** @SelfDocumented */
    internal fun getQuantumPlacementTypeByPosition(): QuantumPlacementType {
        return when {
            top && bottom && left && right -> MULTIPLE
            top && bottom && left -> NO_RIGHT
            top && bottom && right -> NO_LEFT
            bottom && left && right -> NO_TOP
            top && left && right -> NO_BOTTOM
            top && left -> TOP_LEFT
            top && right -> TOP_RIGHT
            bottom && left -> BOTTOM_LEFT
            bottom && right -> BOTTOM_RIGHT
            right && left -> IN_LINE
            top -> TOP
            bottom -> BOTTOM
            left -> LEFT
            right -> RIGHT
            else -> SINGLE
        }
    }
}