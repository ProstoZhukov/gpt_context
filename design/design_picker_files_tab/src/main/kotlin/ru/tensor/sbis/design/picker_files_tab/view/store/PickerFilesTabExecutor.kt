package ru.tensor.sbis.design.picker_files_tab.view.store

import android.net.Uri
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.files_picker.decl.GallerySelectionMode
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.gallery.decl.GalleryComponent
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.gallery.decl.GalleryEvent
import ru.tensor.sbis.design.gallery.decl.GalleryMode
import ru.tensor.sbis.design.picker_files_tab.feature.PickerFilesTabFeature
import ru.tensor.sbis.design.picker_files_tab.utils.mapToPickedItems

/**
 * MVI Executor для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
internal class PickerFilesTabExecutor @AssistedInject constructor(
    private val tabFeature: PickerFilesTabFeature,
    galleryComponent: GalleryComponent
) : CoroutineExecutor<
    PickerFilesTabStore.Intent,
    PickerFilesTabStore.Action,
    PickerFilesTabStore.State,
    PickerFilesTabStore.Message,
    PickerFilesTabStore.Label>() {

    @AssistedFactory
    interface Factory {
        fun create(): PickerFilesTabExecutor
    }

    init {
        galleryComponent
            .events
            .onEach(::onGalleryEvent)
            .launchIn(scope)
    }

    override fun executeAction(action: PickerFilesTabStore.Action, getState: () -> PickerFilesTabStore.State) {
        val state = getState()
        when (action) {
            PickerFilesTabStore.Action.Init ->
                if (state.config.tab.isGalleryEnabled.not()) {
                    onStorageFolderClicked(tab = state.config.tab)
                }
        }
    }

    override fun executeIntent(intent: PickerFilesTabStore.Intent, getState: () -> PickerFilesTabStore.State) {
        val state = getState()
        when (intent) {
            is PickerFilesTabStore.Intent.OnGalleryFolderClicked ->
                onGalleryFolderClicked(tab = state.config.tab)
            is PickerFilesTabStore.Intent.OnStorageFolderClicked ->
                onStorageFolderClicked(tab = state.config.tab)
            is PickerFilesTabStore.Intent.OnStorageFilesSelected ->
                onStorageFilesSelected(uris = intent.uris, tab = state.config.tab)
            is PickerFilesTabStore.Intent.OnBackClicked ->
                onBackClicked()
        }
    }

    private fun onGalleryFolderClicked(tab: SbisFilesPickerTab.Files) {
        tabFeature.isControlsVisible = false
        safePublish(
            PickerFilesTabStore.Label.ShowGalleryScreen(
                GalleryConfig(
                    mode = GalleryMode.ByAlbums(),
                    selectionMode = if (tab.filesSelectionLimit == 1)
                        GallerySelectionMode.Single(imageCropParams = tab.cropParams)
                    else
                        GallerySelectionMode.Multiple(tab.filesSelectionLimit),
                    sizeInMBytesLimit = tab.fileSizeInMBytesLimit,
                    needOnlyImages = false
                )
            )
        )
    }

    private fun onStorageFolderClicked(tab: SbisFilesPickerTab.Files) {
        safePublish(
            PickerFilesTabStore.Label.ShowStorageScreen(isMultiply = tab.filesSelectionLimit > 1)
        )
    }

    private fun onStorageFilesSelected(
        uris: List<Uri>,
        tab: SbisFilesPickerTab.Files
    ) {
        if (uris.size > tab.filesSelectionLimit) {
            safePublish(
                PickerFilesTabStore.Label.ShowSelectionLimitMessage(limit = tab.filesSelectionLimit)
            )
            return
        }
        tabFeature.onForcedSelection(files = uris.mapToPickedItems())
    }

    private fun onBackClicked() {
        safePublish(
            PickerFilesTabStore.Label.NavigateBack
        )
        tabFeature.isControlsVisible = true
    }

    private fun onGalleryEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.OnAddButtonClick ->
                tabFeature.onForcedSelection(event.selectedFiles)
            is GalleryEvent.OnCancelButtonClick -> {
                safePublish(
                    PickerFilesTabStore.Label.NavigateBack
                )
                tabFeature.isControlsVisible = true
            }
            else -> Unit
        }
    }

    private fun safePublish(label: PickerFilesTabStore.Label) {
        inMain { publish(label) }
    }

    private fun inMain(block: () -> Unit) {
        scope.launch(Dispatchers.Main) { block() }
    }
}