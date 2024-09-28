package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communicator.base_folders.list_section.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChatListFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView.Event
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView.Model
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.CRMChatListOnScrollListener
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.launchAndCollect
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.CRMChatSwipeMenuHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.TakeMenuItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.utils.PinnedHeaderViewHelper
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.design.view.input.searchinput.util.expandSearchInput
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.DataInsertListener

/**
 * @author da.zhukov
 */
internal class CRMChatListViewImpl(
    private val binding: CommunicatorCrmChatListFragmentBinding,
    listComponentFactory: CRMChatListComponentFactory,
    foldersViewHolderHelper: FoldersViewHolderHelper,
    crmChatSwipeMenuHelper: CRMChatSwipeMenuHelper,
    crmChatListOnScrollListener: CRMChatListOnScrollListener,
    private val isHistoryMode: Boolean,
    private val filterHolder: CRMChatListFilterHolder
) : BaseMviView<Model, Event>(), CRMChatListView {

    private val actionHandler = DebounceActionHandler()

    override val renderer: ViewRenderer<Model> = diff {
        diff(
            get = Model::query,
            set = {
                binding.communicatorCrmChatListSearchInput.apply {
                    setSearchText(it ?: StringUtils.EMPTY)
                }
            }
        )
        diff(
            get = Model::filters,
            set = { binding.communicatorCrmChatListSearchInput.setSelectedFilters(it) }
        )
        diff(
            get = Model::currentFolderViewIsVisible,
            set = {
                binding.crmChatListFolderTitleLayout.isVisible = it
                foldersViewHolderHelper.run {
                    if (it) hideFoldersView(true) else showFoldersView(false)
                }
            }
        )
        diff(
            get = Model::folderTitle,
            set = { binding.crmChatListFolderTitleLayout.setTitle(it) }
        )
        diff(
            get = Model::searchPanelIsOpen,
            set = { if (it) expandSearchInput(binding.crmChatListAppBar) }
        )
        diff(
            get = Model::fabVisible,
            set = {
                if (isTablet) {
                    binding.communicatorCrmChatListFab?.isVisible = it
                } else {
                    dispatch(Event.TakeOldestFabVisibilityChanged(it))
                }
            }
        )
    }

    private val queryChangeFlow get() = binding.communicatorCrmChatListSearchInput
        .searchQueryChangedObservable().asFlow()

    private val cancelSearchFlow get() = binding.communicatorCrmChatListSearchInput
        .cancelSearchObservable().asFlow()

    private val searchActionsFlow get() = binding.communicatorCrmChatListSearchInput
        .searchFieldEditorActionsObservable().asFlow()

    private val searchFilterClickFlow get() = binding.communicatorCrmChatListSearchInput
        .filterClickObservable().asFlow()

    private val isTablet
        get() = DeviceConfigurationUtils.isTablet(binding.root.context)

    private val needBackButton
        get() = !isTablet || isHistoryMode

    init {
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope

        with(binding) {
            binding.communicatorCrmChatListSearchInput.setSelectedFilters(filterHolder.getCurrentFilters())
            crmChatListAppBar.addOnOffsetChangedListener(
                PinnedHeaderViewHelper(
                    pinnedView = crmChatListFolderTitleLayout,
                    updateListViewTopMargin = { communicatorCrmChatList.updateTopMargin(it) },
                    onOffsetChanged = {
                        foldersViewHolderHelper.run {
                            if (binding.crmChatListFolderTitleLayout.isVisible) {
                                hideFoldersView(true)
                            } else {
                                showFoldersView(false)
                            }
                        }
                    }
                )
            )
            crmChatListFolderTitleLayout.setOnClickListener {
                scope?.launch {
                    foldersViewHolderHelper.folderActionListener.closed()
                    dispatch(Event.FolderChanged(ConsultationGroupType.UNKNOWN, StringUtils.EMPTY))
                }
            }
            communicatorCrmChatList.list.apply {
                navMenuPadding(true)
                addOnScrollListener(crmChatListOnScrollListener)
            }

            communicatorCrmChatListFab?.apply {
                icon = SbisButtonDrawableIcon(icon = TakeOldestDrawable(context))
                setOnClickListener { dispatch(Event.TakeOldestConsultation) }
            }
        }

        scope?.launchWhenStarted {
            launchAndCollect(queryChangeFlow) {
                dispatch(Event.EnterSearchQuery(it))
            }
            launchAndCollect(cancelSearchFlow) {
                dispatch(Event.EnterSearchQuery(null))
            }
            launchAndCollect(searchActionsFlow) {
                if (it == EditorInfo.IME_ACTION_NEXT || it == EditorInfo.IME_ACTION_SEARCH) {
                    binding.communicatorCrmChatListSearchInput.hideKeyboard()
                }
            }
            launchAndCollect(searchFilterClickFlow) {
                dispatch(Event.ClickFilterIcon)
            }
            val listComponentView = binding.communicatorCrmChatList.apply {
                list.itemAnimator = null
            }
            listComponentFactory.create(listComponentView).apply {
                launchAndCollect(onItemClick.asFlow()) { item ->
                    val canOpenConsultation = item.value.fieldConsultationViewModel!!.allowedActions
                        .contains(ConsultationActionsFlags.CAN_VIEW)

                    if (canOpenConsultation) {
                        val isMessagePanelVisible = item.value.fieldConsultationViewModel!!.allowedActions
                            .contains(ConsultationActionsFlags.CAN_SEND_MESSAGE)

                        item.value.fieldConsultationViewModel?.id?.let {
                            clearSearchInputFocusIfNeed()
                            dispatch(
                                Event.OpenConsultation(
                                    CRMConsultationOpenParams(
                                        crmConsultationCase = CRMConsultationCase.Operator(
                                            originUuid = it,
                                            viewId = filterHolder.viewId
                                        ),
                                        isCompleted = item.value.fieldConsultationViewModel?.isClosed == true,
                                        isHistoryMode = isHistoryMode,
                                        needBackButton = needBackButton,
                                        isMessagePanelVisible = isMessagePanelVisible
                                    )
                                )
                            )
                        }
                    } else {
                        dispatch(
                            Event.ShowInformer(
                                R.string.communicator_crm_chat_list_chat_no_access,
                                SbisPopupNotificationStyle.ERROR,
                                SbisMobileIcon.Icon.smi_information.character.toString()
                            )
                        )
                    }
                }
            }
            launchAndCollect(foldersViewHolderHelper.foldersActionFlow) { action ->
                clearSearchInputFocusIfNeed()
                action.folder?.let {
                    dispatch(Event.FolderChanged(ConsultationGroupType.valueOf(it.id), it.title))
                } ?: dispatch(Event.FolderChanged(ConsultationGroupType.UNKNOWN, StringUtils.EMPTY))
            }
            launchAndCollect(crmChatSwipeMenuHelper.swipeMenuActionFlow) {
                dispatch(Event.SwipeMenuItemClicked(it))
                if (it is TakeMenuItem) {
                    clearSearchInputFocusIfNeed()
                    dispatch(
                        Event.OpenConsultation(
                            CRMConsultationOpenParams(
                                crmConsultationCase = CRMConsultationCase.Operator(
                                    originUuid = it.consultationId,
                                    viewId = filterHolder.viewId
                                ),
                                isCompleted = false,
                                isHistoryMode = isHistoryMode,
                                needBackButton = needBackButton,
                            )
                        )
                    )
                }
            }
            launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    dispatch(Event.OpenSearchPanel)
                }
            }
        }
    }

    private fun clearSearchInputFocusIfNeed() {
        binding.communicatorCrmChatListSearchInput.run {
            if (hasFocus()) {
                clearFocus()
                hideKeyboard()
            }
        }
    }
}
