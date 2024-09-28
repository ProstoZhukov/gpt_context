package ru.tensor.sbis.share_menu.ui.store.domain.executor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communication_decl.analytics.model.OpenedSharedExtension
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.share_menu.ShareMenuPlugin
import ru.tensor.sbis.share_menu.ui.store.domain.ShareHandlersProvider
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabItem
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.BaseExecutorUseCase
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.ExecutorUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.Message
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case.AttachmentsUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case.ShowContentUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case.ShowLoginUseCase
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import javax.inject.Inject

internal abstract class Action : BaseExecutorUseCase<State, Message, Label>()

/**
 * Действие инициализации меню для поделиться.
 *
 * @property shareData данные, которыми делится пользователь.
 * @property quickShareKey ключ для быстрого шаринга.
 * @property shareHandlersProvider поставщик обработчиков шаринга.
 * @property loginUseCase use-case для работы с авторизацией.
 * @property attachmentsUseCase use-case для работы с вложениями.
 * @property contentUseCase use-case для работы с контентом меню.
 *
 * @author vv.chekurda
 */
internal class ShareMenuInitAction @Inject constructor(
    private val shareData: ShareData,
    private val quickShareKey: String?,
    private val shareHandlersProvider: ShareHandlersProvider,
    private val loginUseCase: ShowLoginUseCase,
    private val attachmentsUseCase: AttachmentsUseCase,
    private val contentUseCase: ShowContentUseCase
) : Action() {

    override val subUseCases: List<ExecutorUseCase<State, Message, Label>>
        get() = listOf(
            loginUseCase,
            attachmentsUseCase,
            contentUseCase
        )

    override fun execute(getState: () -> State) {
        val isAuthorized = loginUseCase.showLoginScreen()
        if (!isAuthorized) return
        showMenuAfterCheck(getState)
    }

    private fun showMenuAfterCheck(getState: () -> State) {
        if ((shareData.text?.length ?: 0) > MAX_TEXT_LENGTH) {
            publish(
                Label.ShowErrorMessage(
                    message = PlatformSbisString.Res(R.string.share_menu_text_restriction),
                    withFinish = true
                )
            )
        } else {
            attachmentsUseCase.withCheckAttachments {
                ShareMenuPlugin.menuDependency.analyticsUtil?.sendAnalytics(
                    OpenedSharedExtension(ShareMenuInitAction::class.java.simpleName)
                )
                showMenu(getState)
            }
        }
    }

    private fun showMenu(getState: () -> State) {
        if (quickShareKey == null) {
            showShareMenu(getState)
        } else {
            showQuickShareMenu()
        }
    }

    private fun showShareMenu(getState: () -> State) {
        scope.launch {
            with(shareHandlersProvider) {
                val currentAvailableHandlers = getAvailableHandlers(shareData)
                onAvailableHandlersChanged(currentAvailableHandlers, getState)
                getAvailableHandlersFlow(shareData).distinctUntilChanged()
                    .collect { handlers -> onAvailableHandlersChanged(handlers, getState) }
            }
        }
    }

    private suspend fun onAvailableHandlersChanged(
        handlers: List<ShareHandler>,
        getState: () -> State
    ) {
        withContext(Dispatchers.Main) {
            dispatch(State.OnAvailableHandlersChanged(handlers))
            if (handlers.isNotEmpty()) {
                showMenuWithTabs(
                    tabs = handlers.map { ShareMenuTabItem(it.menuItem) },
                    getState = getState
                )
            } else {
                publish(
                    Label.ShowErrorMessage(
                        message = PlatformSbisString.Res(R.string.share_menu_no_available_options),
                        withFinish = true
                    )
                )
            }
        }
    }

    private fun showMenuWithTabs(tabs: List<ShareMenuTabItem>, getState: () -> State) {
        val containsSelectedTab = tabs.find { it.id == getState().tabsData.selected?.id } != null
        val isFirstOpen = tabs.isNotEmpty() && !containsSelectedTab
        if (isFirstOpen) {
            val selectedTab = tabs.find { it.navItem.canBeSelected }
            selectedTab?.also { tab ->
                dispatch(State.OnTabSelected(tab))
                contentUseCase.showTabContent(tab)
            }
            publish(Label.ShowMenuContainer)
        }
    }

    private fun showQuickShareMenu() {
        dispatch(State.ChangeTabPanelVisibility(isVisible = false))
        val showMenu = contentUseCase.showQuickShareContent()
            ?: run {
                publish(
                    Label.ShowErrorMessage(
                        message = PlatformSbisString.Res(R.string.share_menu_no_available_options),
                        withFinish = true
                    )
                )
                false
            }
        if (showMenu) {
            publish(Label.ShowMenuContainer)
        }
    }
}

private const val MAX_TEXT_LENGTH = 16240