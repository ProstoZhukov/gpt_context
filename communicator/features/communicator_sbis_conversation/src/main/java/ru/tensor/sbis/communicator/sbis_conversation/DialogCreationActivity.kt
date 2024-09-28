package ru.tensor.sbis.communicator.sbis_conversation

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.communicator.ui.DialogCreationParams
import ru.tensor.sbis.communication_decl.communicator.ui.DialogCreationWithParticipantsParams
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.CONVERSATION_ACTIVITY_CONVERSATION_ARG
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPoolController
import ru.tensor.sbis.design.utils.KeyboardUtils
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити для создания нового диалога.
 * Сначала показывает выбор получателей, чтобы выбрать участников нового диалога.
 * При подтверждении выбора осуществляется переход на новый диалог с выбранными участниками.
 *
 * @author vv.chekurda
 */
internal class DialogCreationActivity : AdjustResizeActivity() {

    private val disposer = CompositeDisposable()
    private val conversationViewPoolController: ConversationViewPoolController
        get() = singletonComponent.conversationViewPoolController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO https://online.sbis.ru/opendoc.html?guid=5d6aa3ce-3498-4362-9eff-b28624eeb6b4&client=3
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(R.layout.communicator_conversation_activity)

        doStuff(savedInstanceState, allowFallback = true)
    }

    private fun doStuff(savedInstanceState: Bundle?, allowFallback: Boolean) {
        // TODO https://online.sbis.ru/opendoc.html?guid=cfd26d5f-653e-4064-905b-bee703b8464c
        communicatorSbisConversationDependency ?: run {
            Timber.e("communicatorSbisConversationDependency is null, fallback: ${!allowFallback}")
            if (allowFallback) {
                // Нет нужных зависимостей. Пробуем отложить все действия, до следующего события в очереди главной вью
                contentView.post { doStuff(savedInstanceState, allowFallback = false) }
            } else {
                finish()
            }
            return
        }
        subscribeRecipientSelectionResult()
        if (savedInstanceState == null) showRecipientSelection()
        conversationViewPoolController.initNewDialogViewPool(this)
    }

    @SuppressLint("CommitTransaction")
    private fun showRecipientSelection() {
        val recipientSelectionFragment = communicatorSbisConversationDependency!!
            .getRecipientSelectionFragment(
                RecipientSelectionConfig(
                    useCase = RecipientSelectionUseCase.NewDialog,
                    requestKey = DIALOG_CREATION_RECIPIENT_SELECTION_REQUEST_KEY,
                    closeOnComplete = false
                )
            )

        supportFragmentManager.beginTransaction()
            .add(R.id.communicator_conversation_fragment_container, recipientSelectionFragment)
            .commit()
    }

    @Suppress("DEPRECATION")
    private fun subscribeRecipientSelectionResult() {
        communicatorSbisConversationDependency!!.getRecipientSelectionResultManager()
            .getSelectionResultObservable(DIALOG_CREATION_RECIPIENT_SELECTION_REQUEST_KEY)
            .doAfterNext { disposer.dispose() }
            .subscribe { result ->
                if (result.isSuccess) {
                    showNewConversationFragment(result.data.allPersonsUuids)
                } else {
                    onBackPressed()
                }
            }.storeIn(disposer)
    }

    @SuppressLint("CommitTransaction")
    @Suppress("DEPRECATION")
    private fun showNewConversationFragment(participantUuidList: List<UUID>) {
        val activityArg = intent.extras?.getSerializable(CONVERSATION_ACTIVITY_CONVERSATION_ARG) as? DialogCreationParams
        val newConversationFragment: Fragment

        if (activityArg != null) {
            newConversationFragment = CommunicatorSbisConversationPlugin.feature.getConversationFragment(
                DialogCreationWithParticipantsParams(
                    participantsUuids = participantUuidList.asArrayList(),
                    folderUuid = activityArg.folderUuid,
                    docInfo = activityArg.docInfo,
                    type = activityArg.type
                )
            )
        } else {
            newConversationFragment = CommunicatorSbisConversationPlugin.feature
                .getConversationFragment(
                    dialogUuid = null,
                    messageUuid = null,
                    folderUuid = intent.extras?.getSerializable(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_FOLDER_UUID_KEY) as? UUID,
                    participantsUuids = participantUuidList.asArrayList(),
                    files = null,
                    text = null,
                    document = intent.extras?.getParcelable(EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY),
                    type = intent.extras
                        ?.getSerializable(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY) as? ConversationType
                        ?: ConversationType.REGULAR,
                    isChat = false,
                    archivedDialog = false
                ).apply {
                    requireArguments().putBoolean(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_ARE_RECIPIENTS_SELECTED, true)
                    requireArguments().putBoolean(IntentAction.Extra.NEED_TO_SHOW_KEYBOARD, true)
                }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.communicator_conversation_fragment_container, newConversationFragment)
            .commit()
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
        } catch (ex: NullPointerException) {
            // fragment view was recycled
            Timber.e(ex)
        }
        disposer.dispose()
        conversationViewPoolController.clearNewDialogViewPoolsHolder()
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        if (isRunning && !delegateBackPressToFragment()) super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun onViewGoneBySwipe() {
        supportFragmentManager.fragments.lastOrNull()?.view?.let { view ->
            KeyboardUtils.hideKeyboard(view)
            view.postDelayed({
                super.onViewGoneBySwipe()
            }, 100)
        }
    }

    private fun delegateBackPressToFragment(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.communicator_conversation_fragment_container)
        return (fragment as? FragmentBackPress)?.onBackPressed() == true
    }

    override fun getContentViewId(): Int =
        R.id.communicator_conversation_fragment_container

    override fun swipeBackEnabled(): Boolean = true
}

private const val DIALOG_CREATION_RECIPIENT_SELECTION_REQUEST_KEY = "DIALOG_CREATION_RECIPIENT_SELECTION_REQUEST_KEY"