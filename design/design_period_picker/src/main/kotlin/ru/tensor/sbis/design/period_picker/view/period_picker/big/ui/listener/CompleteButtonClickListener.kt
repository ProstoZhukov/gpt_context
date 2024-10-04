package ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.listener

import android.view.View
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView
import ru.tensor.sbis.design.period_picker.view.utils.hideKeyboard
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.mask.date.DateInputView
import ru.tensor.sbis.design.period_picker.view.utils.checkPeriod
import ru.tensor.sbis.design.period_picker.view.utils.checkQuantum
import java.util.Calendar

/**
 * Слушатель события клика по кнопке окончания выбора периода.
 * Убирает фокус с полей ввода даты, чтобы сработала встроенная автокоррекция, и клавиатуру.
 * Проверяет корректность введенных данных.
 *
 * @author mb.kruglova
 */
internal class CompleteButtonClickListener(
    private val root: View,
    private val startDateInputView: DateInputView,
    private val endDateInputView: DateInputView,
    private val isSingleClick: Boolean,
    private val isOneDaySelection: Boolean,
    private val displayedRange: SbisPeriodPickerRange,
    private val dispatch: (PeriodPickerView.Event) -> Unit
) : View.OnClickListener {

    private var startPeriod: Calendar? = null
    private var endPeriod: Calendar? = null

    override fun onClick(v: View?) {
        clearDateInputViewFocus()
        hideKeyboard()

        var start = startPeriod
        var end = endPeriod
        val startDate = startDateInputView.getDate()
        val endDate = if (isOneDaySelection) startDate else endDateInputView.getDate()
        if (startDate != null && endDate != null) {
            start = startDate
            end = endDate
        }

        val isChecked = checkPeriod(start, end, displayedRange.start, displayedRange.end)
        when {
            (isSingleClick && isChecked && checkQuantum(start!!, end!!)) ||
                (!isSingleClick && isChecked) -> dispatch(PeriodPickerView.Event.ClickCompleteButton(start!!, end!!))
            else -> updateValidationStatus()
        }
    }

    /** Обновить выбранный период. */
    internal fun updatePeriod(newStart: Calendar?, newEnd: Calendar?) {
        startPeriod = newStart
        endPeriod = newEnd
    }

    /** Очистить фокус с полей ввода даты, чтобы сработала автокоррекция. */
    private fun clearDateInputViewFocus() {
        startDateInputView.clearFocus()
        endDateInputView.clearFocus()
    }

    /** Скрыть клавиатуру. */
    private fun hideKeyboard() {
        hideKeyboard(root)
    }

    /** Обновить статус. */
    private fun updateValidationStatus() {
        /*
        Подсвечиваем поля ввода даты как ошибочные в случае, если
        1) начальное значение выбранного периода больше конечного;
        2) пользователь не выбрал период ни через календарь, ни через поля ввода;
        3) в календаре дата выбирается по одному клику и выбранный в полях ввода период не равен одному кванту.
        */
        startDateInputView.validationStatus = ValidationStatus.Error("")
        endDateInputView.validationStatus = ValidationStatus.Error("")
    }
}