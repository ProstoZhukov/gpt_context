package ru.tensor.sbis.message_panel.core.state_machine.state.config

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import ru.tensor.sbis.message_panel.core.state_machine.config.StateConfig
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventEnable
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventRecipients
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventUserInputClear
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.core.state_machine.state.observeUserChangesClear
import ru.tensor.sbis.message_panel.declaration.data.ShareContent
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class SimpleSendStateConfig(
    private val sharedContent: ShareContent?
) : StateConfig<MessagePanelViewModel, AbstractMessagePanelState<MessagePanelViewModel>> {

    override fun apply(state: AbstractMessagePanelState<MessagePanelViewModel>): Disposable {
        val disposable = CompositeDisposable()

        state.addOnSetAction { sharedContent?.let(::setupSharedContent) }
        state.addOnSetAction { disposable.add(observeUserChangesClear(liveData) { state.fire(EventUserInputClear) }) }

        state.event(EventRecipients::class) { disposable.add(handleRecipientsEvent(it)) }
        state.event(EventUserInputClear::class) { state.fire(CleanStateEvent(false)) }
        state.event(EventSign::class) { state.fire(SendingSignMessageEvent(it.action)) }
        state.event(EventEnable::class) {
            state.vm.resetRecipients()
            state.vm.resetConversationInfo()
        }

        return disposable
    }

    private fun setupSharedContent(content: ShareContent) {
        liveData.setMessageText(content.text)
        attachmentPresenter.onFilesAttached(content.fileUriList)
    }

    /**
     * Подставляем получателей, если:
     * - они были выбраны пользователем [EventRecipients.isUserSelected]
     * - панель находится в режиме ммода нового сообщения. Допущение о том, что в этом режиме получатели не
     * устанавливаются в режиме "живой переписки"
     */
    private fun handleRecipientsEvent(event: EventRecipients): Disposable = Single.zip(
        Single.just(event.isUserSelected),
        liveData.newDialogModeEnabled.firstOrError(),
        BiFunction<Boolean, Boolean, Boolean> { byUser, newMessageMode ->
            byUser || newMessageMode
        })
        .filter { it }
        .subscribe {
            viewModel.loadRecipients(event.recipients, event.isUserSelected)
        }
}