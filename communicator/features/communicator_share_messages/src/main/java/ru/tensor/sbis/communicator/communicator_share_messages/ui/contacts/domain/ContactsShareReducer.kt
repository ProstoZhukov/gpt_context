package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain

import android.view.View
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareEvent.*
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareState.*
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.di.QUICK_SHARE_KEY_NAME
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data.DirectSharePerson
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data.SendContactsShareData
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data.ContactsShareLiveData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

/**
 * Редуктор раздела шаринга в контакты.
 *
 * @author vv.chekurda
 */
internal class ContactsShareReducer @Inject constructor(
    private val shareData: ShareData,
    private val liveData: ContactsShareLiveData,
    @Named(QUICK_SHARE_KEY_NAME) private val quickShareKey: String?
) {
    private var state: ContactsShareState = SELECTION

    init {
        liveData.createDraftDialog(shareData)
        if (quickShareKey != null) setDirectState()
    }

    /**
     * Обработать событие [event].
     */
    fun reduce(event: ContactsShareEvent) {
        Timber.i("onEvent: state = $state, event = $event")
        val requireContinue = handleObserveEvent(event)
        if (!requireContinue) return
        when (state) {
            SELECTION -> handleSelectionStateEvent(event)
            SELECTED_SINGLE -> handleSelectedSingleStateEvent(event)
            SELECTED_MULTI -> handleSelectedMultiStateEvent(event)
            DIRECT -> handleDirectStateEvent(event)
            SENDING -> IgnoreEvent
        }
    }

    private fun handleObserveEvent(event: ContactsShareEvent): Boolean =
        when (event) {
            is ContactsShareMessagePanelEvent.OnMessagePanelTextChanged -> {
                liveData.setMessagePanelText(event.text)
                false
            }
            is ContactsShareSelectionEvent.OnSelectedDataChanged -> {
                liveData.setMultiSelectedPersons(event.data.items)
                true
            }
            is OnDraftDialogCreated -> {
                liveData.setConversationUuid(event.conversationUuid)
                false
            }
            else -> true
        }

    private fun handleSelectionStateEvent(event: ContactsShareEvent) {
        fun setSelectedMultiState() {
            state = SELECTED_MULTI
            with(liveData) {
                changeRecipientSelectionVisibility(View.INVISIBLE)
                changeSelectionPanelVisibility(isVisible = true)
                changeMenuBackButtonVisibility(isVisible = true)
            }
        }

        with(liveData) {
            when (event) {
                is ContactsShareSelectionEvent.OnSelectedDataChanged -> {
                    val data = event.data
                    when {
                        !data.hasSelectedItems -> {
                            setMultiSelectedPersons(persons = data.items)
                            changeMessagePanelVisibility(isVisible = false)
                            changeMenuNavPanelVisibility(isVisible = true)
                        }
                        data.isMultiSelection -> {
                            setMultiSelectedPersons(persons = data.items)
                            changeMessagePanelVisibility(isVisible = true)
                            changeMenuNavPanelVisibility(isVisible = false)
                        }
                        else -> {
                            state = SELECTED_SINGLE
                            setSelectedPerson(data.items.first() as SelectionPersonItem)
                            changeMessagePanelVisibility(isVisible = true)
                            changeRecipientSelectionVisibility(View.INVISIBLE)
                            changeMenuBackButtonVisibility(isVisible = true)
                            changeMenuNavPanelVisibility(isVisible = false)
                            showKeyboard()
                        }
                    }
                }
                is ContactsShareMessagePanelEvent.OnMessagePanelFocusChanged -> {
                    if (event.isFocused) {
                        setSelectedMultiState()
                    } else {
                        IgnoreEvent
                    }
                }
                is OnBackPressed -> {
                    finishShare()
                }
                is ContactsShareMessagePanelEvent.OnSendMessageClicked -> {
                    setSelectedMultiState()
                    setSendingState()
                    setSendContactsShareData(
                        getSendContactsShareData(
                            recipients = multiSelectedItems.value
                        )
                    )
                }
                else -> illegalState(event)
            }
        }
    }

    private fun handleSelectedSingleStateEvent(event: ContactsShareEvent) {
        with(liveData) {
            fun setSelectionState() {
                state = SELECTION
                setSelectedPerson(null)
                changeMessagePanelVisibility(isVisible = false)
                changeMenuBackButtonVisibility(isVisible = false)
                changeMenuNavPanelVisibility(isVisible = true)
                changeRecipientSelectionVisibility(View.VISIBLE)
                hideKeyboard()
            }

            when (event) {
                OnBackPressed -> {
                    setSelectionState()
                }
                ContactsShareMessagePanelEvent.OnSendMessageClicked -> {
                    setSendingState()
                    setSendContactsShareData(
                        getSendContactsShareData(
                            recipients = listOf(selectedPerson.value!!)
                        )
                    )
                }
                is ContactsShareMessagePanelEvent.OnMessagePanelFocusChanged,
                is ContactsShareSelectionEvent.OnSelectedDataChanged -> IgnoreEvent
                else -> illegalState(event)
            }
        }
    }

    private fun handleSelectedMultiStateEvent(event: ContactsShareEvent) {
        with(liveData) {
            fun setSelectionState(hideMessagePanel: Boolean) {
                state = SELECTION
                changeSelectionPanelVisibility(isVisible = false)
                changeMenuBackButtonVisibility(isVisible = false)
                changeRecipientSelectionVisibility(View.VISIBLE)
                hideKeyboard()
                if (hideMessagePanel) {
                    changeMessagePanelVisibility(isVisible = false)
                    changeMenuNavPanelVisibility(isVisible = true)
                }
            }

            when (event) {
                OnBackPressed -> {
                    setSelectionState(hideMessagePanel = false)
                }
                is ContactsShareSelectionEvent.OnSelectedDataChanged -> {
                    if (!event.data.hasSelectedItems) {
                        setSelectionState(hideMessagePanel = true)
                    }
                }
                is ContactsShareSelectionEvent.OnUnselectClicked -> {
                    unselectItem(event.item)
                }
                ContactsShareMessagePanelEvent.OnSendMessageClicked -> {
                    setSendingState()
                    setSendContactsShareData(
                        getSendContactsShareData(
                            recipients = multiSelectedItems.value
                        )
                    )
                }
                is ContactsShareMessagePanelEvent.OnMessagePanelFocusChanged -> IgnoreEvent
                else -> illegalState(event)
            }
        }
    }

    private fun handleDirectStateEvent(event: ContactsShareEvent) {
        with(liveData) {
            when (event) {
                is ContactQuickShareEvent.OnProfileLoaded -> {
                    val profile = event.profile
                    if (profile != null) {
                        val person = profile.person
                        val selectedPerson = DirectSharePerson(
                            id = RecipientPersonId(person.uuid),
                            title = "${person.name.last} ${person.name.first}".trim(),
                            subtitle = profile.companyOrDepartment,
                            photoData = PersonData(
                                uuid = person.uuid,
                                photoUrl = person.photoUrl,
                                initialsStubData = person.photoDecoration?.let { decor ->
                                    InitialsStubData(
                                        decor.initials,
                                        decor.backgroundColorHex
                                    )
                                }
                            ),
                            isInMyCompany = profile.inMyCompany,
                            personName = person.name.let { PersonName(it.first, it.last, it.patronymic) }
                        )
                        setSelectedPerson(selectedPerson)
                    } else {
                        Timber.e("ContactsShareViewModel: person is null for direct sharing")
                        finishShare()
                    }
                }
                OnBackPressed -> {
                    finishShare()
                }
                ContactsShareMessagePanelEvent.OnSendMessageClicked -> {
                    setSendingState()
                    setSendContactsShareData(
                        getSendContactsShareData(
                            recipients = listOf(selectedPerson.value!!)
                        )
                    )
                }
                is ContactsShareMessagePanelEvent.OnMessagePanelFocusChanged -> IgnoreEvent
                else -> illegalState(event)
            }
        }
    }

    private fun setSendingState() {
        with(liveData) {
            state = SENDING
            changeMessagePanelVisibility(isVisible = false)
            changeMenuBackButtonVisibility(isVisible = false)
            changeRecipientSelectionVisibility(View.GONE)
            changeHeightMode(mode = ShareMenuHeightMode.Short)
            setSendingMessageText(messagePanelText.value.toString())
            hideKeyboard()
        }
    }

    private fun setDirectState() {
        state = DIRECT
        UUIDUtils.fromString(quickShareKey?.removePrefix(NavxId.CONTACTS.id)?.removePrefix(NavxId.DIALOGS.id))
            ?.also(liveData::loadSelectedPersonData)
        liveData.changeRecipientSelectionVisibility(View.INVISIBLE)
        liveData.changeMessagePanelVisibility(isVisible = true)
    }

    private fun getSendContactsShareData(recipients: List<SelectionItem>) =
        SendContactsShareData(
            shareData = shareData,
            comment = liveData.messagePanelText.value.toString(),
            recipients = recipients
        )

    private fun illegalState(event: ContactsShareEvent) {
        illegalState { "State = $state, event $event" }
    }
}

