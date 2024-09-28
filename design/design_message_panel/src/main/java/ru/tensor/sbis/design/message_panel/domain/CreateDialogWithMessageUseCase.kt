package ru.tensor.sbis.design.message_panel.domain

import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import kotlin.coroutines.CoroutineContext

/**
 * @author ma.kolpakov
 */
internal class CreateDialogWithMessageUseCase internal constructor(
    environment: MessagePanelEnvironmentModel,
    useCaseComponent: MessagePanelUseCaseComponent.Factory,
    dispatcher: CoroutineContext = Dispatchers.IO
) : SendMessageUseCase(environment, useCaseComponent, dispatcher) {

    private lateinit var vm: MessagePanelViewModel

    override suspend fun setup(vm: MessagePanelViewModel) {
        super.setup(vm)
        this.vm = vm
        vm.setNewDialog(true)
    }

    override suspend fun send() {
        super.send()
        vm.setNewDialog(false)
    }
}