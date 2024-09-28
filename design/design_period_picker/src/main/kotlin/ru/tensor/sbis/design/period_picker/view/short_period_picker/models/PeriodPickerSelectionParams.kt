package ru.tensor.sbis.design.period_picker.view.short_period_picker.models

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem

/**
 * Параметры для визуального выделения периода.
 *
 * @param selectionView визуальная обводка периода.
 * @param rootContainerLayout корневой контейнер.
 * @param startMonth месяц начала периода.
 * @param endMonth месяц окончания периода.
 * @param horizontalViewId id горизонтальной вью, до которой рисуется обводка периода.
 * @param item выбранный элемент списка.
 * @param position позиция выбранного элемента списка.
 * @param listener слушатель события выбора элемента списка.
 * @param isParentConstraint есть ли ограничение для обводки периода по родительскому вью.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerSelectionParams(
    val selectionView: View,
    val rootContainerLayout: ConstraintLayout,
    val startMonth: Int,
    val endMonth: Int,
    val horizontalViewId: Int,
    val item: ShortPeriodPickerItem,
    val position: Int,
    val listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit,
    val isParentConstraint: Boolean = false
)