internal sealed interface ContactsShareEvent {

    object OnBackPressed : ContactsShareEvent {
        override fun toString(): String = javaClass.simpleName
    }

    class OnDraftDialogCreated(val conversationUuid: UUID) : ContactsShareEvent

    sealed interface ContactsShareMessagePanelEvent : ContactsShareEvent {
        data class OnMessagePanelFocusChanged(val isFocused: Boolean) : ContactsShareMessagePanelEvent
        data class OnMessagePanelTextChanged(val text: CharSequence) : ContactsShareMessagePanelEvent
        object OnSendMessageClicked : ContactsShareMessagePanelEvent {
            override fun toString(): String = javaClass.simpleName
        }
    }

    sealed interface ContactsShareSelectionEvent : ContactsShareEvent {
        data class OnSelectedDataChanged(val data: SelectedData<SelectionItem>) : ContactsShareSelectionEvent
        data class OnUnselectClicked(val item: SelectionItem) : ContactsShareSelectionEvent
    }

    sealed interface ContactQuickShareEvent : ContactsShareEvent {

        data class OnProfileLoaded(val profile: EmployeeProfile?) : ContactQuickShareEvent
    }
}

private enum class ContactsShareState {
    SELECTION,
    SELECTED_SINGLE,
    SELECTED_MULTI,
    SENDING,
    DIRECT
}

private typealias IgnoreEvent = Unit