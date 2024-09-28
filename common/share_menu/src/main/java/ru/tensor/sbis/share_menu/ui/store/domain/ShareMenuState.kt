package ru.tensor.sbis.share_menu.ui.store.domain

import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabItem
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabsData
import ru.tensor.sbis.share_menu.ui.view.header.toShareHeaderViewState
import ru.tensor.sbis.share_menu.utils.base_mvi.reducer.ReducerUseCase
import ru.tensor.sbis.share_menu.ui.view.ShareMenuView.Model
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState

/**
 * Сообщения для изменения состояния компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal interface Message : ReducerUseCase<State>

/**
 * Состояние компонента меню для "поделиться".
 *
 * @property shareData данные, которыми делится пользователь.
 * @property availableHandlers доступные обработчики шаринга, в которые пользователь может поделиться.
 * @property loadingState состояние загрузки контента "поделиться".
 * @property isBackButtonVisible признак видимости кнопки назад в шапке.
 * @property isTabPanelVisible признак видимости панели навигационных вкладок.
 * @property tabPanelHeight высота панели вкладок.
 * @property tabsData данные о вкладках для панели.
 * @property heightMode режим измерения высоты контейнера контента.
 * @property quickShareHandler текущий обработчик быстрого шаринга.
 * @property isAutoCloseTimerStarted признак активированного таймера на закрытие экрана.
 */
internal data class State(
    val shareData: ShareData,
    val availableHandlers: List<ShareHandler> = emptyList(),
    val loadingState: ShareMenuLoadingState = ShareMenuLoadingState.None,
    val isBackButtonVisible: Boolean = false,
    val isTabPanelVisible: Boolean = true,
    val tabPanelHeight: Int = 0,
    val tabsData: ShareMenuTabsData = ShareMenuTabsData(),
    val heightMode: ShareMenuHeightMode = ShareMenuHeightMode.Full,
    val quickShareHandler: ShareHandler? = null,
    val isAutoCloseTimerStarted: Boolean = false
) {
    /** UI состояние меню. */
    val uiState: Model
        get() = Model(
            headerState = loadingState.toShareHeaderViewState(),
            isBackButtonVisible = isBackButtonVisible,
            isTabPanelVisible = isTabPanelVisible,
            filesCount = shareData.files.size,
            tabsData = tabsData,
            heightMode = heightMode
        )

    /** Изменить видимость панели вкладок. */
    class ChangeTabPanelVisibility(private val isVisible: Boolean) : Message {
        override fun State.reduce(): State =
            copy(isTabPanelVisible = isVisible)
    }

    /** Изменить видимость кнопки назад в шапке. */
    class ChangeBackButtonVisibility(private val isVisible: Boolean) : Message {
        override fun State.reduce(): State =
            copy(isBackButtonVisible = isVisible)
    }

    /** Изменить видимость кнопки назад в шапке. */
    class ChangeHeightMode(private val mode: ShareMenuHeightMode) : Message {
        override fun State.reduce(): State =
            copy(heightMode = mode)
    }

    /** Изменить состояние загрузки. */
    class ChangeLoadingState(private val state: ShareMenuLoadingState) : Message {
        override fun State.reduce(): State =
            copy(loadingState = state)
    }

    /** Изменилась выбранныя вкладка. */
    class OnTabSelected(private val item: ShareMenuTabItem) : Message {
        override fun State.reduce(): State =
            copy(tabsData = tabsData.copy(selected = item))
    }

    /** Изменилась высота панели вкладок. */
    class OnTabPanelHeightChanged(private val height: Int) : Message {
        override fun State.reduce(): State =
            copy(tabPanelHeight = height)
    }

    /** Начался быстрый шаринг для обработчика [handler]. */
    class OnQuickShareStarted(private val handler: ShareHandler) : Message {
        override fun State.reduce(): State =
            copy(quickShareHandler = handler)
    }

    /** Запустился таймер на автоматическое закрытие меню. */
    object OnAutoCloseTimerStarted : Message {
        override fun State.reduce(): State =
            copy(isAutoCloseTimerStarted = true)
    }

    /** Изменился список доступных обработчиков для "поделиться". */
    class OnAvailableHandlersChanged(private val availableHandlers: List<ShareHandler>) : Message {
        override fun State.reduce(): State {
            val navItems = this@OnAvailableHandlersChanged.availableHandlers.map { it.menuItem }
            return copy(
                availableHandlers = availableHandlers,
                tabsData = tabsData.copy(items = navItems.map(::ShareMenuTabItem)),
                isTabPanelVisible = if (availableHandlers.size == 1) false else isTabPanelVisible
            )
        }
    }
}