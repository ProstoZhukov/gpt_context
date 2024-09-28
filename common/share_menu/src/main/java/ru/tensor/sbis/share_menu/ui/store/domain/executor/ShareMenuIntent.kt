package ru.tensor.sbis.share_menu.ui.store.domain.executor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabItem
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.BaseExecutorUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.Message
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case.ShowContentUseCase
import ru.tensor.sbis.share_menu.ui.view.ShareMenuView
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState
import javax.inject.Inject
import javax.inject.Provider

/**
 * Намерения компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal abstract class Intent : BaseExecutorUseCase<State, Message, Label>() {

    /**
     * Фабрика для создания намерений.
     */
    class Factory @Inject constructor(
        private val contentUseCaseProvider: Provider<ShowContentUseCase>
    ) {
        /**
         * Создать намерение по ui событию.
         */
        fun create(event: ShareMenuView.Event): Intent =
            when (event) {
                ShareMenuView.Event.OnBackButtonClicked -> HandleBackPress
                ShareMenuView.Event.OnCloseButtonClicked -> CloseMenu
                is ShareMenuView.Event.OnTabPanelHeightChanged -> HandleTabPanelHeightChange(height = event.height)
                is ShareMenuView.Event.OnTabSelected -> ShowTabContent(event.item, contentUseCaseProvider.get())
            }
    }

    /** Обработать нажатие кнопки назад. */
    object HandleBackPress : Intent() {
        override fun execute(getState: () -> State) {
            publish(Label.NavigationLabel.HandleBackPressed)
        }
    }

    /** Закрыть меню. */
    object CloseMenu : Intent() {
        override fun execute(getState: () -> State) {
            publish(Label.NavigationLabel.FinishTask)
        }
    }

    /** Обработать изменение высота панели вкладок. */
    class HandleTabPanelHeightChange(val height: Int) : Intent() {
        override fun execute(getState: () -> State) {
            val offset = if (getState().isTabPanelVisible) height else 0
            dispatch(State.OnTabPanelHeightChanged(height = height))
            publish(Label.UpdateBottomOffset(offset = offset))
        }
    }

    /** Показать контент вкладки [tab]. */
    class ShowTabContent(
        private val tab: ShareMenuTabItem,
        private val contentUseCase: ShowContentUseCase
    ) : Intent() {

        override val subUseCases = listOf(contentUseCase)

        override fun execute(getState: () -> State) {
            val isContentShown = contentUseCase.showTabContent(tab)
            if (isContentShown) {
                dispatch(State.OnTabSelected(item = tab))
            }
        }
    }

    /** Намерения делегата контента. */
    abstract class ContentDelegateIntent : Intent() {

        /** Изменить видимость панели вкладок. */
        class ChangeTabPanelVisibility(val isVisible: Boolean) : ContentDelegateIntent() {
            override fun execute(getState: () -> State) {
                val offset = if (getState().isTabPanelVisible) getState().tabPanelHeight else 0
                publish(Label.UpdateBottomOffset(offset = offset))
                dispatch(State.ChangeTabPanelVisibility(isVisible = isVisible && getState().tabsData.items.size > 1))
            }
        }

        /** Изменить режим измерения высоты меню. */
        class ChangeHeightMode(val mode: ShareMenuHeightMode) : ContentDelegateIntent() {
            override fun execute(getState: () -> State) {
                dispatch(State.ChangeHeightMode(mode = mode))
            }
        }

        /** Закрыть меню. */
        object Dismiss : ContentDelegateIntent() {
            override fun execute(getState: () -> State) {
                publish(Label.NavigationLabel.FinishTask)
            }
        }

        /** Изменить видимость кнопки назад. */
        class ChangeBackButtonVisibility(val isVisible: Boolean) : ContentDelegateIntent() {
            override fun execute(getState: () -> State) {
                dispatch(State.ChangeBackButtonVisibility(isVisible))
            }
        }

        /** Изменить состояние загрузки данных, которыми "делятся". */
        class ChangeLoadingState(val state: ShareMenuLoadingState) : ContentDelegateIntent() {
            override fun execute(getState: () -> State) {
                dispatch(State.ChangeLoadingState(state = state))
                checkAutoCloseTimer(getState)
            }

            private fun checkAutoCloseTimer(getState: () -> State) {
                if (getState().isAutoCloseTimerStarted) return
                dispatch(State.OnAutoCloseTimerStarted)
                logAnalyticsSendEvent(getState)
                scope.launch(Dispatchers.IO) {
                    delay(AUTO_CLOSE_ON_START_SENDING_DELAY_MS)
                    withContext(Dispatchers.Main) {
                        publish(Label.NavigationLabel.FinishTask)
                    }
                }
            }

            private fun logAnalyticsSendEvent(getState: () -> State) {
                val state = getState()
                val selectedTab = state.tabsData.selected
                if (selectedTab != null) {
                    state.availableHandlers.find { selectedTab.id == it.menuItem.id }?.analyticHandlerName
                } else {
                    state.quickShareHandler?.analyticHandlerName
                }?.also { name ->
                    publish(Label.LogAnalyticEvent(name = name, isQuickShare = selectedTab != null))
                }
            }
        }
    }
}

private const val AUTO_CLOSE_ON_START_SENDING_DELAY_MS = 1000L