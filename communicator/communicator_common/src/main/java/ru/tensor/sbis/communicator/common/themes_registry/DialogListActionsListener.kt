package ru.tensor.sbis.communicator.common.themes_registry

import ru.tensor.sbis.communicator.common.data.theme.ConversationButton
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import java.util.*

interface DialogListActionsListener {

    fun onCollageViewClick(conversation: ConversationModel)

    fun onButtonViewClick(button: ConversationButton)

    //region swipe actions
    fun onSwipeRemoveClicked(conversation: ConversationModel)

    fun onSwipeDismissed(uuid: UUID)

    fun onDismissedWithoutMessage(uuid: String?)

    fun onSwipeRestoreClicked(conversation: ConversationModel)

    fun onSwipeMoveToFolderClicked(conversation: ConversationModel)

    fun onSwipeMarkClicked(conversation: ConversationModel, markAsRead: Boolean)
    //endregion swipe actions
}