package ru.tensor.sbis.design.message_panel.vm.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.domain.EmptyMessageUseCase
import ru.tensor.sbis.design.message_panel.domain.MessagePanelUseCaseBuilder

/**
 * @author ma.kolpakov
 */
internal class UseCaseDelegateImpl(
    private val useCaseBuilder: MessagePanelUseCaseBuilder
) : UseCaseDelegate {

    override val useCase = MutableStateFlow<AbstractMessagePanelUseCase>(EmptyMessageUseCase)

    override fun setUseCase(newUseCase: AbstractMessagePanelUseCase) {
        useCase.value = newUseCase
    }

    override fun updateUseCase(buildUseCase: UpdateUseCaseFunction) {
        useCase.value = useCaseBuilder.buildUseCase(useCase.value)
    }
}
