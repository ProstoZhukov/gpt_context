package ru.tensor.sbis.design.files_picker.view.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.mvi_extension.create

/**
 * Фабрика [FilesPickerStore] для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerStoreFactory(
    private val storeFactory: StoreFactory,
    private val reducer: FilesPickerReducer,
    private val executorFactory: FilesPickerExecutor.Factory,
    private val tabFeatures: List<SbisFilesPickerTabFeature<*>>,
) {

    fun create(stateKeeper: StateKeeper): FilesPickerStore =
        kotlin.run {
            val tabPanelItems = tabFeatures.map { it.tabPanelItem }
            val selectedTabPanelItem = tabPanelItems.first()
            val tabFeature = tabFeatures.get(selectedTabPanelItem)
            object :
                FilesPickerStore,
                Store<FilesPickerStore.Intent, FilesPickerStore.State, FilesPickerStore.Label> by storeFactory.create(
                    stateKeeper = stateKeeper,
                    name = FilesPickerStore::class.java.simpleName,
                    initialState = FilesPickerStore.State(
                        selectedPickedItems = emptyList(),
                        isCompressImages = false,
                        tabPanelItems = tabPanelItems,
                        selectedTabPanelItem = selectedTabPanelItem,
                        isControlsVisible = true,
                        isMenuVisible = tabFeature.isMenuVisible(),
                        appliedHeaderColor = tabFeature.tabSettings.backgroundColor
                    ),
                    bootstrapper = SimpleBootstrapper(FilesPickerStore.Action.InitSelectedTab),
                    executorFactory = executorFactory,
                    reducer = reducer
                ) {}
        }
}