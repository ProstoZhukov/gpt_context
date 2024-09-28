package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import java.util.Calendar
import javax.inject.Inject

/**
 * Класс отвечает за генерацию изменение состояния интерфейса или источника данных
 * в ответ на намерение от интерфейса или источника данных.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val startSelectionDate: Calendar?,
    private val endSelectionDate: Calendar?,
    private val presetStartDate: Calendar?,
    private val presetEndDate: Calendar?,
    private val mode: SbisPeriodPickerMode
) {
    /** @SelfDocumented */
    internal fun create(stateKeeper: StateKeeper): PeriodPickerStore {
        return PeriodPickerStoreImpl(
            storeFactory,
            stateKeeper,
            startSelectionDate,
            endSelectionDate,
            presetStartDate,
            presetEndDate,
            mode
        )
    }
}