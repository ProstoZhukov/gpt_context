package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumPosition
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import java.util.Calendar

/**
 * Ячейка дня.
 * @property dayOfMonth день месяца.
 * @property dayOfWeek день недели.
 * @property date описывает день в году.
 * @property daySelection выделение дня.
 * @property counter счётчик.
 * @property markerType тип маркера.
 * @property isCurrent true если день текущий, иначе false.
 * @property isRangePart true если день попадает в доступный для отображения период.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class DayModel(
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    override val date: Calendar,
    var daySelection: QuantumSelection,
    var counter: String,
    val markerType: MarkerType,
    val isCurrent: Boolean,
    val isRangePart: Boolean,
    var isAvailable: Boolean = true,
    var customTheme: SbisPeriodPickerDayCustomTheme
) : DayItemModel, Parcelable {

    override val isFirstItem = false

    /** Настроить выделение дня. */
    internal fun setSelection(
        dayType: QuantumType = QuantumType.STANDARD,
        dayPosition: QuantumPosition = QuantumPosition()
    ) {
        daySelection = QuantumSelection(
            dayType,
            dayPosition.getQuantumPlacementTypeByPosition().drawableType
        )
    }

    /** Сбросить выделение. */
    internal fun resetSelection() {
        daySelection = QuantumSelection(
            QuantumType.NO_SELECTION,
            QuantumDrawableType.DefaultDrawableType
        )
    }
}