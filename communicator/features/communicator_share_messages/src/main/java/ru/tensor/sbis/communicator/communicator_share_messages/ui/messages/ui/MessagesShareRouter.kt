package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter

/**
 * Интерфейс роутера экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
internal interface MessagesShareRouter : Router<Fragment> {

    /**
     * Завершить шаринг.
     */
    fun endShare()
}

/**
 * Реализация роутера экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
internal class MessagesShareRouterImpl : FragmentRouter(), MessagesShareRouter {

    override fun endShare() = execute {
        requireActivity().finishAndRemoveTask()
    }
}
