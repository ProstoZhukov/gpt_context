package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStoreFactory
import java.util.Calendar
import javax.inject.Named

/**
 * @author mb.kruglova
 */
@Module
internal class YearModePeriodPickerModule {

    internal companion object {
        /** Начало периода. */
        internal const val START_PERIOD = "startPeriod"

        /** Конец периода. */
        internal const val END_PERIOD = "endPeriod"

        /** Тип выделения периода. */
        internal const val SELECTION_TYPE = "selectionType"

        /** Доступный для отображения период. */
        internal const val DISPLAYED_RANGE = "displayedRange"

        /** Начало преустановленного периода. */
        internal const val PRESET_START_PERIOD = "presetStartPeriod"

        /** Конец преустановленного периода. */
        internal const val PRESET_END_PERIOD = "presetEndPeriod"

        /** Дата подскроллинга для автотестов. */
        internal const val ANCHOR_DATE = "anchorDate"

        /** Скроллируется ли календарь к низу. */
        internal const val IS_BOTTOM_POSITION = "isBottomPosition"
    }

    @Provides
    @YearModePeriodPickerScope
    fun provideStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }

    @Provides
    @YearModePeriodPickerScope
    fun provideCalendarStorageRepository(): CalendarStorageRepository {
        return CalendarStorageRepository()
    }

    @Provides
    @YearModePeriodPickerScope
    fun providePeriodPickerStoreFactory(
        storeFactory: StoreFactory,
        repository: CalendarStorageRepository,
        @Named(START_PERIOD) startPeriod: Calendar?,
        @Named(END_PERIOD) endPeriod: Calendar?,
        @Named(SELECTION_TYPE) selectionType: SbisPeriodPickerSelectionType,
        @Named(DISPLAYED_RANGE) displayedRange: SbisPeriodPickerRange,
        @Named(PRESET_START_PERIOD) presetStartPeriod: Calendar?,
        @Named(PRESET_END_PERIOD) presetEndPeriod: Calendar?,
        @Named(ANCHOR_DATE) anchorDate: Calendar?,
        @Named(IS_BOTTOM_POSITION) isBottomPosition: Boolean
    ): PeriodPickerStoreFactory =
        PeriodPickerStoreFactory(
            storeFactory,
            repository,
            startPeriod,
            endPeriod,
            presetStartPeriod,
            presetEndPeriod,
            selectionType,
            SbisPeriodPickerDayType.Simple,
            displayedRange,
            anchorDate,
            isBottomPosition
        )
}