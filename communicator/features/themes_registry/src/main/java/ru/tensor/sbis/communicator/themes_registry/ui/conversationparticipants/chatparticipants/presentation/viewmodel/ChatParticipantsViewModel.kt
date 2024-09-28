package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import java.util.*

/**
 * Вью-модель экрана "Участники чата"
 */
internal class ChatParticipantsViewModel : ViewModel() {

    /** UUID текущего пользователя приложения */
    val currentUserUuid = MutableLiveData<UUID>()

    /** Разрешения на какие-либо действия в чате */
    val chatPermissions = MutableLiveData<Permissions>()

    /** Доступно ли свайп-меню [SwipeableLayout] для конкретного item'а списка */
    val isSwipeEnabled = MutableLiveData<Boolean>()

    /** UUID открытой папки, null - корневая папка */
    var folderUUID: UUID? = null
}
