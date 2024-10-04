package ru.tensor.sbis.design.files_picker.view.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.files_picker.feature.SbisFilesPickerImpl
import ru.tensor.sbis.design.files_picker.view.logAttachProcess
import ru.tensor.sbis.design.tab_panel.TabPanelItem

/**
 * MVI Executor для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerExecutor(
    private val tabFeatures: List<SbisFilesPickerTabFeature<*>>,
    private val filesPickerFeature: SbisFilesPickerImpl
) : CoroutineExecutor<
    FilesPickerStore.Intent,
    FilesPickerStore.Action,
    FilesPickerStore.State,
    FilesPickerStore.Message,
    FilesPickerStore.Label>() {

    fun interface Factory : () -> FilesPickerExecutor

    private var tabFeatureJob: Job? = null
    private var currentTabFeature: SbisFilesPickerTabFeature<*>? = null

    override fun executeAction(action: FilesPickerStore.Action, getState: () -> FilesPickerStore.State) {
        when (action) {
            is FilesPickerStore.Action.InitSelectedTab -> executeAction(action, getState)
        }
    }

    // region Actions
    @Suppress("UNUSED_PARAMETER")
    private fun executeAction(action: FilesPickerStore.Action.InitSelectedTab, getState: () -> FilesPickerStore.State) {
        getState().selectedTabPanelItem.activate()
    }
    // endregion

    override fun executeIntent(intent: FilesPickerStore.Intent, getState: () -> FilesPickerStore.State) {
        when (intent) {
            is FilesPickerStore.Intent.OnAddButtonClick -> executeIntent(intent, getState)
            is FilesPickerStore.Intent.OnCancelButtonClick -> executeIntent(intent, getState)
            is FilesPickerStore.Intent.OnMenuButtonClick -> executeIntent(intent, getState)
            is FilesPickerStore.Intent.OnTabPanelItemSelected -> executeIntent(intent, getState)
        }
    }

    // region Intents
    @Suppress("UNUSED_PARAMETER")
    private fun executeIntent(
        intent: FilesPickerStore.Intent.OnAddButtonClick,
        getState: () -> FilesPickerStore.State
    ) {
        val state = getState()
        tabFeatures.get(state.selectedTabPanelItem).addButtonCustomClickAction?.invoke()
            ?: sendSelectedItems(selectedItems = state.selectedPickedItems, isCompress = state.isCompressImages)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun executeIntent(
        intent: FilesPickerStore.Intent.OnCancelButtonClick,
        getState: () -> FilesPickerStore.State
    ) {
        safePublish(
            FilesPickerStore.Label.Close
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun executeIntent(
        intent: FilesPickerStore.Intent.OnMenuButtonClick,
        getState: () -> FilesPickerStore.State
    ) {
        val menu = currentTabFeature?.tabSettings?.menu
            ?: kotlin.run {
                illegalState { "Menu is null." }
                return
            }
        safePublish(
            FilesPickerStore.Label.ShowMenu(
                menu = menu,
                verticalLocator = intent.verticalLocator,
                horizontalLocator = intent.horizontalLocator
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun executeIntent(
        intent: FilesPickerStore.Intent.OnTabPanelItemSelected,
        getState: () -> FilesPickerStore.State
    ) {
        intent.tabPanelItem.activate()
    }
    // endregion

    private fun TabPanelItem.activate() {
        dispatch(
            FilesPickerStore.Message.SetSelectedTabPanelItem(tabPanelItem = this)
        )
        tabFeatures.get(this).let {
            subscribeOn(it)
            safePublish(
                FilesPickerStore.Label.ShowTabScreen(it.clickAction)
            )
            dispatch(
                FilesPickerStore.Message.SetMenuVisible(it.isMenuVisible())
            )
            dispatch(
                FilesPickerStore.Message.SetAppliedHeaderColor(it.tabSettings.backgroundColor)
            )
        }
    }

    private fun subscribeOn(tabFeature: SbisFilesPickerTabFeature<*>) {
        if (currentTabFeature != tabFeature) {
            logAttachProcess("subscribeOnTabEvents, tab - $tabFeature")
            currentTabFeature = tabFeature
            tabFeatureJob?.cancel()
            tabFeatureJob = tabFeature.event.onEach(::handleTabFeatureEvents).launchIn(scope)
        }
    }

    private fun handleTabFeatureEvents(event: SbisFilesPickerTabEvent) {
        when (event) {
            is SbisFilesPickerTabEvent.Cancel ->
                safePublish(
                    FilesPickerStore.Label.Close
                )
            is SbisFilesPickerTabEvent.OnItemsSelected -> {
                logAttachProcess("handleOnItemsSelectedEvent, uri - ${event.selectedItems}")
                dispatch(
                    FilesPickerStore.Message.SetSelectedPickedItems(
                        selectedPickedItems = event.selectedItems,
                        isCompressImages = event.compressImages
                    )
                )
                if (event.pushRightNow) {
                    sendSelectedItems(selectedItems = event.selectedItems, isCompress = event.compressImages)
                }
            }
            is SbisFilesPickerTabEvent.SwitchControlsVisibility ->
                dispatch(
                    FilesPickerStore.Message.SetControlsVisible(isVisible = event.isVisible)
                )
        }
    }

    private fun sendSelectedItems(selectedItems: List<SbisPickedItem>, isCompress: Boolean) {
        logAttachProcess("sendSelectedItems(), items - $selectedItems")
        filesPickerFeature.onUnitsSelected(
            selectedItems = selectedItems,
            compressImages = isCompress
        )
        safePublish(
            FilesPickerStore.Label.Close
        )
    }

    private fun safePublish(label: FilesPickerStore.Label) {
        scope.launch {
            withContext(Dispatchers.Main) {
                publish(label)
            }
        }
    }
}

fun List<SbisFilesPickerTabFeature<*>>.get(tabPanelItem: TabPanelItem): SbisFilesPickerTabFeature<*> =
    find { it.tabPanelItem == tabPanelItem }!!

fun SbisFilesPickerTabFeature<*>.isMenuVisible(): Boolean =
    tabSettings.menu != null