package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State

/**
 * Класс, который принимает предыдущее состояние и возвращает следующее состояние (новую версию предыдущего).
 *
 * @author mb.kruglova
 */
internal class Reducer : Reducer<State, Message> {
    override fun State.reduce(msg: Message): State = msg.reduce(this)
}