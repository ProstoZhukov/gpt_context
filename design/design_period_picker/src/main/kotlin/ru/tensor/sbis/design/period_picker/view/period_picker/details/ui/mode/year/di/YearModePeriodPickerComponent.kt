package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di

import android.view.View
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerController
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.PRESET_END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.PRESET_START_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.di.YearModePeriodPickerModule.Companion.START_PERIOD
import java.util.Calendar
import javax.inject.Named

/**
 * @author mb.kruglova
 */
@YearModePeriodPickerScope
@Component(modules = [YearModePeriodPickerModule::class])
internal interface YearModePeriodPickerComponent {
    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance viewFactory: (View) -> YearModePeriodPickerView,
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
            @Named(DISPLAYED_RANGE)
            displayedRange: SbisPeriodPickerRange,
            @BindsInstance
            @Named(PRESET_START_PERIOD)
            presetStartPeriod: Calendar?,
            @BindsInstance
            @Named(PRESET_END_PERIOD)
            presetEndPeriod: Calendar?,
            @BindsInstance
            @Named(ANCHOR_DATE)
            anchorDate: Calendar?,
            @BindsInstance
            @Named(IS_BOTTOM_POSITION)
            isBottomPosition: Boolean
        ): YearModePeriodPickerComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: YearModePeriodPickerFragment): YearModePeriodPickerController
    }
}