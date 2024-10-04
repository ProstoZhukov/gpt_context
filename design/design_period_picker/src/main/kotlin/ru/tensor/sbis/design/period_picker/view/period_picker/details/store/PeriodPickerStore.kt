package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.period_picker.details.model.LabelParams
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Intent as HostIntent
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarUpdateDelegate
import ru.tensor.sbis.design.period_picker.view.utils.getParentFragment
import ru.tensor.sbis.design.period_picker.view.utils.getRequestKey
import ru.tensor.sbis.design.period_picker.view.utils.getResultKey
import ru.tensor.sbis.design.period_picker.view.utils.sendResult
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import java.util.Calendar

/**
 * Описание сайд-эффектов и состояний.
 *
 * @author mb.kruglova
 */
internal interface PeriodPickerStore : Store<Intent, State, Label> {

    /** Сайд-эффекты. */
    sealed interface Label {

        /** @SelfDocumented */
        fun handle(
            fragment: Fragment,
            params: LabelParams
        )

        /** Закрыть компонент выбор периода. */
        class ClosePeriodPicker(val range: SbisPeriodPickerRange) : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                val hostFragment = fragment.getParentFragment(params.isCompact)
                hostFragment?.sendResult(range, fragment.getRequestKey(), fragment.getResultKey())
                if (hostFragment is Container.Closeable) {
                    // Если закрывать шторку как DialogFragment, то не будет анимации у шторки.
                    (hostFragment as Container.Closeable).closeContainer()
                } else {
                    (hostFragment as DialogFragment).dismiss()
                }
            }
        }

        /** Обновить позицию календаря на текущую дату. */
        object UpdateScrollToCurrentDay : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                fragment.getCalendarUpdateDelegate()?.updateScrollPosition()
                if (params.isFragment) {
                    val hostFragment = fragment.getParentFragment(params.isCompact)
                    hostFragment?.sendResult(
                        SbisPeriodPickerRange(null, null),
                        fragment.getRequestKey(),
                        fragment.getResultKey()
                    )
                }
            }
        }

        /** Догрузить календарь. */
        class ReloadCalendar(
            private val calendarStorage: CalendarStorage,
            private val isNextPart: Boolean
        ) : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                fragment.getCalendarUpdateDelegate()?.reloadCalendar(calendarStorage, isNextPart)
            }
        }

        /** Обновить выделение периода в календаре. */
        class UpdateSelection(
            private val calendarStorage: CalendarStorage
        ) : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                fragment.getCalendarUpdateDelegate()?.updateSelection(calendarStorage)
            }
        }

        /** Обновить выделение периода в календаре и послать результат выделения. */
        class UpdatePeriod(
            private val calendarStorage: CalendarStorage,
            private val range: SbisPeriodPickerRange
        ) : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                fragment.getCalendarUpdateDelegate()?.updateSelection(calendarStorage)

                val hostFragment = if (params.isFragment) fragment else fragment.getParentFragment(params.isCompact)
                hostFragment?.sendResult(range, fragment.getRequestKey(), fragment.getResultKey())
            }
        }

        /** Обновить календарь. */
        class UpdateCalendar(
            private val calendarStorage: CalendarStorage,
            private val startPeriod: Calendar,
            private val endPeriod: Calendar,
            private val mode: SbisPeriodPickerMode,
            private val scrollDate: Calendar? = null
        ) : Label {
            override fun handle(
                fragment: Fragment,
                params: LabelParams
            ) {
                val modeFragment = fragment.getCalendarUpdateDelegate()
                modeFragment?.updateSelection(calendarStorage)
                scrollDate?.let {
                    modeFragment?.updateScrollPosition(it)
                }

                params.hostStore?.let {
                    val tag = SbisPeriodPickerMode.getOppositeMode(mode.tag).tag
                    it.accept(HostIntent.UpdateSelection(tag, startPeriod, endPeriod))
                }
            }
        }
    }

    /**
     * Модель состояний.
     * @param calendarStorage хранилище данных для календаря.
     * @param startCalendar начальная дата календаря.
     * @param endCalendar конечная дата календаря.
     * @param startPeriod начало выбранного периода.
     * @param endPeriod конец выбранного периода.
     * @param counters счетчики по дням.
     * @param isSingleClick является ли режим выбора - выбор одного дня.
     * @param selectionType тип выделения периода.
     */
    @Parcelize
    data class State(
        val calendarStorage: CalendarStorage = CalendarStorage(),
        val startCalendar: Calendar? = null,
        val endCalendar: Calendar? = null,
        val startPeriod: Calendar? = null,
        val endPeriod: Calendar? = null,
        val counters: Map<Calendar, Int>? = null,
        val isSingleClick: Boolean = true,
        val selectionType: SelectionType = SelectionType.NO_SELECTION
    ) : Parcelable
}