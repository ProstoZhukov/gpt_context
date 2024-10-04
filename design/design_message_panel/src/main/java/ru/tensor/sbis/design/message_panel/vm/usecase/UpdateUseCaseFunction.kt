package ru.tensor.sbis.design.message_panel.vm.usecase

import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.domain.MessagePanelUseCaseBuilder

/**
 * Псевдоним функции для обновления [AbstractMessagePanelUseCase]
 *
 * @author ma.kolpakov
 */
typealias UpdateUseCaseFunction =
        MessagePanelUseCaseBuilder.(useCase: AbstractMessagePanelUseCase) -> AbstractMessagePanelUseCase