package ru.tensor.sbis.design.message_panel.vm.usecase

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase

/**
 * Внутренний API для управления сценариями работы панели ввода
 *
 * @author ma.kolpakov
 */
internal interface UseCaseDelegate : MessagePanelUseCaseApi {

    override val useCase: StateFlow<AbstractMessagePanelUseCase>

    fun setUseCase(newUseCase: AbstractMessagePanelUseCase)
}
