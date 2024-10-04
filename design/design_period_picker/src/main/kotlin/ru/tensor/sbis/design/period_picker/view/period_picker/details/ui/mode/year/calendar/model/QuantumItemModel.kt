package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumPosition
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType

/**
 * Ячейка кванта календаря.
 *
 * @property label метка.
 * @property year год.
 * @property month месяц.
 * @property quantumSelection выделение дня.
 * @property isRangePart true если квант попадает в доступный для отображения период.
 *
 * @author mb.kruglova
 */
internal sealed class QuantumItemModel(
    val label: String,
    val year: Int,
    val month: Int,
    var quantumSelection: QuantumSelection,
    val isRangePart: Boolean
) {
    internal fun setSelection(
        type: QuantumType = QuantumType.STANDARD,
        position: QuantumPosition = QuantumPosition()
    ) {
        quantumSelection = QuantumSelection(
            type,
            position.getQuantumPlacementTypeByPosition().drawableType
        )
    }

    /** Сбросить выделение. */
    internal fun resetSelection() {
        quantumSelection = QuantumSelection(
            QuantumType.NO_SELECTION,
            QuantumDrawableType.DefaultDrawableType
        )
    }

    /**
     * Ячейка с наименованием года.
     * @property isMarked выделен ли год.
     */
    internal class YearLabelModel(
        label: String,
        year: Int,
        quantumSelection: QuantumSelection,
        isRangePart: Boolean,
        var isMarked: Boolean = false
    ) : QuantumItemModel(label, year, 0, quantumSelection, isRangePart)

    /** Ячейка с полугодием. */
    internal class HalfYearModel(
        label: String,
        year: Int,
        month: Int,
        quantumSelection: QuantumSelection,
        isRangePart: Boolean
    ) : QuantumItemModel(label, year, month, quantumSelection, isRangePart)

    /** Ячейка с кварталом. */
    internal class QuarterModel(
        label: String,
        year: Int,
        month: Int,
        quantumSelection: QuantumSelection,
        isRangePart: Boolean
    ) : QuantumItemModel(label, year, month, quantumSelection, isRangePart)

    /** Ячейка с месяцем. */
    internal class MonthModel(
        label: String,
        year: Int,
        month: Int,
        quantumSelection: QuantumSelection,
        isRangePart: Boolean
    ) : QuantumItemModel(label, year, month, quantumSelection, isRangePart)
}