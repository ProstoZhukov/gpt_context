package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment

import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter

/**
 * Роутер экрана ввода комментария, при переназначении другому оператору.
 *
 * @author da.zhukov
 */
internal class CrmReassignCommentRouter : FragmentRouter() {

    fun close() = execute {
        if (!popBackStackChildFragmentIfNeed()) {
            (parentFragment as? ContainerMovableDialogFragment)?.dismissAllowingStateLoss()
        }
    }
}