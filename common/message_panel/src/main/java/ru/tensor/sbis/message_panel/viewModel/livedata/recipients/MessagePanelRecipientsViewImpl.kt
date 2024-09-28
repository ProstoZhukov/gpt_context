package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.message_panel.helper.ConversationRecipientsChecker
import ru.tensor.sbis.message_panel.helper.MessagePanelMentionFeature
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import java.util.*

internal const val MAX_RECIPIENTS_COUNT = 300

/**
 * @author vv.chekurda
 */
internal class MessagePanelRecipientsViewImpl(
    private val viewModel: MessagePanelViewModel<*, *, *>,
    private val selectionConfigFactory: RecipientSelectionConfigFactory = DefaultSelectionConfigFactory(viewModel),
    private val selectionMenuConfigFactory: RecipientSelectionMenuConfigFactory =
        DefaultSelectionMenuConfigFactory(selectionConfigFactory),
    private val observeOn: Scheduler
) : MessagePanelRecipientsView {

    private val recipientsPanelVisibility: Subject<Boolean> = BehaviorSubject.createDefault(true)
    private val forceHideRecipientsPanel: Subject<Boolean> = BehaviorSubject.createDefault(false)
    private val recipientRequestCount: Subject<Int> = PublishSubject.create()
    private val selectionMenuVisibility: Subject<Boolean> = BehaviorSubject.createDefault(false)
    private val recipientSelectionMenuRequestCount: Subject<Int> = PublishSubject.create()
    private val conversationRecipientsChecker by lazy {
        ConversationRecipientsChecker(viewModel.recipientsInteractor!!)
    }

    override val recipientSelectionScreen: Observable<RecipientSelectionConfig> by lazy {
        recipientRequestCount.withLatestFrom(
            viewModel.liveData.document,
            viewModel.liveData.conversationUuid,
            selectionConfigFactory
        )
    }

    override val recipientSelectionMenu: Observable<RecipientSelectionMenuConfig> by lazy {
        recipientSelectionMenuRequestCount.withLatestFrom(
            viewModel.liveData.document,
            viewModel.liveData.conversationUuid,
            selectionMenuConfigFactory
        )
    }

    override val recipientSelectionMenuVisibility: Observable<Boolean> = selectionMenuVisibility

    override val recipientsVisibility: Observable<Boolean> by lazy {
        Observable.combineLatest(
            viewModel.liveData.recipientsFeatureEnabled,
            recipientsPanelVisibility,
            forceHideRecipientsPanel,
            viewModel.liveData.hasSpaceForRecipients
        ) { featureEnabled, visible, hide, hasSpace -> featureEnabled && visible && !hide && hasSpace }
    }

    override val recipientsViewData: Observable<RecipientsViewData> by lazy {
        val argumentCombiner = Observable.combineLatest(
            viewModel.liveData.conversationUuid,
            viewModel.liveData.recipients,
            viewModel.liveData.isRecipientsHintEnabled,
            viewModel.liveData.requireCheckAllMembers
        ) { conversationUuid: RxContainer<UUID?>,
            recipients: List<RecipientItem>,
            isRecipientsHintEnabled: Boolean,
            requireCheckAllMembers: Boolean ->
            RecipientsArguments(
                conversationUuid.value,
                recipients,
                isRecipientsHintEnabled,
                requireCheckAllMembers
            )
        }

        argumentCombiner
            .observeOn(Schedulers.io())
            .distinctUntilChanged()
            /*
            Эта реализация MessagePanelRecipientsView создаётся только когда поддерживается выбор получателей.
            Если выбор получателей не доступен, создаётся MessagePanelRecipientsViewStub
             */
            .map { arguments ->
                val recipients = if (arguments.requireCheckAllMembers) {
                    conversationRecipientsChecker.apply(arguments.conversationUuid to arguments.recipients)
                } else {
                    arguments.recipients
                }
                RecipientsViewData(recipients, arguments.isHintEnabled)
            }
            .observeOn(observeOn)
    }

    override fun setRecipientsPanelVisibility(isVisible: Boolean) {
        recipientsPanelVisibility.onNext(isVisible)
    }

    override fun forceHideRecipientsPanel(hide: Boolean) {
        forceHideRecipientsPanel.onNext(hide)
    }

    override fun requestRecipientsSelection() {
        recipientRequestCount.onNext(viewModel.conversationInfo.recipientsLimit)
    }

    override fun requestRecipientSelectionMenu() {
        !MessagePanelMentionFeature.isActive && return

        recipientSelectionMenuRequestCount.onNext(viewModel.conversationInfo.recipientsLimit)
    }

    override fun changeRecipientSelectionMenuVisibility(isVisible: Boolean) {
        TODO("Not yet implemented")
    }

    /**
     * Модель аргументов по получателям для комбинирования данных.
     *
     * @property conversationUuid идентификатор переписки.
     * @property recipients список получателей получателей.
     * @property isHintEnabled true, если для пустого списка получателей доступна подсказка.
     * @property requireCheckAllMembers true, если необходима проверка списка получателей на полное совпадение
     * со списком участников переписки. Например, данная механика используется в реестре сообщений,
     * чтобы отобразить соответствующий текст в панели получателей.
     */
    private data class RecipientsArguments(
        val conversationUuid: UUID?,
        val recipients: List<RecipientItem>,
        val isHintEnabled: Boolean,
        val requireCheckAllMembers: Boolean
    )
}