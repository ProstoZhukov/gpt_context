package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State

/**
 * Исполнитель бизнес-логики.
 *
 * @author mb.kruglova
 */
internal class Executor :
    CoroutineExecutor<Intent, Any, State, Message, Label>() {

    override fun executeIntent(intent: Intent, getState: () -> State) {
        intent.handle(this, getState())
    }

    /** @SelfDocumented */
    fun dispatchMessage(message: Message) = dispatch(message)

    /** @SelfDocumented */
    fun publishLabel(label: Label) = publish(label)
}