package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State
import java.util.Calendar

/**
 * Список обработанных Executor'ом действий и намерений.
 *
 * @author mb.kruglova
 */
internal sealed interface Message {

    /** @SelfDocumented */
    fun reduce(state: State): State

    /** Сообщение о сбросе выбранного периода. */
    object ResetSelection : Message {
        override fun reduce(state: State): State {
            return state.copy(
                startInitialPeriod = null,
                endInitialPeriod = null,
                startPeriod = null,
                endPeriod = null
            )
        }
    }

    /** Сообщение об обновлении выбранного периода. */
    data class UpdateSelection(
        private val start: Calendar,
        private val end: Calendar
    ) : Message {
        override fun reduce(state: State): State {
            return state.copy(
                startInitialPeriod = null,
                endInitialPeriod = null,
                startPeriod = start,
                endPeriod = end
            )
        }
    }

    /** Сообщение об обновлении режима календаря. */
    data class UpdateMode(
        private val newMode: SbisPeriodPickerMode
    ) : Message {
        override fun reduce(state: State): State = state.copy(mode = newMode)
    }
}