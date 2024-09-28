package ru.tensor.sbis.share_menu.ui.view

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.share_menu.ui.store.domain.executor.Intent
import ru.tensor.sbis.share_menu.ui.store.domain.executor.Intent.ContentDelegateIntent
import ru.tensor.sbis.share_menu.ui.store.ShareMenuStore
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState

/**
 * Делегат контента меню для поделиться для управления его состоянием и внешним видом.
 *
 * @author vv.chekurda
 */
internal class ShareMenuContentDelegate(
    private val store: ShareMenuStore
) : ShareMenuDelegate,
    InstanceKeeper.Instance {

    override val bottomOffset = MutableStateFlow(0)

    override fun changeNavPanelVisibility(isVisible: Boolean) {
        ContentDelegateIntent.ChangeTabPanelVisibility(isVisible = isVisible).produce()
    }

    override fun changeHeightMode(mode: ShareMenuHeightMode) {
        ContentDelegateIntent.ChangeHeightMode(mode = mode).produce()
    }

    override fun dismiss() {
        ContentDelegateIntent.Dismiss.produce()
    }

    override fun changeBackButtonVisibility(isVisible: Boolean) {
        ContentDelegateIntent.ChangeBackButtonVisibility(isVisible = isVisible).produce()
    }

    override fun changeLoadingState(state: ShareMenuLoadingState) {
        ContentDelegateIntent.ChangeLoadingState(state = state).produce()
    }

    /**
     * Обновить нижний отступ от края меню до контента.
     */
    fun updateBottomOffset(offset: Int) {
        bottomOffset.tryEmit(offset)
    }

    override fun onDestroy() = Unit

    private fun Intent.produce() {
        store.accept(this)
    }
}