package ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.communicator.common.util.message_search.ThemeMessageSearchApi
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.themesRegistryFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.utils.MessageSearchHelper
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.verification_decl.verification.ContactVerifiedEvent
import java.util.UUID

/**
 * Вью-модель экрана переписки сбис
 *
 * @author vv.chekurda
 */
internal class ConversationViewModel : ViewModel() {

    val messageSearchHelper: MessageSearchHelper = MessageSearchHelper()

    val isLocked: Observer<Boolean> = BehaviorSubject.createDefault(false)
    val showMenuIcon: Observer<Boolean> = BehaviorSubject.createDefault(false)
    val showDocumentTitle: Observer<Boolean> = BehaviorSubject.createDefault(false)
    val showMessagePanel: Observer<Boolean> = PublishSubject.create()
    val phoneVerification: Observable<ContactVerifiedEvent> = singletonComponent.dependency.observeContactVerifiedEvent()
    val showRecipientsPanel: Subject<Boolean> = BehaviorSubject.createDefault(true)
    val currentStub: BehaviorSubject<StubViewContent> = BehaviorSubject.create()
    val showStub: Subject<Boolean> = BehaviorSubject.createDefault(false)

    val menuIconObservable: Observable<Boolean> =
        isLocked.combine(showMenuIcon) { (isLocked, showMenuIcon) -> !isLocked && showMenuIcon }
    val documentNameObservable: Observable<Boolean> =
        isLocked.combine(showDocumentTitle) { (isLocked, showDocumentName) -> !isLocked && showDocumentName }
    val enableInputObservable: Observable<Boolean> =
        showMessagePanel.withLatestFrom(isLocked) { (showMessagePanel, isLocked) -> showMessagePanel && !isLocked }

    val requireKeyboardOnFirstLaunch = BehaviorSubject.createDefault(false)
    val firstDataLoaded: Observer<Boolean> = PublishSubject.create()
    val showKeyboardOnFirstLaunch: Observable<Boolean> =
         requireKeyboardOnFirstLaunch.combine(firstDataLoaded) {
             (needShowKeyboard, dataLoaded) -> needShowKeyboard && dataLoaded
         }.distinctUntilChanged()
    val recipientsPanelVisibility: Observable<Boolean> =
        showRecipientsPanel.distinctUntilChanged()
    val currentStubContent: Observable<StubViewContent> = currentStub.distinctUntilChanged()
    val stubIsVisible: Observable<Boolean> = showStub.distinctUntilChanged()

    override fun onCleared() {
        super.onCleared()
        isLocked.onComplete()
        showMenuIcon.onComplete()
        showDocumentTitle.onComplete()
        showMessagePanel.onComplete()
        requireKeyboardOnFirstLaunch.onComplete()
        firstDataLoaded.onComplete()
        showRecipientsPanel.onComplete()
    }

}

/** @SelfDocumented */
@Suppress("UNCHECKED_CAST", "NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
private fun <T : Any> Observer<T>.combine(other: Observer<T>, condition: (Pair<T, T>) -> T): Observable<T> =
    Observable.combineLatest(this as Observable<T>, other as Observable<T>) { first, second ->
        condition(
            Pair(
                first,
                second
            )
        )
    }

/** @SelfDocumented */
@Suppress("UNCHECKED_CAST", "NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
private fun <T : Any> Observer<T>.withLatestFrom(other: Observer<T>, condition: (Pair<T, T>) -> T): Observable<T> =
    (this as Observable<T>).withLatestFrom(other as Observable<T>) { first, second -> condition(Pair(first, second)) }