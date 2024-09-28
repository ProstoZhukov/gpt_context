package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

enum class ThemeBottomCheckAction(
    val iconResId: Int,
    val textResId: Int,
    var action: () -> Unit = {}
) {
    MARK_GROUP_AS_READ(
        iconResId = RDesign.string.design_mobile_icon_read,
        textResId = RCommunicatorDesign.string.communicator_check_panel_read
    ),

    MARK_GROUP_AS_UNREAD(
        iconResId = RDesign.string.design_mobile_icon_unread,
        textResId = RCommunicatorDesign.string.communicator_check_panel_unread
    ),

    MOVE_GROUP(
        iconResId = RDesign.string.design_mobile_icon_move,
        textResId = RCommunicatorDesign.string.communicator_check_panel_move,
    ),

    DELETE_GROUP(
        iconResId = RDesign.string.design_mobile_icon_delete,
        textResId = RCommunicatorDesign.string.communicator_check_panel_delete,
    );

    // Метод для задания действия после инициализации
    fun withAction(action: () -> Unit): ThemeBottomCheckAction {
        this.action = action
        return this
    }
}