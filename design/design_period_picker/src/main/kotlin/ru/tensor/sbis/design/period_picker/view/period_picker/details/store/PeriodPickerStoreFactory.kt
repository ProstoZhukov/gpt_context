package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType.Marked
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMarkedDayType.COUNTER
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMarkedDayType.DOT
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import java.util.Calendar
import javax.inject.Inject

/**
 * Отвечает за генерацию изменение состояния интерфейса или источника данных
 * в ответ на намерение от интерфейса или источника данных.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val repository: CalendarStorageRepository,
    private val startSelectionDate: Calendar?,
    private val endSelectionDate: Calendar?,
    private val presetStartDate: Calendar?,
    private val presetEndDate: Calendar?,
    private val selectionType: SbisPeriodPickerSelectionType,
    private val dayType: SbisPeriodPickerDayType,
    private val displayedRange: SbisPeriodPickerRange,
    private val anchorDate: Calendar?,
    private val isBottomPosition: Boolean,
    private val isCompact: Boolean = false,
    private val isFragment: Boolean = false,
    private val isDayAvailable: ((Calendar) -> Boolean)? = null,
    private val dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme) = { SbisPeriodPickerDayCustomTheme() },
    private val dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory? = null
) {

    /** @SelfDocumented */
    internal fun create(stateKeeper: StateKeeper): PeriodPickerStore {
        val markerType = getMarkerType(dayType)
        val actions = listOf(
            Action.LoadCalendarStorage(
                displayedRange,
                markerType,
                isCompact,
                isDayAvailable,
                dayCustomTheme,
                anchorDate,
                isBottomPosition
            ),
            Action.InitCounters
        )
        return PeriodPickerStoreImpl(
            storeFactory,
            repository,
            stateKeeper,
            actions,
            selectionType,
            markerType,
            isCompact,
            dayCountersFactory?.createSbisPeriodPickerDayCountersRepository(),
            displayedRange,
            startSelectionDate,
            endSelectionDate,
            presetStartDate,
            presetEndDate,
            isDayAvailable,
            dayCustomTheme,
            isFragment
        )
    }

    private fun getMarkerType(dayType: SbisPeriodPickerDayType): MarkerType {
        return when {
            dayType is Marked && dayType.markedDayType == DOT -> MarkerType.DOT
            dayType is Marked && dayType.markedDayType == COUNTER -> MarkerType.COUNTER
            else -> MarkerType.NO_MARKER
        }
    }
}