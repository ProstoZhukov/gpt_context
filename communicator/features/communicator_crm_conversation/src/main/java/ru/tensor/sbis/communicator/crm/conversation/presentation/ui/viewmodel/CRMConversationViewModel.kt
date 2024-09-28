package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.stubview.StubViewContent
import java.util.UUID

/**
 * Вью-модель экрана переписки CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationViewModel : ViewModel() {

    var selectedGreeting: String? = null
    var channelUuid: UUID? = null
    var setQuickReplyScrollListener = false

    val showCreateConsultationIcon: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showConsultationMenuIcon: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showCreateConsultationFab: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showMessagePanel: Subject<Boolean> = BehaviorSubject.createDefault(true)
    val msgUnreadCounter: Subject<Int> = BehaviorSubject.createDefault(0)
    val showHistoryView: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val prepareHistoryView: Subject<Triple<Boolean, UUID, UUID>> = BehaviorSubject.createDefault(Triple(false, UUIDUtils.NIL_UUID, UUIDUtils.NIL_UUID))
    val showNextButton: Subject<UUID> = BehaviorSubject.createDefault(UUIDUtils.NIL_UUID)
    val showTakeButton: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showReopenButton: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showHistoryButton: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val requireKeyboardOnFirstLaunch = BehaviorSubject.createDefault(false)
    val showNNP: BehaviorSubject<() -> Unit> = BehaviorSubject.create()
    val prepareQuickReplyViews: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val showQuickReplyOnButton: Subject<Boolean> = BehaviorSubject.createDefault(false)
    val requestQuickRepliesOnTextInput: Subject<String> = BehaviorSubject.createDefault(StringUtils.EMPTY)
    val greetingsSubject: Subject<List<String>> = BehaviorSubject.createDefault(emptyList())
    val currentStub: BehaviorSubject<StubViewContent> = BehaviorSubject.create()
    val showStub: Subject<Boolean> = BehaviorSubject.createDefault(false)

    val createConsultationIcon: Observable<Boolean> = showCreateConsultationIcon.distinctUntilChanged()
    val menuConsultationMenuIcon: Observable<Boolean> = showConsultationMenuIcon.distinctUntilChanged()
    val createConsultationFab: Observable<Boolean> = showCreateConsultationFab.distinctUntilChanged()
    val messagePanel: Observable<Boolean> = showMessagePanel.distinctUntilChanged()
    val msgUnreadCount: Observable<Int> = msgUnreadCounter.distinctUntilChanged()
    val isNeedPreparedHistoryView: Observable<Triple<Boolean, UUID, UUID>> = prepareHistoryView.distinctUntilChanged()
    val isNeedShowHistoryView: Observable<Boolean> = showHistoryView.distinctUntilChanged()
    val isNeedShowNextButton: Observable<UUID> = showNextButton.distinctUntilChanged()
    val isNeedShowTakeButton: Observable<Boolean> = showTakeButton.distinctUntilChanged()
    val isNeedShowReopenButton: Observable<Boolean> = showReopenButton.distinctUntilChanged()
    val isNeedShowHistoryButton: Observable<Boolean> = showHistoryButton.distinctUntilChanged()
    val showKeyboardOnFirstLaunch: Observable<Boolean> = requireKeyboardOnFirstLaunch.distinctUntilChanged()
    val isNeedShowNNp: Observable<() -> Unit> = showNNP.distinctUntilChanged()
    val needPrepareQuickReplyViews: Observable<Boolean> = prepareQuickReplyViews.distinctUntilChanged()
    val isNeedShowQuickReplyOnButton: Observable<Boolean> = showQuickReplyOnButton.distinctUntilChanged()
    val isNeedRequestQuickRepliesOnTextInput: Observable<String> = requestQuickRepliesOnTextInput.distinctUntilChanged()
    val greetings: Observable<List<String>> = greetingsSubject.distinctUntilChanged()
    val currentStubContent: Observable<StubViewContent> = currentStub.distinctUntilChanged()
    val stubIsVisible: Observable<Boolean> = showStub.distinctUntilChanged()

    override fun onCleared() {
        super.onCleared()
        showCreateConsultationIcon.onComplete()
        showCreateConsultationFab.onComplete()
        showMessagePanel.onComplete()
        msgUnreadCounter.onComplete()
        showNextButton.onComplete()
        showTakeButton.onComplete()
        showReopenButton.onComplete()
        showHistoryButton.onComplete()
        requireKeyboardOnFirstLaunch.onComplete()
        showNNP.onComplete()
        prepareQuickReplyViews.onComplete()
        showQuickReplyOnButton.onComplete()
        requestQuickRepliesOnTextInput.onComplete()
        greetingsSubject.onComplete()
    }
}
