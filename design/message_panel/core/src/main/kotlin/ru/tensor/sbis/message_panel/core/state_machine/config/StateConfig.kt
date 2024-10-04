package ru.tensor.sbis.message_panel.core.state_machine.config

import androidx.annotation.CheckResult
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
interface StateConfig<out VM : MessagePanelViewModel, in STATE : AbstractMessagePanelState<VM>> {

    @CheckResult
    fun apply(state: STATE): Disposable
}