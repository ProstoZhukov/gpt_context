package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Вспомогательный класс для работы со свайп меню.
 *
 * @author da.zhukov
 */
internal class CRMChatSwipeMenuHelper(val scope: LifecycleCoroutineScope) {

    /**@SelfDocumented*/
    val swipeMenuActionFlow: MutableSharedFlow<MenuItem> = MutableSharedFlow()

    /**@SelfDocumented*/
    fun onMenuClick(menuItem: MenuItem) {
        scope.launch { swipeMenuActionFlow.emit(menuItem) }
    } 
}