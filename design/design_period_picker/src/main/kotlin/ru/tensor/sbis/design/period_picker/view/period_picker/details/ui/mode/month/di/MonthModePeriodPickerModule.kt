package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
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
internal class MonthModePeriodPickerModule {

    internal companion object {
        /** Начало периода. */
        internal const val START_PERIOD = "startPeriod"

        /** Конец периода. */
        internal const val END_PERIOD = "endPeriod"

        /** Тип выделения периода. */
        internal const val SELECTION_TYPE = "selectionType"

        /** Тип дня для отрисовки счётчика. */
        internal const val DAY_TYPE = "dayType"

        /** Доступный для отображения период. */
        internal const val DISPLAYED_RANGE = "displayedRange"

        /** Является ли компонент компактным выбором периода. */
        internal const val IS_COMPACT = "isCompact"

        /** Проверка доступности дня для взаимодействия. */
        internal const val IS_DAY_AVAILABLE = "isDayAvailable"

        /** Начало преустановленного периода. */
        internal const val PRESET_START_PERIOD = "presetStartPeriod"

        /** Конец преустановленного периода. */
        internal const val PRESET_END_PERIOD = "presetEndPeriod"

        /** Является ли компонент фрагментом. */
        internal const val IS_FRAGMENT = "isFragment"

        /** Проверка доступности дня для взаимодействия. */
        internal const val DAY_CUSTOM_THEME = "dayCustomTheme"

        /** Дата подскроллинга для автотестов. */
        internal const val ANCHOR_DATE = "anchorDate"

        /** Скроллируется ли календарь к низу. */
        internal const val IS_BOTTOM_POSITION = "isBottomPosition"

        /** Фабрика репозитория для предоставления счетчиков по дням. */
        internal const val DAY_COUNTERS_FACTORY = "dayCountersFactory"
    }

    @Provides
    @MonthModePeriodPickerScope
    fun provideStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }

    @Provides
    @MonthModePeriodPickerScope
    fun provideCalendarStorageRepository(): CalendarStorageRepository {
        return CalendarStorageRepository()
    }

    @Provides
    @MonthModePeriodPickerScope
    fun provideSbisCompactPeriodPickerStoreFactory(
        storeFactory: StoreFactory,
        repository: CalendarStorageRepository,
        @Named(START_PERIOD) startPeriod: Calendar?,
        @Named(END_PERIOD) endPeriod: Calendar?,
        @Named(SELECTION_TYPE) selectionType: SbisPeriodPickerSelectionType,
        @Named(DAY_TYPE) dayType: SbisPeriodPickerDayType,
        @Named(DISPLAYED_RANGE) displayedRange: SbisPeriodPickerRange,
        @Named(IS_COMPACT) isCompact: Boolean,
        @Named(IS_DAY_AVAILABLE) isDayAvailable: ((Calendar) -> Boolean)?,
        @Named(PRESET_START_PERIOD) presetStartPeriod: Calendar?,
        @Named(PRESET_END_PERIOD) presetEndPeriod: Calendar?,
        @Named(IS_FRAGMENT) isFragment: Boolean,
        @Named(DAY_CUSTOM_THEME) dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
        @Named(ANCHOR_DATE) anchorDate: Calendar?,
        @Named(IS_BOTTOM_POSITION) isBottomPosition: Boolean,
        @Named(DAY_COUNTERS_FACTORY) dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory?
    ): PeriodPickerStoreFactory =
        PeriodPickerStoreFactory(
            storeFactory,
            repository,
            startPeriod,
            endPeriod,
            presetStartPeriod,
            presetEndPeriod,
            selectionType,
            dayType,
            displayedRange,
            anchorDate,
            isBottomPosition,
            isCompact = isCompact,
            isFragment = isFragment,
            isDayAvailable = isDayAvailable,
            dayCustomTheme = dayCustomTheme,
            dayCountersFactory = dayCountersFactory
        )
}