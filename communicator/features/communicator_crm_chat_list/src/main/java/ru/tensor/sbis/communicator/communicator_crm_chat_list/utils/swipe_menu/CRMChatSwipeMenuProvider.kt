package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu

import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.ConsultationViewModel
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemFactory.createDeleteItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle

/**
 * Провайдер элементов свайп-меню.
 *
 * @author da.zhukov
 */
internal class CRMChatSwipeMenuProvider(private val crmChatSwipeMenuHelper: CRMChatSwipeMenuHelper) {

    /**@SelfDocumented*/
    fun getSwipeMenu(consultationViewModel: ConsultationViewModel): List<SwipeMenuItem> {
        return buildList<MenuItem> {
            with(consultationViewModel.allowedActions) {
                if (contains(ConsultationActionsFlags.CAN_DELETE)) add(DeleteMenuItem(consultationViewModel.id))
                if (contains(ConsultationActionsFlags.CAN_TAKE)) add(TakeMenuItem(consultationViewModel.id))
                if (contains(ConsultationActionsFlags.CAN_CLOSE)) add(CompleteMenuItem(consultationViewModel.id))
            }
        }.map {
            val onClick: () -> Unit = { crmChatSwipeMenuHelper.onMenuClick(it) }
            when (it) {
                is DeleteMenuItem -> {
                    createDeleteItem(
                        R.string.communicator_crm_chat_list_swipe_delete,
                        onClick
                    )
                }
                is TakeMenuItem -> {
                    IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_Successful),
                        R.string.communicator_crm_chat_list_swipe_take,
                        SwipeItemStyle.GREEN,
                        onClick
                    )
                }
                is CompleteMenuItem -> {
                    IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_Successful),
                        R.string.communicator_crm_chat_list_swipe_complete,
                        SwipeItemStyle.GREEN,
                        onClick
                    )
                }
            }
        }
    }
}