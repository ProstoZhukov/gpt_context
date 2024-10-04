package ru.tensor.sbis.design.picker_files_tab.view.ui

import android.content.Intent
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.design.files_picker.view.pushSelectionLimitNotification
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabFragment
import ru.tensor.sbis.design.picker_files_tab.view.di.PickerFilesTabDIScope
import ru.tensor.sbis.design.picker_files_tab.view.store.PickerFilesTabStore
import ru.tensor.sbis.design.picker_files_tab.view.store.PickerFilesTabStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.buffer.BufferStatePolicy
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import javax.inject.Inject

/**
 * MVI Controller для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@PickerFilesTabDIScope
internal class PickerFilesTabController @Inject constructor(
    private val fragment: PickerFilesTabFragment,
    private val storeFactory: PickerFilesTabStoreFactory,
    viewFactory: PickerFilesTabView.Factory,
    private val router: PickerFilesTabRouter
) {

    private val store = fragment.provideStore(storeFactory::create)

    init {
        router.attachNavigator(
            WeakLifecycleNavigator(
                entity = fragment,
                bufferStatePolicy = BufferStatePolicy.ViewModel("PickerFilesTabController", fragment)
            )
        )
        fragment.attachBinder(mode = BinderLifecycleMode.CREATE_DESTROY, viewFactory = viewFactory) { view ->
            bind {
                view.render(mapViewModel(store.state))
                store.states.map(::mapViewModel) bindTo view
                view.events.map(::viewEventToIntent) bindTo store
                store.labels bindTo { handleLabel(label = it) }
            }
        }
    }

    /**
     * Событие о выборе файлов из внутреннего хранилища
     */
    fun onSelectFilesFromStorage(data: Intent) {
        store.accept(PickerFilesTabStore.Intent.OnStorageFilesSelected(FileUriUtil.getUrisFromIntent(data)))
    }

    /**
     * Событие о нажатии кнопки назад
     */
    fun onBackPressed(): Boolean {
        if (router.isCanNavigateBack()) {
            store.accept(PickerFilesTabStore.Intent.OnBackClicked)
            return true
        }
        return false
    }

    private fun mapViewModel(state: PickerFilesTabStore.State): PickerFilesTabView.ViewModel =
        PickerFilesTabView.ViewModel(isGalleryVisible = state.config.tab.isGalleryEnabled)

    private fun viewEventToIntent(event: PickerFilesTabView.Event): PickerFilesTabStore.Intent =
        when (event) {

            is PickerFilesTabView.Event.OnGalleryFolderClick ->
                PickerFilesTabStore.Intent.OnGalleryFolderClicked
            is PickerFilesTabView.Event.OnStorageFolderClick ->
                PickerFilesTabStore.Intent.OnStorageFolderClicked
        }

    private fun handleLabel(label: PickerFilesTabStore.Label) {
        when (label) {
            is PickerFilesTabStore.Label.ShowGalleryScreen ->
                router.openGalleryScreen(label.galleryConfig)
            is PickerFilesTabStore.Label.ShowStorageScreen ->
                router.openStorageScreen(isMultiply = label.isMultiply)
            is PickerFilesTabStore.Label.NavigateBack ->
                router.navigateBack()
            is PickerFilesTabStore.Label.ShowSelectionLimitMessage ->
                pushSelectionLimitNotification(resources = fragment.resources, selectionLimit = label.limit)
        }
    }
}