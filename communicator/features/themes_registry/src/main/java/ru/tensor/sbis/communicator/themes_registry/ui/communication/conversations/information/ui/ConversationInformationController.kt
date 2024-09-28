package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter.Companion.CONVERSATION_INFO_SELECTION_RESULT_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStoreFactory
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.ConversationInformationTabContentFragmentFactory
import ru.tensor.sbis.folderspanel.PickNameDialogFragment
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/**
 * Связывает [ConversationInformationFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class ConversationInformationController @AssistedInject constructor(
    @Assisted fragment: Fragment,
    viewFactory: ConversationInformationView.Factory,
    private val conversationInformationData: ConversationInformationData,
    private val router: ConversationInformationRouter,
    private val conversationInformationStoreFactory: ConversationInformationStoreFactory
) : PickNameDialogFragment.FolderPickNameDialogListener {

    private val store = fragment.provideStore { conversationInformationStoreFactory.create(it) }
    private val tabContentFragmentFactory = ConversationInformationTabContentFragmentFactory(
        fragment.arguments,
        conversationInformationData
    )

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))

        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map { it.toIntent() } bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
            childFragmentManager.setFragmentResultListener(CONVERSATION_INFO_SELECTION_RESULT_KEY, this) { _, bundle ->
                store.accept(Intent.NavigateBack)
                parentFragmentManager.setFragmentResult(CONVERSATION_INFO_SELECTION_RESULT_KEY, bundle)
            }
        }
    }

    fun changeTransactionsAvailability(isAvailable: Boolean) {
        router.changeTransactionsAvailability(isAvailable)
    }

    private fun toModel(storeState: ConversationInformationStore.State): ConversationInformationView.Model =
        ConversationInformationView.Model(
            toolbarData = storeState.toolbarData,
            searchQuery = storeState.searchQuery,
            tabsViewState = storeState.tabsViewState,
            isGroupConversation = storeState.isGroupConversation,
            participantViewData = storeState.participantViewData,
            callRunning = storeState.callRunning
        )

    private fun Label.consume() {
        when (this) {
            Label.NavigateBack -> router.navigateBack()
            is Label.TabSelected -> {
                if (this.id == ConversationInformationTab.DEFAULT.id) {
                    router.clearTabContent()
                } else {
                    router.changeTabContent(tabContentFragmentFactory.createTabContentFragment(this.id))
                }
            }
            is Label.OpenFilter -> {
                router.openFilterSelection(currentFilter) { result ->
                    store.accept(Intent.FilterSelected(result))
                }
            }
            is Label.CreateFolder -> {
                router.createFolder(folderName)
            }
            is Label.AddButtonClicked -> store.accept(Intent.ShowFabMenu { option -> store.accept(Intent.FabMenuOptionSelected(option)) })
            is Label.StartCall -> router.startCall(participants, isVideo)
            is Label.CopyLink -> router.copyLink(url)
            is Label.OpenParticipantSelection -> router.openParticipantSelection(uuids)
            Label.OpenLinkAddition -> router.openLinkAddition(conversationInformationData.conversationUuid)
            is Label.OpenProfile -> router.showProfile(profileUuid)
            is Label.OpenMenu -> router.openMenu(onOptionSelected)
            is Label.ShowFabMenu -> router.showFabMenu(optionAction)
            is Label.ShowFolderCreationDialog -> router.showFolderCreationDialog()
            is Label.ShowFilesPicker -> router.showFilesPicker()
            is Label.AddFiles -> router.addFiles(selectedFiles, compressImages)
        }
    }

    /** Обработать нажатие кнопки *назад* */
    fun onBackPressed(): Boolean {
        store.accept(Intent.OnBackPressed)
        return true
    }

    /** Закрыть экран при свайпбеке. */
    fun closeOnSwipeBack() {
        router.navigateBack()
    }

    override fun onNameAccepted(name: String?) {
        name?.let {
            store.accept(Intent.CreateFolder(it))
        }
    }

    override fun onDialogClose() = Unit
}