package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.toolbar

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationView.Event
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.KeyboardUtils

/**
 * Тулбар экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
internal class ConversationInformationToolbar(
    private val toolbar: SbisTopNavigationView,
    private val lifecycleOwner: LifecycleOwner?,
    private val checkFileTabSelected: () -> Boolean,
    private val dispatch: (Event) -> Unit,
) {

    private val context: Context = toolbar.context
    private val buttonsClickListeners = object : ConversationInformationToolbarButtonsClickListeners {
        override fun onSearchButtonClick() { dispatch(Event.Search.Open) }
        override fun onMoreButtonClick() {
            dispatch(
                Event.OpenMenu { option -> dispatch(Event.MenuOptionSelected(option)) }
            )
        }
        override fun onFilterButtonClick() { dispatch(Event.OpenFilter) }
        override fun onDoneButtonClick() {
            toolbar.titleView?.value?.let { dispatch(Event.EditTitle.End(it)) }
        }
    }
    private val buttons = ConversationInformationToolbarButtons(context, buttonsClickListeners)
    private var isDialog: Boolean = false
    private var restoredEditTitleValue: String? = null

    /** @SelfDocumented */
    fun setData(data: ConversationInformationToolbarData) {
        isDialog = !data.isChat
        when (data.toolbarState) {
            ConversationInformationToolbarState.DEFAULT -> setBaseState(data)
            ConversationInformationToolbarState.EDITING -> setEditingState(data)
            ConversationInformationToolbarState.SEARCHING -> setSearchingState()
        }
    }

    private fun setBaseState(data: ConversationInformationToolbarData) {
        toolbar.apply {
            init(data)
            isEditingEnabled = false
            titleView?.apply {
                value = data.title
                placeholder = getTitlePlaceholder(data.isChat)
                setOnClickListener {
                    dispatch(Event.EditTitle.Start)
                }
                onFocusChangeListener = null
                onValueChanged = null
                clearFocus()
                KeyboardUtils.hideKeyboard(this)
            }
            onChangeGroupAttribute(data.isGroup)
            setButtons()
        }
    }

    private fun View.getTitlePlaceholder(isChat: Boolean): String = resources.getString(
        if (isChat) R.string.communicator_channel_name else R.string.communicator_dialog_name
    )

    private fun SbisTopNavigationView.setButtons() {
        val isFilesTabSelected = checkFileTabSelected()
        rightItems = when {
            isEditingEnabled -> buttons.onlyDone
            content is SbisTopNavigationContent.SearchInput && isFilesTabSelected -> buttons.onlyFilter
            content is SbisTopNavigationContent.SearchInput && !isFilesTabSelected -> buttons.onlyMore
            isFilesTabSelected -> buttons.searchWithFilter
            else -> buttons.searchWithMore
        }
    }

    private fun setEditingState(data: ConversationInformationToolbarData) {
        toolbar.apply {
            init(data)
            isEditingEnabled = true
            titleView?.apply {
                value = restoredEditTitleValue ?: data.title
                placeholder = getTitlePlaceholder(data.isChat)
                onValueChanged = { _, value ->
                    restoredEditTitleValue = null
                    updateDoneButton(value, data.title)
                }
                smallTitleMaxLines = EDITING_MAX_LINES
                requestFocus()
                setSelection(value.length)
            }
            subtitleView?.isVisible = false
            setButtons()
            updateDoneButton(titleView?.value ?: EMPTY, data.title)
        }
    }

    private fun SbisTopNavigationView.init(data: ConversationInformationToolbarData) {
        searchInput?.setSearchText(EMPTY)
        content = SbisTopNavigationContent.SmallTitle(
            title = PlatformSbisString.CharSequence(data.title),
            subtitle = PlatformSbisString.CharSequence(data.subtitle)
        )
        smallTitleMaxLines = DEFAULT_MAX_LINES
        showBackButton = true
        backBtn?.setOnClickListener {
            dispatch(Event.NavigateBack)
        }
        if (data.isGroup) {
            personView?.setDataList(data.photoDataList)
        }
        personView?.isVisible = data.isGroup
    }

    private fun SbisTopNavigationView.updateDoneButton(value: CharSequence, savedTitle: CharSequence) {
        val doneButton = rightBtnContainer?.getChildAt(0)?.castTo<FrameLayout>()?.getChildAt(0)
        doneButton?.let {
            it.isEnabled = (value.isNotBlank() || (value.isEmpty() && isDialog)) && value != savedTitle
        }
    }

    private fun setSearchingState() {
        toolbar.apply {
            content = SbisTopNavigationContent.SearchInput
            subscribeSearch()
            setButtons()
            showBackButton = false
            if (searchInput?.hasFocus() == false) {
                searchInput!!.requestFocus()
            }
        }
    }

    private fun subscribeSearch() {
        lifecycleOwner?.let {
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    toolbar.searchInput?.apply {
                        searchQueryChangedObservable().asFlow().collect { query ->
                            dispatch(Event.Search.QueryChanged(query))
                        }
                    }
                }
            }
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    toolbar.searchInput?.apply {
                        cancelSearchObservable().asFlow().collect {
                            setSearchText(EMPTY)
                        }
                    }
                }
            }
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    toolbar.searchInput?.apply {
                        searchFocusChangeObservable().asFlow().collect { hasFocus ->
                            val searchText = this.getSearchText()
                            when {
                                hasFocus -> KeyboardUtils.showKeyboard(this)
                                searchText.isEmpty() || searchText.isBlank() -> closeSearch()
                                else -> KeyboardUtils.hideKeyboard(this)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun closeSearch() {
        toolbar.searchInput?.let { KeyboardUtils.hideKeyboard(it) }
        setSearchText(EMPTY)
        dispatch(Event.Search.Close)
    }

    /** @SelfDocumented */
    fun onChangeTab() {
        toolbar.setButtons()
        if (toolbar.content is SbisTopNavigationContent.SearchInput) {
            closeSearch()
        }
    }

    fun getCurrentSearchQuery() = toolbar.searchInput?.getSearchText() ?: EMPTY

    /** @SelfDocumented */
    fun restoreEditValue(newValue: String) {
        restoredEditTitleValue = newValue
        if (toolbar.isEditingEnabled) {
            toolbar.titleView?.value = newValue
        }
    }

    /** @SelfDocumented */
    fun setSearchText(query: String) = with(toolbar) {
        if (content is SbisTopNavigationContent.SearchInput && searchInput?.getSearchText() != query) {
            searchInput?.setSearchText(query)
        }
    }

    private fun onChangeGroupAttribute(isGroupConversation: Boolean) = with(toolbar) {
        subtitleView?.isVisible = isGroupConversation
        personView?.isVisible = isGroupConversation
    }
}

private const val DEFAULT_MAX_LINES = 1
private const val EDITING_MAX_LINES = 5