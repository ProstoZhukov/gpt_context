package ru.tensor.sbis.design.picker_files_tab.view.store

import android.net.Uri
import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.gallery.decl.GalleryConfig
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabConfig

/**
 * MVI Store для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
internal interface PickerFilesTabStore :
    Store<PickerFilesTabStore.Intent, PickerFilesTabStore.State, PickerFilesTabStore.Label> {

    /**
     * MVI Intent для экрана "Вкладка Файлы".
     *
     * @author ai.abramenko
     */
    sealed interface Intent {

        object OnGalleryFolderClicked : Intent

        object OnStorageFolderClicked : Intent

        object OnBackClicked : Intent

        class OnStorageFilesSelected(val uris: List<Uri>) : Intent
    }

    /**
     * MVI Label для экрана "Вкладка Файлы".
     *
     * @author ai.abramenko
     */
    sealed interface Label {

        class ShowGalleryScreen(val galleryConfig: GalleryConfig) : Label

        class ShowStorageScreen(val isMultiply: Boolean) : Label

        object NavigateBack : Label

        class ShowSelectionLimitMessage(val limit: Int) : Label
    }

    /**
     * MVI Message для экрана "Вкладка Файлы".
     *
     * @author ai.abramenko
     */
    sealed interface Message

    /**
     * MVI Action для экрана "Вкладка Файлы".
     *
     * @author ai.abramenko
     */
    sealed interface Action {

        object Init : Action
    }

    /**
     * MVI State для экрана "Вкладка Файлы".
     *
     * @author ai.abramenko
     */
    @Parcelize
    class State(val config: PickerFilesTabConfig) : Parcelable
}