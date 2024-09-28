package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.toolbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.internal.functions.Functions
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.toolbar.BaseConversationToolbarPresenter
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationDataDispatcher
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationState
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationToolbarView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option.CRMConversationOption
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.communicator.crm.conversation.router.CRMConversationRouter
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.CreateConsultationButtonPosition
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.networkUtilsFeatureProvider
import ru.tensor.sbis.communicator.crm.conversation.review.ConversationReviewEvent
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.SbisMobileIcon
import timber.log.Timber

/**
 * Реализация делегата презентера по тулбару чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationToolbarPresenter(
    interactor: CRMConversationInteractor,
    coreConversationInfo: CRMCoreConversationInfo,
    dataDispatcher: CRMConversationDataDispatcher,
    private val viewModel: CRMConversationViewModel?,
    private val router: CRMConversationRouter
) : BaseConversationToolbarPresenter<
        CRMConversationToolbarView, CRMConversationInteractor, CRMConversationMessage,
        CRMConversationState, CRMConversationData, CRMCoreConversationInfo, CRMConversationDataDispatcher
        >(interactor, coreConversationInfo, dataDispatcher),
    CRMConversationToolbarPresenterContract<CRMConversationToolbarView> {

    private var unreadChatsMessagesCounter = 0
    private var createConsultationButton: CreateConsultationButtonPosition? = null

    init {
        subscribeOnCounterUpdates()
        presetToolbarData()
    }

    override fun handleConversationDataChanges(conversationData: CRMConversationData) {
        super.handleConversationDataChanges(conversationData)

        conversationData.also {
            this.unreadChatsMessagesCounter = it.unreadChatsMessagesCounter
            this.createConsultationButton = it.createConsultationButton
            viewModel?.showConsultationMenuIcon?.onNext(!it.isHideMenu)
            coreConversationInfo.apply {
                sourceId = it.sourceId
                allowedMenuOptions = it.allowedActions
                operatorId = it.operatorId
                authorId = it.authorId
                consultationChannel = it.channel
            }
        }
    }

    /** @SelfDocumented */
    override fun displayViewState(view: CRMConversationToolbarView) {
        super.displayViewState(view)
        val isTablet = (mView as? Fragment)?.isTablet

        // Если менять значение счетчика в планшетной верстке, то он будет отображаться, чтобы
        // этого избежать, обновляем значение счетчика только в мобильном варианте.
        if (unreadChatsMessagesCounter != 0 && isTablet == false) {
            viewModel?.msgUnreadCounter?.onNext(unreadChatsMessagesCounter)
        }

        createConsultationButton?.also {
            when (it) {
                CreateConsultationButtonPosition.NONE -> {
                    viewModel?.showCreateConsultationIcon?.onNext(false)
                    viewModel?.showCreateConsultationFab?.onNext(false)
                }
                CreateConsultationButtonPosition.HEAD -> {
                    viewModel?.showCreateConsultationIcon?.onNext(true)
                    viewModel?.showCreateConsultationFab?.onNext(false)
                }
                CreateConsultationButtonPosition.BODY -> {
                    viewModel?.showCreateConsultationFab?.onNext(true)
                    viewModel?.showCreateConsultationIcon?.onNext(false)
                }
            }
        }
    }

    private fun presetToolbarData() {
        if (toolbarData != null) return
        with(coreConversationInfo) {
            toolbarData = ToolbarData(
                photoDataList = listOf(PersonData(null, photoUrl)),
                title = chatName.orEmpty(),
                showOnlyTitle = true,
                isChat = true
            )
        }
    }

    private fun subscribeOnCounterUpdates() {
        interactor.subscribeOnCounterUpdates()
            .subscribe {
                val (chatUuid, counter) = it
                if (coreConversationInfo.conversationUuid == chatUuid) {
                    viewModel?.msgUnreadCounter?.onNext(counter)
                }
            }
            .storeIn(compositeDisposable)
    }

    override fun openCRMConversationMenu() = DebounceActionHandler.INSTANCE.handle {
        val options = getCRMConversationMenuOptions()
        mView?.showCRMConversationMenu(options)
    }

    private fun getCRMConversationMenuOptions(): List<CRMConversationOption> {
        val options = coreConversationInfo.allowedMenuOptions
        return buildList {
            options?.let {
                if (options.contains(ConsultationActionsFlags.CAN_REASSIGN_TO_QUEUE)) add(CRMConversationOption.REASSIGN_TO_QUEUE)
                if (options.contains(ConsultationActionsFlags.CAN_REASSIGN_TO_GROUP)) add(CRMConversationOption.REASSIGN_TO_GROUP)
                if (options.contains(ConsultationActionsFlags.CAN_REASSIGN_TO_OPERATOR)) add(CRMConversationOption.REASSIGN_TO_OPERATOR)
                add(CRMConversationOption.COPY_LINK)
                if (options.contains(ConsultationActionsFlags.CAN_CREATE_NEW)) add(CRMConversationOption.WRITE_TO_CLIENT)
                if (options.contains(ConsultationActionsFlags.CAN_REQUEST_CONTACTS)) add(CRMConversationOption.REQUEST_CONTACTS)
                if (options.contains(ConsultationActionsFlags.CAN_CLOSE)) add(CRMConversationOption.COMPLETE)
                if (options.contains(ConsultationActionsFlags.CAN_DELETE)) add(CRMConversationOption.DELETE)
            } ?: add(CRMConversationOption.COPY_LINK)
        }
    }

    override fun onConversationOptionSelected(option: CRMConversationOption) {
        if (networkUtilsFeatureProvider?.get()?.isConnected == false) {
            mView?.showErrorPopup(
                messageRes = RCommunicatorDesign.string.communicator_sync_error_message,
                icon = SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
            )
        }
        when (option) {
            CRMConversationOption.REASSIGN_TO_QUEUE -> reassignToQueue()
            CRMConversationOption.REASSIGN_TO_GROUP -> openReassignToGroupScreen()
            CRMConversationOption.REASSIGN_TO_OPERATOR -> openReassignToOperator()
            CRMConversationOption.COPY_LINK -> copyLink()
            CRMConversationOption.REQUEST_CONTACTS -> requestContacts()
            CRMConversationOption.WRITE_TO_CLIENT -> openWriteToClientScreen()
            CRMConversationOption.COMPLETE -> completeConsultation()
            CRMConversationOption.DELETE -> deleteConsultation()
        }
    }

    private fun openReassignToGroupScreen() {
        coreConversationInfo.conversationUuid?.let {
            router.openReassignToGroupScreen(it)
        }
    }
    private fun openReassignToOperator() {
        coreConversationInfo.conversationUuid?.let {
            router.openReassignCommentToOperator(
                it,
                coreConversationInfo.operatorId,
                coreConversationInfo.consultationChannel!!
            )
        }
    }

    override fun onReassignCommentResult(result: Bundle) {
        router.openReassignToOperator(result)
    }

    private fun requestContacts() {
        coreConversationInfo.conversationUuid?.let {
            interactor.requestContacts(it).subscribe(Functions.EMPTY_ACTION, Timber::e)
                .storeIn(compositeDisposable)
        }
    }

    private fun openWriteToClientScreen() {
        coreConversationInfo.authorId?.let {
            router.openWriteToClientScreen(coreConversationInfo.conversationUuid!!, it)
        }
    }

    override fun onToolbarClick() {
        val canOpenCompanyCard = coreConversationInfo.sourceId != null
                && coreConversationInfo.crmConsultationCase is CRMConsultationCase.SalePoint

        val canOpenPersonCard = coreConversationInfo.authorId != null
                && coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator

        if (canOpenCompanyCard) {
            router.openCompanyDetails(coreConversationInfo.sourceId!!)
        } else if (canOpenPersonCard) {
            router.openPersonCard(coreConversationInfo.authorId!!)
        }
    }

    private fun copyLink() {
        coreConversationInfo.conversationUuid?.let {
            interactor.getUrlByUuid(it)
                .subscribe(
                    { url ->
                        mView?.copyLink(url)
                    },
                    Timber::e
                ).storeIn(compositeDisposable)
        }
    }

    private fun reassignToQueue() {
        coreConversationInfo.conversationUuid?.let {
            interactor.reassignToQueue(it).subscribe(Functions.EMPTY_ACTION, Timber::e)
                .storeIn(compositeDisposable)
        }
    }

    private fun completeConsultation() {
        coreConversationInfo.conversationUuid?.let {
            interactor.closeConsultation(it).subscribe(Functions.EMPTY_ACTION, Timber::e)
                .storeIn(compositeDisposable)

            crmConversationDependency?.reviewFeature?.onEvent(ConversationReviewEvent.COMPLETE_CONSULTATION)
        }
    }

    private fun deleteConsultation() {
        coreConversationInfo.conversationUuid?.let {
            interactor.deleteConsultation(it).subscribe(
                { mView?.closeConversationFragment() },
                Timber::e
            )
                .storeIn(compositeDisposable)
        }
    }
}
