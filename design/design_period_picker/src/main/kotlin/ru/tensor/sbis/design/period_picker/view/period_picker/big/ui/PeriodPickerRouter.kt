package ru.tensor.sbis.design.period_picker.view.period_picker.big.ui

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.*
import ru.tensor.sbis.design.period_picker.view.utils.findCalendarUpdateDelegate
import ru.tensor.sbis.design.period_picker.view.utils.manageFragmentTransaction
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import java.util.Calendar

/**
 * Роутер Большого выбора периода.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerRouter(
    private val isEnabled: Boolean,
    private val selectionType: SbisPeriodPickerSelectionType,
    private val displayedRange: SbisPeriodPickerRange,
    private val isBottomPosition: Boolean,
    private val presetStartDate: Calendar?,
    private val presetEndDate: Calendar?,
    private val anchorDate: Calendar?,
    internal val requestKey: String,
    internal val resultKey: String
) : FragmentRouter() {

    /** Открыть режим Месяц. */
    fun openMonthMode(
        startPeriod: Calendar?,
        endPeriod: Calendar?
    ) = execute {
        manageFragmentTransaction(
            childFragmentManager,
            MONTH,
            startPeriod,
            endPeriod
        )
    }

    /** Открыть режим Год. */
    fun openYearMode(
        startPeriod: Calendar?,
        endPeriod: Calendar?
    ) = execute {
        manageFragmentTransaction(
            childFragmentManager,
            YEAR,
            startPeriod,
            endPeriod
        )
    }

    /** Сбросить выделение в календаре. */
    fun resetSelection() = execute {
        findCalendarUpdateDelegate(MONTH.tag)?.resetSelection()
        findCalendarUpdateDelegate(YEAR.tag)?.resetSelection()
    }

    /** Настроить предустановленное выделение в календаре. */
    fun setPresetSelection(presetStart: Calendar, presetEnd: Calendar) = execute {
        findCalendarUpdateDelegate(MONTH.tag)?.setPresetSelection(presetStart, presetEnd)
        findCalendarUpdateDelegate(YEAR.tag)?.setPresetSelection(presetStart, presetEnd)
    }

    /** Обновить выделение в календаре. */
    fun updateSelection(tag: String?) = execute {
        tag?.let {
            findCalendarUpdateDelegate(tag)?.resetSelection()
        }
    }

    /** Послать результат выбора периода в вызывающий элемент и закрыть календарь. */
    fun closePeriodPicker(range: SbisPeriodPickerRange) = execute {
        val hostFragment = parentFragment
        hostFragment?.setFragmentResult(
            requestKey,
            bundleOf(resultKey to range)
        )

        if (hostFragment is Container.Closeable) {
            // Если закрывать шторку как DialogFragment, то не будет анимации у шторки.
            (hostFragment as Container.Closeable).closeContainer()
        } else {
            (hostFragment as DialogFragment).dismiss()
        }
    }

    /** @SelfDocumented */
    private fun manageFragmentTransaction(
        fragmentManager: FragmentManager,
        mode: SbisPeriodPickerMode,
        startPeriod: Calendar?,
        endPeriod: Calendar?
    ) {
        manageFragmentTransaction(
            fragmentManager,
            mode,
            startPeriod,
            endPeriod,
            presetStartDate,
            presetEndDate,
            isEnabled,
            selectionType,
            displayedRange,
            isBottomPosition,
            anchorDate,
            requestKey,
            resultKey,
            null
        )
    }
}