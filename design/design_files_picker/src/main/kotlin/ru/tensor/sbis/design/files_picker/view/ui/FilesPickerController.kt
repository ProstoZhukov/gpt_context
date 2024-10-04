package ru.tensor.sbis.design.files_picker.view.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.design.files_picker.view.store.FilesPickerStore
import ru.tensor.sbis.design.files_picker.view.store.FilesPickerStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.buffer.BufferStatePolicy
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/**
 * MVI Controller для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerController(
    fragment: Fragment,
    storeFactory: FilesPickerStoreFactory,
    viewFactory: FilesPickerView.Factory,
    private val router: FilesPickerRouter
) {

    private val store = fragment.provideStore(storeFactory::create)

    init {
        router.attachNavigator(
            WeakLifecycleNavigator(
                entity = fragment,
                bufferStatePolicy = BufferStatePolicy.ViewModel("FilesPickerController", fragment)
            )
        )
        fragment.attachBinder(mode = BinderLifecycleMode.CREATE_DESTROY, viewFactory = viewFactory) { view ->
            bind {
                view.render(handleState(store.state))
                store.states.map(::handleState) bindTo view
                view.events.map(::handleViewEvents) bindTo store
                store.labels bindTo ::handleLabels
            }
        }
    }

    fun onBackPressed(): Boolean =
        router.navigateBack()

    // region State
    private fun handleState(state: FilesPickerStore.State): FilesPickerView.ViewModel =
        FilesPickerView.ViewModel(
            tabPanelItems = state.tabPanelItems,
            selectedTabPanelItem = state.selectedTabPanelItem,
            headerViewModel = FilesPickerView.HeaderViewModel(
                isVisible = state.isControlsVisible,
                isCounterVisible = state.selectedPickedItems.isNotEmpty(),
                selectedCounter = state.selectedPickedItems.size,
                isMenuButtonVisible = state.isMenuVisible,
                backgroundColor = state.appliedHeaderColor
            ),
            footerViewModel = FilesPickerView.FooterViewModel(
                isVisible = state.isControlsVisible,
                state = if (state.selectedPickedItems.isNotEmpty()) {
                    FilesPickerView.FooterViewModel.State.ADD_BUTTON
                } else {
                    FilesPickerView.FooterViewModel.State.TAB_PANEL
                }
            )
        )
    // endregion

    // region ViewEvents
    private fun handleViewEvents(event: FilesPickerView.Event): FilesPickerStore.Intent =
        when (event) {
            is FilesPickerView.Event.OnAddButtonClick -> handleViewEvent(event)
            is FilesPickerView.Event.OnCancelButtonClick -> handleViewEvent(event)
            is FilesPickerView.Event.OnMenuButtonClick -> handleViewEvent(event)
            is FilesPickerView.Event.OnTabSelected -> handleViewEvent(event)
        }

    @Suppress("UNUSED_PARAMETER")
    private fun handleViewEvent(event: FilesPickerView.Event.OnAddButtonClick): FilesPickerStore.Intent =
        FilesPickerStore.Intent.OnAddButtonClick

    @Suppress("UNUSED_PARAMETER")
    private fun handleViewEvent(event: FilesPickerView.Event.OnCancelButtonClick): FilesPickerStore.Intent =
        FilesPickerStore.Intent.OnCancelButtonClick

    private fun handleViewEvent(event: FilesPickerView.Event.OnMenuButtonClick): FilesPickerStore.Intent =
        FilesPickerStore.Intent.OnMenuButtonClick(
            event.verticalLocator,
            event.horizontalLocator
        )

    private fun handleViewEvent(event: FilesPickerView.Event.OnTabSelected): FilesPickerStore.Intent =
        FilesPickerStore.Intent.OnTabPanelItemSelected(event.tabPanelItem)
    // endregion

    // region Labels
    private fun handleLabels(label: FilesPickerStore.Label) {
        when (label) {
            is FilesPickerStore.Label.Close -> handleLabel(label)
            is FilesPickerStore.Label.ShowMenu -> handleLabel(label)
            is FilesPickerStore.Label.ShowTabScreen -> handleLabel(label)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLabel(label: FilesPickerStore.Label.Close) {
        router.close()
    }

    private fun handleLabel(label: FilesPickerStore.Label.ShowMenu) {
        router.showMenu(
            menu = label.menu,
            verticalLocator = label.verticalLocator,
            horizontalLocator = label.horizontalLocator
        )
    }

    private fun handleLabel(label: FilesPickerStore.Label.ShowTabScreen) {
        router.showTabScreen(label.clickAction)
    }
    // endregion
}