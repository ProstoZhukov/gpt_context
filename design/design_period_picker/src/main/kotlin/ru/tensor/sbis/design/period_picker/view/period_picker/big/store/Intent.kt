package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.MONTH
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State
import java.util.Calendar

/**
 * Намерения и их обработка.
 *
 * @author mb.kruglova
 */
internal sealed interface Intent {

    /** @SelfDocumented */
    fun handle(
        executor: Executor,
        state: State
    )

    /** Намерение переключить режим календаря. */
    data class SwitchMode(
        val mode: SbisPeriodPickerMode,
        val startPeriod: Calendar?,
        val endPeriod: Calendar?
    ) : Intent {
        override fun handle(
            executor: Executor,
            state: State
        ) {
            if (mode == MONTH) {
                executor.publishLabel(Label.NavigateToMonthMode(startPeriod, endPeriod))
            } else {
                executor.publishLabel(Label.NavigateToYearMode(startPeriod, endPeriod))
            }

            executor.dispatchMessage(Message.UpdateMode(mode))
        }
    }

    /**
     * Намерение сбросить выбранный период в календаре.
     *
     * Если есть предустановленный период, то обновляем на него.
     */
    object ResetSelection : Intent {
        override fun handle(
            executor: Executor,
            state: State
        ) {
            if (state.startPresetPeriod != null && state.endPresetPeriod != null) {
                executor.publishLabel(Label.SetPresetSelection(state.startPresetPeriod, state.endPresetPeriod))
                executor.dispatchMessage(Message.UpdateSelection(state.startPresetPeriod, state.endPresetPeriod))
            } else {
                executor.publishLabel(Label.ResetSelection)
                executor.dispatchMessage(Message.ResetSelection)
            }
        }
    }

    /** Намерение обновить выбранный период. */
    data class UpdateSelection(
        private val tag: String,
        private val startPeriod: Calendar,
        private val endPeriod: Calendar
    ) : Intent {
        override fun handle(
            executor: Executor,
            state: State
        ) {
            executor.publishLabel(Label.UpdateSelection(tag))
            executor.dispatchMessage(Message.UpdateSelection(startPeriod, endPeriod))
        }
    }

    /** Намерение закрыть Большой выбор периода. */
    data class ClosePeriodPicker(
        val startPeriod: Calendar,
        val endPeriod: Calendar
    ) : Intent {
        override fun handle(
            executor: Executor,
            state: State
        ) {
            executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(startPeriod, endPeriod)))
            executor.dispatchMessage(Message.UpdateSelection(startPeriod, endPeriod))
        }
    }
}