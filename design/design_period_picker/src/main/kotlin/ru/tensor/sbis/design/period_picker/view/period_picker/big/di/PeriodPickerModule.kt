package ru.tensor.sbis.design.period_picker.view.period_picker.big.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStoreFactory
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerRouter
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerViewImpl
import java.util.Calendar
import javax.inject.Named

/**
 * Di module Большого выбора периода.
 *
 * @author mb.kruglova
 */
@Module
internal class PeriodPickerModule {

    companion object {

        /** Начало периода. */
        internal const val START_PERIOD = "startPeriod"

        /** Конец периода. */
        internal const val END_PERIOD = "endPeriod"

        /** Доступный для отображения период. */
        internal const val DISPLAYED_RANGE = "displayedRange"

        /** Является ли компонент доступным для взаимодействия. */
        internal const val IS_ENABLED = "isEnabled"

        /** Нужно ли позиционировать текущую даты или исходный выбранный период снизу. */
        internal const val IS_BOTTOM_POSITION = "isBottomPosition"

        /** Начало предустановленного периода. */
        internal const val PRESET_START_PERIOD = "presetStartPeriod"

        /** Конец предустановленного периода. */
        internal const val PRESET_END_PERIOD = "presetEndPeriod"

        /** Дата, к которой подскроллится календарь. */
        internal const val ANCHOR_DATE = "anchorDate"

        /** Тип выделения периода. */
        internal const val SELECTION_TYPE = "selectionType"

        /** Выделение одного дня. */
        internal const val IS_ONE_DAY_SELECTION = "isOneDaySelection"

        /** Ключ запроса для получения выбранного периода. */
        internal const val REQUEST_KEY = "requestKey"

        /** Ключ результата для получения выбранного периода. */
        internal const val RESULT_KEY = "resultKey"

        internal const val MODE = "mode"
    }

    @Provides
    @PeriodPickerScope
    fun providePeriodPickerViewFactory(
        @Named(IS_ONE_DAY_SELECTION) isOneDaySelection: Boolean,
        @Named(SELECTION_TYPE) selectionType: SbisPeriodPickerSelectionType,
        @Named(DISPLAYED_RANGE) displayedRange: SbisPeriodPickerRange
    ): PeriodPickerView.Factory =
        PeriodPickerView.Factory {
            PeriodPickerViewImpl(
                it,
                selectionType == SbisPeriodPickerSelectionType.Single,
                isOneDaySelection,
                displayedRange
            )
        }

    @Provides
    @PeriodPickerScope
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    @Provides
    @PeriodPickerScope
    fun providePeriodPickerStoreFactory(
        storeFactory: StoreFactory,
        @Named(START_PERIOD) startPeriod: Calendar?,
        @Named(END_PERIOD) endPeriod: Calendar?,
        @Named(PRESET_START_PERIOD) presetStartPeriod: Calendar?,
        @Named(PRESET_END_PERIOD) presetEndPeriod: Calendar?,
        @Named(MODE) mode: SbisPeriodPickerMode
    ) = PeriodPickerStoreFactory(storeFactory, startPeriod, endPeriod, presetStartPeriod, presetEndPeriod, mode)

    @Provides
    @PeriodPickerScope
    fun providePeriodPickerRouter(
        @Named(IS_ENABLED) isEnabled: Boolean,
        @Named(SELECTION_TYPE) selectionType: SbisPeriodPickerSelectionType,
        @Named(DISPLAYED_RANGE) displayedRange: SbisPeriodPickerRange,
        @Named(IS_BOTTOM_POSITION) isBottomPosition: Boolean,
        @Named(PRESET_START_PERIOD) presetStartPeriod: Calendar?,
        @Named(PRESET_END_PERIOD) presetEndPeriod: Calendar?,
        @Named(ANCHOR_DATE) anchorDate: Calendar?,
        @Named(REQUEST_KEY) requestKey: String,
        @Named(RESULT_KEY) resultKey: String
    ) = PeriodPickerRouter(
        isEnabled,
        selectionType,
        displayedRange,
        isBottomPosition,
        presetStartPeriod,
        presetEndPeriod,
        anchorDate,
        requestKey,
        resultKey
    )
}