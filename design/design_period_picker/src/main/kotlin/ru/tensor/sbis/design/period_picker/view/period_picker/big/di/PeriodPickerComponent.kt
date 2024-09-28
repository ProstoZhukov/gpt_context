package ru.tensor.sbis.design.period_picker.view.period_picker.big.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.PeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.IS_ENABLED
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.IS_ONE_DAY_SELECTION
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.MODE
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.PRESET_END_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.PRESET_START_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.REQUEST_KEY
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.RESULT_KEY
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.SELECTION_TYPE
import ru.tensor.sbis.design.period_picker.view.period_picker.big.di.PeriodPickerModule.Companion.START_PERIOD
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerController
import java.util.Calendar
import javax.inject.Named

/**
 * Di component Большого выбора периода.
 *
 * @author mb.kruglova
 */
@PeriodPickerScope
@Component(modules = [PeriodPickerModule::class])
internal interface PeriodPickerComponent {

    /** @SelfDocumented */
    fun injector(): Injector

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: PeriodPickerFragment,
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
            @Named(IS_ENABLED)
            isEnabled: Boolean,
            @BindsInstance
            @Named(IS_ONE_DAY_SELECTION)
            isOneDaySelection: Boolean,
            @BindsInstance
            @Named(IS_BOTTOM_POSITION)
            isBottomPosition: Boolean,
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
            @Named(MODE)
            mode: SbisPeriodPickerMode,
            @BindsInstance
            @Named(REQUEST_KEY)
            requestKey: String,
            @BindsInstance
            @Named(RESULT_KEY)
            resultKey: String
        ): PeriodPickerComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: PeriodPickerFragment): PeriodPickerController
    }

    companion object {
        internal fun resolveDependencies(parentFragment: Fragment?): PeriodPickerComponent =
            (parentFragment as PeriodPickerComponentHolder).diComponent
    }
}