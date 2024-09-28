package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.router

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsFragment
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem

/**
 * Интерфейс роутера для списка участников канала.
 *
 * @author dv.baranov
 */
internal interface ChatParticipantsRouter {

    /**
     * Провалиться в выбранную папку [folder].
     */
    fun openFolder(args: Bundle?, folder: ThemeParticipantListItem.ThemeParticipantFolder?)

    /**
     * Закрыть все папки до корневой.
     */
    fun closeAllFolders()

    /**
     * Обработать действие для перехода назад по стеку.
     */
    fun back(): Boolean
}

/**
 * Реализация роутера для списка участников канала.
 *
 * @author dv.baranov
 */
internal class ChatParticipantsRouterImpl(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int,
) : ChatParticipantsRouter {

    override fun openFolder(args: Bundle?, folder: ThemeParticipantListItem.ThemeParticipantFolder?) {
        fragmentManager.beginTransaction()
            .add(containerId, ChatParticipantsFragment.newInstance(args, folder))
            .addToBackStack(null)
            .commit()
    }

    override fun closeAllFolders() {
        while (back()) Unit
    }

    override fun back(): Boolean {
        return if (fragmentManager.fragments.size > 1) {
            fragmentManager.popBackStackImmediate()
        } else {
            false
        }
    }
}
