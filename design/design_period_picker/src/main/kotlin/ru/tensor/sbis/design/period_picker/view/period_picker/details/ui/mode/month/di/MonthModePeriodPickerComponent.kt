package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di

import android.view.View
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerComponent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerController
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.DAY_CUSTOM_THEME
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.DAY_COUNTERS_FACTORY
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.DAY_TYPE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.IS_COMPACT
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.IS_DAY_AVAILABLE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.IS_FRAGMENT
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.PRESET_END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.PRESET_START_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.di.MonthModePeriodPickerModule.Companion.START_PERIOD
import java.util.Calendar
import javax.inject.Named

/**
 * @author mb.kruglova
 */
@MonthModePeriodPickerScope
@Component(modules = [MonthModePeriodPickerModule::class])
internal interface MonthModePeriodPickerComponent {

    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance viewFactory: (View) -> MonthModePeriodPickerView,
            @BindsInstance
            @Named(START_PERIOD)
            startPeriod: Calendar?,
            @BindsInstance
            @Named(END_PERIOD)
            endPeriod: Calendar?,
            @BindsInstance
            @Named(SELECTION_TYPE)
            selectionType: SbisPeriodPickerSelectionType,
            @BindsInstance
            @Named(DAY_TYPE)
            dayType: SbisPeriodPickerDayType,
            @BindsInstance
            @Named(DISPLAYED_RANGE)
            displayedRange: SbisPeriodPickerRange,
            @BindsInstance
            @Named(IS_COMPACT)
            isCompact: Boolean,
            @BindsInstance
            @Named(IS_DAY_AVAILABLE)
            isDayAvailable: ((Calendar) -> Boolean)?,
            @BindsInstance
            @Named(PRESET_START_PERIOD)
            presetStartPeriod: Calendar?,
            @BindsInstance
            @Named(PRESET_END_PERIOD)
            presetEndPeriod: Calendar?,
            @BindsInstance
            @Named(IS_FRAGMENT)
            isFragment: Boolean,
            @BindsInstance
            @Named(DAY_CUSTOM_THEME)
            dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
            @BindsInstance
            @Named(ANCHOR_DATE)
            anchorDate: Calendar?,
            @BindsInstance
            @Named(IS_BOTTOM_POSITION)
            isBottomPosition: Boolean,
            @BindsInstance
            @Named(DAY_COUNTERS_FACTORY)
            dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory?,
            @BindsInstance parentComponent: PeriodPickerComponent?
        ): MonthModePeriodPickerComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: MonthModePeriodPickerFragment): MonthModePeriodPickerController
    }
}