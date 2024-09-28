/**
 * Вспомогательные инструменты для поддержки опционального MessagePanelRecipientsInteractor
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.message_panel.interactor.recipients

import io.reactivex.Observable
import io.reactivex.Scheduler
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsView
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsViewImpl

private const val RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED =
    "This action is not supported without MessagePanelRecipientsInteractor. " +
            "You need to provide dependencies on EmployeeProfileControllerWrapper and RecipientsController. " +
            "If you've got this message with optional dependencies, please report a bug"

/**
 * Выполняет [block] с требованием интерактора. Применять на сценариях, которые нельзя выполнять опционально.
 * Например, явный запрос получателя. Сценарий должен запрещаться со стороны UI и программной логики
 */
internal inline fun MessagePanelViewModel<*, *, *>.requireRecipientInteractor(
    block: (interactor: MessagePanelRecipientsInteractor) -> Unit
) {
    checkNotNullSafe(recipientsInteractor) { RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED }?.apply(block)
}

/**
 * Выполняет [block], если доступен интерактор. Применять на сценариях, которые можно безопасно игнорировать.
 * Например, подписки на запросы получателей (нет интерактора - нет подписки)
 */
internal inline fun MessagePanelViewModel<*, *, *>.optionalRecipientInteractor(
    block: (interactor: MessagePanelRecipientsInteractor) -> Unit
) {
    recipientsInteractor?.apply(block)
}

/**
 * Создание реализации [MessagePanelRecipientsView] под сценарии с [MessagePanelRecipientsInteractor] и без него
 */
internal fun MessagePanelViewModel<*, *, *>.createMessagePanelRecipientsView(observeOn: Scheduler) =
    recipientsInteractor?.let { MessagePanelRecipientsViewImpl(this, observeOn = observeOn) }
        ?: MessagePanelRecipientsViewStub()

private class MessagePanelRecipientsViewStub : MessagePanelRecipientsView {

    override val recipientsVisibility: Observable<Boolean> =
        Observable.just(false)

    override val recipientsViewData: Observable<RecipientsViewData> =
        Observable.empty()

    override val recipientSelectionScreen: Observable<RecipientSelectionConfig> =
        Observable.empty()

    override val recipientSelectionMenu: Observable<RecipientSelectionMenuConfig> =
        Observable.empty()

    override val recipientSelectionMenuVisibility: Observable<Boolean> =
        Observable.empty()

    override fun forceHideRecipientsPanel(hide: Boolean) = Unit

    override fun setRecipientsPanelVisibility(isVisible: Boolean) =
        error(RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED)

    override fun requestRecipientsSelection() =
        error(RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED)

    override fun requestRecipientSelectionMenu() =
        error(RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED)

    override fun changeRecipientSelectionMenuVisibility(isVisible: Boolean) =
        error(RECIPIENTS_INTERACTOR_DEPENDENCY_IS_NOT_PROVIDED)
}