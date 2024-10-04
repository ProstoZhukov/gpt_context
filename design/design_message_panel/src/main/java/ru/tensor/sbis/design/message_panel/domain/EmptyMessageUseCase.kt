package ru.tensor.sbis.design.message_panel.domain

import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.utils.errorSafe
import java.util.*

/**
 * Заглушка для инициализации панели ввода. Стартовое состояние, без специфичной механики
 * гарантирует отсутствие вызовов к микросервисам. Вызовы микросервиса с этим use case можно
 * обрабатывать как ошибку
 *
 * @author ma.kolpakov
 */
object EmptyMessageUseCase : AbstractMessagePanelUseCase(
    MessagePanelEnvironmentModel(UUID(0, 0), null, null)
) {
    override suspend fun setup(vm: MessagePanelViewModel) = Unit

    override suspend fun send() {
        errorSafe("Message panel is not initialized. You need setup use case first")
    }
}