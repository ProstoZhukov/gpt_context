package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.*
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerRouter
import java.util.Calendar

/**
 * Описание сайд-эффектов и состояний.
 *
 * @author mb.kruglova
 */
internal interface PeriodPickerStore : Store<Intent, State, Label> {

    /** Сайд-эффекты. */
    sealed interface Label {
        fun handle(router: PeriodPickerRouter)

        /** Перейти в режим Месяц. */
        class NavigateToMonthMode(
            val startPeriod: Calendar?,
            val endPeriod: Calendar?
        ) : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.openMonthMode(startPeriod, endPeriod)
            }
        }

        /** Перейти в режим Год. */
        class NavigateToYearMode(
            val startPeriod: Calendar?,
            val endPeriod: Calendar?
        ) : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.openYearMode(startPeriod, endPeriod)
            }
        }

        /** Сбросить выбранный период во всех режимах календаря. */
        object ResetSelection : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.resetSelection()
            }
        }

        /** Установить предустановленный период в календаре. */
        class SetPresetSelection(
            private val presetStart: Calendar,
            private val presetEnd: Calendar
        ) : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.setPresetSelection(presetStart, presetEnd)
            }
        }

        /** Обновить выбранный период в календаре. */
        class UpdateSelection(
            val tag: String?
        ) : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.updateSelection(tag)
            }
        }

        /** Закрыть компонент выбор периода. */
        class ClosePeriodPicker(val range: SbisPeriodPickerRange) : Label {
            override fun handle(router: PeriodPickerRouter) {
                router.closePeriodPicker(range)
            }
        }
    }

    /**
     * Модель состояний.
     * @param mode режим календаря.
     * @param startInitialPeriod начало исходного выбранного периода.
     * @param endInitialPeriod конец исходного выбранного периода.
     * @param startPresetPeriod начало предустановленного периода.
     * @param endPresetPeriod конец предустановленного периода.
     * @param startPeriod начало выбранного периода.
     * @param endPeriod конец выбранного периода.
     */
    @Parcelize
    data class State(
        val mode: SbisPeriodPickerMode = YEAR,
        val startInitialPeriod: Calendar?,
        val endInitialPeriod: Calendar?,
        val startPresetPeriod: Calendar?,
        val endPresetPeriod: Calendar?,
        val startPeriod: Calendar? = null,
        val endPeriod: Calendar? = null
    ) : Parcelable
}