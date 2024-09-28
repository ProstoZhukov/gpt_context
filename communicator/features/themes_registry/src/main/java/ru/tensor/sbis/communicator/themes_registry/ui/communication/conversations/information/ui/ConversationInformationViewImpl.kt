package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentConversationInformationBinding
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.DEFAULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.PARTICIPANTS
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTabsViewState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.mapToScreenState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState.SEARCHING
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationView.Event
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view.ConversationInformationParticipantViewData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.toolbar.ConversationInformationToolbar
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.ConversationInformationTouchHelper
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsHostFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.view.DialogParticipantsFragment
import ru.tensor.sbis.design.tabs.util.tabs
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.utils.extentions.doOnNextGlobalLayout

/**
 * Реализация View содержимого экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
@SuppressLint("ClickableViewAccessibility")
internal class ConversationInformationViewImpl(
    private val binding: CommunicatorFragmentConversationInformationBinding,
    private val conversationInformationData: ConversationInformationData
) : BaseMviView<ConversationInformationView.Model, Event>(),
    ConversationInformationView {

    private val context: Context = binding.communicatorConversationInformationToolbar.context
    private val tabsView = binding.communicatorConversationInformationTabs
    private val participantView = binding.communicatorConversationInformationSingleParticipant
    private val touchHelper = ConversationInformationTouchHelper(
        context,
        binding.communicatorConversationInformationAppBar
    )
    private val toolbar = ConversationInformationToolbar(
        binding.communicatorConversationInformationToolbar,
        binding.root.findViewTreeLifecycleOwner(),
        { tabsView.tabs.indexOfFirst { it.id == ConversationInformationTab.FILES.id } == tabsView.selectedTabIndex },
        { event -> dispatch(event) }
    )

    init {
        tabsView.setOnTouchListener { _, event ->
            touchHelper.onTouchEvent(event)
        }
        initFloatingButtons()
    }

    private fun initFloatingButtons() {
        binding.communicatorConversationInformationButtonAdd.setOnClickListener {
            dispatch(Event.AddButtonClicked)
        }
        binding.communicatorConversationInformationButtonCall.setOnClickListener {
            dispatch(Event.StartCall(false))
        }
        binding.communicatorConversationInformationButtonVideo.setOnClickListener {
            dispatch(Event.StartCall(true))
        }
    }

    private fun initScreen() {
        val state = conversationInformationData.mapToScreenState()
        toolbar.setData(state.toolbarData)
        onTabsStateChange(state.tabsViewState)
        if (state.participantViewData != null) {
            updateParticipantView(state.participantViewData)
            updateParticipantViewVisibility(isVisible = true)
        }
    }

    override val renderer: ViewRenderer<ConversationInformationView.Model> =
        diff {
            diff(
                get = ConversationInformationView.Model::toolbarData,
                set = {
                    toolbar.setData(it)
                    if (it.toolbarState == SEARCHING) { touchHelper.hideSingleParticipantView() }
                    val participantsTabIndex = tabsView.tabs.indexOfFirst { tab -> tab.id == PARTICIPANTS.id }
                    if (tabsView.selectedTabIndex == participantsTabIndex) {
                        updateParticipantsList(it.isChat)
                    }
                }
            )
            diff(
                get = ConversationInformationView.Model::searchQuery,
                set = {
                    toolbar.setSearchText(it)
                    updateTabContentSearchQuery(it)
                }
            )
            diff(
                get = ConversationInformationView.Model::tabsViewState,
                set = { onTabsStateChange(it) }
            )
            diff(
                get = ConversationInformationView.Model::participantViewData,
                set = { updateParticipantView(it) }
            )
            diff(
                get = ConversationInformationView.Model::isCallButtonsVisible,
                set = {
                    binding.communicatorConversationInformationButtonCall.isVisible = it
                    binding.communicatorConversationInformationButtonVideo.isVisible = it
                }
            )
            diff(
                get = ConversationInformationView.Model::isParticipantViewVisible,
                set = { updateParticipantViewVisibility(it) }
            )
        }

    override fun onSaveInstanceState(outState: Bundle): Bundle = outState.apply {
        val editValue = binding.communicatorConversationInformationToolbar.titleView?.value ?: StringUtils.EMPTY
        putString(EDIT_VALUE_KEY, editValue.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) initScreen()
        savedInstanceState?.run {
            toolbar.restoreEditValue(getString(EDIT_VALUE_KEY, StringUtils.EMPTY))
        }
    }

    private fun updateParticipantsList(isChat: Boolean) {
        binding.communicatorConversationInformationTabsContentContainer.apply {
            try {
                val fragment = getFragment<Fragment>()
                if (isChat) {
                    fragment.castTo<ChatParticipantsHostFragment>()?.refreshParticipantsList()
                } else {
                    fragment.castTo<DialogParticipantsFragment>()?.refreshParticipantsList()
                }
            } catch (_: Exception) {}
        }
    }

    private fun updateTabContentSearchQuery(query: String) {
        binding.communicatorConversationInformationTabsContentContainer.apply {
            try {
                getFragment<Fragment>().castTo<ConversationInformationSearchableContent>()?.setSearchQuery(query)
            } catch (_: Exception) {}
        }
    }

    private fun onTabsStateChange(state: ConversationInformationTabsViewState) {
        val isInitialState = tabsView.tabs.isEmpty()
        if (isInitialState || tabsView.needUpdate(state)) {
            updateTabs(state)
        }
        doIf(tabsView.tabs.isNotEmpty()) {
            val selectedTabIndex = tabsView.tabs.indexOfFirst { state.selectedTab.id == it.id }
            if (selectedTabIndex != tabsView.selectedTabIndex) {
                tabsView.selectedTabIndex = selectedTabIndex
            }
        }
        if (!isInitialState) {
            toolbar.onChangeTab()
        }
        binding.communicatorConversationInformationButtonAdd.isVisible = state.selectedTab != DEFAULT
        applySearchQueryForOpeningTab()
    }

    private fun SbisTabsView.needUpdate(state: ConversationInformationTabsViewState): Boolean {
        val availableTabsIds = state.availableTabs.map { it.id }
        val currentTabsIds = tabs.mapNotNull { it.id }
        return !availableTabsIds.containsAll(currentTabsIds) || !currentTabsIds.containsAll(availableTabsIds)
    }

    private fun updateTabs(state: ConversationInformationTabsViewState) {
        tabsView.apply {
            tabs = getTabs(state)
            setOnTabClickListener { item ->
                item.id?.let {
                    touchHelper.hideSingleParticipantView()
                    dispatch(Event.TabSelected(it))
                }
            }
        }
    }

    private fun getTabs(state: ConversationInformationTabsViewState) = tabs {
        state.availableTabs.map {
            tab {
                id = it.id
                content {
                    text(it.text)
                }
            }
        }
    }

    private fun applySearchQueryForOpeningTab() {
        binding.communicatorConversationInformationTabsContentContainer.apply {
            doOnNextGlobalLayout(
                {
                    try {
                        getFragment<Fragment>().castTo<ConversationInformationSearchableContent>() == null
                    } catch (_: Exception) {
                        false
                    }
                },
                {
                    Handler(Looper.getMainLooper()).postDelayed(
                        { updateTabContentSearchQuery(toolbar.getCurrentSearchQuery()) },
                        APPLY_SEARCH_QUERY_DELAY
                    )
                }
            )
        }
    }

    private fun updateParticipantView(data: ConversationInformationParticipantViewData?) {
        data?.let {
            participantView.viewData = data
            participantView.setOnPhotoClickListener {
                data.photoData.uuid?.let { uuid -> dispatch(Event.OpenProfile(uuid)) }
            }
        }
    }

    private fun updateParticipantViewVisibility(isVisible: Boolean) {
        if (isVisible) {
            participantView.isVisible = true
            touchHelper.showSingleParticipantView()
        } else {
            touchHelper.hideSingleParticipantView()
            Handler(Looper.getMainLooper()).postDelayed(
                { participantView.isVisible = false },
                SINGLE_PARTICIPANT_VIEW_INVISIBLE_DELAY
            )
        }
    }
}

private const val SINGLE_PARTICIPANT_VIEW_INVISIBLE_DELAY = 1500L
private const val APPLY_SEARCH_QUERY_DELAY = 500L
private const val EDIT_VALUE_KEY = "EDIT_VALUE_KEY"
