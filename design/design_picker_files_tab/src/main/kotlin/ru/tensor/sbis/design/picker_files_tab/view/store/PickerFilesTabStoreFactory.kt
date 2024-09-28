package ru.tensor.sbis.design.picker_files_tab.view.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.picker_files_tab.view.PickerFilesTabConfig
import ru.tensor.sbis.design.picker_files_tab.view.di.PickerFilesTabDIScope
import ru.tensor.sbis.mvi_extension.create
import javax.inject.Inject

/**
 * Фабрика [PickerFilesTabStore] для экрана "Вкладка Файлы".
 *
 * @author ai.abramenko
 */
@PickerFilesTabDIScope
internal class PickerFilesTabStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val reducer: PickerFilesTabReducer,
    private val config: PickerFilesTabConfig,
    private val executorFactory: PickerFilesTabExecutor.Factory
) {

    fun create(stateKeeper: StateKeeper): PickerFilesTabStore =
        object :
            PickerFilesTabStore,
            Store<
                PickerFilesTabStore.Intent,
                PickerFilesTabStore.State,
                PickerFilesTabStore.Label
                > by storeFactory.create(
                stateKeeper = stateKeeper,
                name = PickerFilesTabStoreFactory::class.java.simpleName,
                initialState = createInitialState(config),
                bootstrapper = SimpleBootstrapper(PickerFilesTabStore.Action.Init),
                executorFactory = { executorFactory.create() },
                reducer = reducer
            ) {}

    private fun createInitialState(config: PickerFilesTabConfig): PickerFilesTabStore.State =
        PickerFilesTabStore.State(config = config)
}