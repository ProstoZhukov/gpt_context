package ru.tensor.sbis.design.message_panel.domain.common

import ru.tensor.sbis.design.message_panel.decl.message.MessageService
import ru.tensor.sbis.design.message_panel.decl.message.MessageServiceHelper
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel

/**
 * Общая механика отправки сообщения
 *
 * @author ma.kolpakov
 */
internal class SendUseCase<MESSAGE, RESULT>(
    private val parentUseCase: AbstractMessagePanelUseCase,
    private val messageService: MessageService<MESSAGE, RESULT>,
    private val messageServiceHelper: MessageServiceHelper<MESSAGE, RESULT>,
    private val vm: MessagePanelViewModel
) {

    suspend operator fun invoke() {
        val result = messageService.send(parentUseCase, vm.text.value, vm.recipientsUuid.value)
        if (messageServiceHelper.isResultError(result)) {
            vm.showToast(messageServiceHelper.getResultError(result))
        } else {
            vm.setText("")
            vm.onAttachmentsClearClicked()
        }
    }
}
