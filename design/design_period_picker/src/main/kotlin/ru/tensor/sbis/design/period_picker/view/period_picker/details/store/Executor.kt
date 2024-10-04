package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Job
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.period_picker.details.model.IntentParams
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State
import java.util.Calendar

/**
 * Исполнитель бизнес-логики.
 *
 * @author mb.kruglova
 */
internal class Executor(
    private val repository: CalendarStorageRepository,
    private val markerType: MarkerType,
    private val isCompact: Boolean,
    private val dayCountersRepository: SbisPeriodPickerDayCountersRepository?,
    private val displayedRange: SbisPeriodPickerRange,
    private val isDayAvailable: ((Calendar) -> Boolean)?,
    private val isFragment: Boolean,
    private val dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    internal var countersJob: Job? = null
    internal var reloadingJob: Job? = null

    override fun executeAction(action: Action, getState: () -> State) {
        action.handle(this, scope, getState, repository, dayCountersRepository)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        intent.handle(
            this,
            scope,
            getState(),
            IntentParams(
                repository,
                markerType,
                isCompact,
                dayCountersRepository,
                displayedRange,
                isDayAvailable,
                isFragment,
                dayCustomTheme
            )
        )
    }

    /** @SelfDocumented */
    fun dispatchMessage(message: Message) = dispatch(message)

    /** @SelfDocumented */
    fun publishLabel(label: Label) = publish(label)
}