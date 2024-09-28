package ru.tensor.sbis.design.period_picker.view.period_picker.big.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Intent
import java.util.Calendar

/**
 * Контракт представления.
 *
 * @author mb.kruglova
 */
internal interface PeriodPickerView : MviView<Model, Event> {

    /**
     * События, генерируемые представлением.
     */
    sealed interface Event {

        /** @SelfDocumented */
        fun eventToIntent(): Intent

        /** Событие клика по кнопке режима календаря. */
        data class ClickModeButton(
            val mode: SbisPeriodPickerMode,
            val startPeriod: Calendar?,
            val endPeriod: Calendar?
        ) : Event {
            override fun eventToIntent(): Intent = Intent.SwitchMode(mode, startPeriod, endPeriod)
        }

        /** Событие клика по кнопке текущей даты. */
        object ClickCurrentDateButton : Event {
            override fun eventToIntent(): Intent = Intent.ResetSelection
        }

        /** Событие клика по кнопке окончания выбора периода. */
        data class ClickCompleteButton(
            val startPeriod: Calendar,
            val endPeriod: Calendar
        ) : Event {
            override fun eventToIntent(): Intent = Intent.ClosePeriodPicker(startPeriod, endPeriod)
        }
    }

    /**
     * Модель представления.
     * @param mode режим календаря.
     * @param startPresetPeriod начало предустановленного периода.
     * @param endPresetPeriod конец предустановленного периода.
     * @param startPeriod начало выбранного периода.
     * @param endPeriod конец выбранного периода.
     */
    data class Model(
        private val mode: SbisPeriodPickerMode,
        private val startPresetPeriod: Calendar?,
        private val endPresetPeriod: Calendar?,
        private val startPeriod: Calendar? = null,
        private val endPeriod: Calendar? = null
    ) {

        /** @SelfDocumented */
        internal fun getStartPresetPeriod() = startPresetPeriod

        /** @SelfDocumented */
        internal fun getEndPresetPeriod() = endPresetPeriod

        /** @SelfDocumented */
        internal fun getStartPeriod() = startPeriod

        /** @SelfDocumented */
        internal fun getEndPeriod() = endPeriod

        /** @SelfDocumented */
        internal fun getMode() = mode
    }

    fun interface Factory : (View) -> PeriodPickerView
}