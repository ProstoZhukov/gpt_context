package ru.tensor.sbis.design.message_panel.vm.usecase

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase

/**
 * Публичный API для управления сценариями работы панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelUseCaseApi {

    val useCase: StateFlow<AbstractMessagePanelUseCase>

    fun updateUseCase(buildUseCase: UpdateUseCaseFunction)
}
