/**
 * Вспомогательные инструменты для тестирования MessagePanelLiveData
 *
 * @author vv.chekurda
 * Создан 8/16/2019
 */
package ru.tensor.sbis.message_panel.viewModel.livedata

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachine

fun initLiveData(
    vm: MessagePanelViewModel<*, *, *> = mock(),
    recipientInteractor: MessagePanelRecipientsInteractor = mock(),
    attachmentHelper: MessagePanelAttachmentHelper = mock(),
    resourceProvider: ResourceProvider = mock(),
    info: CoreConversationInfo = CoreConversationInfo(),
    isEnabled: Flowable<Boolean> = Flowable.just(true),
    isSending: Flowable<Boolean> = Flowable.empty(),
    isQuoting: Flowable<Boolean> = Flowable.empty(),
    isEditing: Flowable<Boolean> = Flowable.empty()
): MessagePanelLiveData {
    whenever(vm.recipientsInteractor).thenReturn(recipientInteractor)
    whenever(vm.attachmentPresenter).thenReturn(attachmentHelper)
    whenever(vm.resourceProvider).thenReturn(resourceProvider)

    val machine: MessagePanelStateMachine<*, *, *> = mock()
    whenever(machine.isEnabled).thenReturn(isEnabled)
    whenever(machine.isSending).thenReturn(isSending)
    whenever(machine.isQuoting).thenReturn(isQuoting)
    whenever(machine.isEditing).thenReturn(isEditing)
    whenever(vm.stateMachine).thenReturn(machine)
    whenever(vm.conversationInfo).thenReturn(info)
    return MessagePanelLiveDataImpl(vm, mock(), Schedulers.single())
}