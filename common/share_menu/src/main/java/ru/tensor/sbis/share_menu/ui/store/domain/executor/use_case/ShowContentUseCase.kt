package ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case

import ru.tensor.sbis.share_menu.ui.store.domain.ShareHandlersProvider
import ru.tensor.sbis.share_menu.ui.data.ShareMenuTabItem
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.BaseExecutorUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.ui.store.domain.Message
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import javax.inject.Inject

/**
 * Use-case по обработке показа контента внутри меню шаринга.
 *
 * @author vv.chekurda
 */
internal class ShowContentUseCase @Inject constructor(
    private val shareData: ShareData,
    private val quickShareKey: String?,
    private val shareHandlersProvider: ShareHandlersProvider
) : BaseExecutorUseCase<State, Message, Label>() {

    /**
     * Показать контент вкладки [tab].
     * @return true, если контент отобразился в меню. false - если открылся внешний экран.
     */
    fun showTabContent(tab: ShareMenuTabItem): Boolean =
        shareHandlersProvider.appShareHandlers
            .first { handler -> handler.menuItem.id == tab.id }
            .showContent()

    /**
     * Показать контент для быстрого шаринга.
     * @return true, если контент отобразился в меню. false - если открылся внешний экран.
     */
    fun showQuickShareContent(): Boolean? =
        shareHandlersProvider.appShareHandlers
            .find { handler -> handler.isQuickShareSupported(quickShareKey!!) }
            ?.also { dispatch(State.OnQuickShareStarted(handler = it)) }
            ?.showContent()

    private fun ShareHandler.showContent(): Boolean =
        getShareContent(shareData, quickShareKey).let { content ->
            val showMenu = content != null
            if (showMenu) {
                publish(Label.NavigationLabel.ShowContent(requireNotNull(content)))
            } else {
                publish(Label.NavigationLabel.OpenScreen { context ->
                    requireNotNull(getShareContentIntent(context, shareData, quickShareKey)) {
                        "If share supported - you must override getNavItemContent or getNavItemIntent"
                    }
                })
            }
            showMenu
        }
}