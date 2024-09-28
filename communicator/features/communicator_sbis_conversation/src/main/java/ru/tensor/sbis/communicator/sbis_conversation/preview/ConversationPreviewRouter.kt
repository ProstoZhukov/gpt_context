package ru.tensor.sbis.communicator.sbis_conversation.preview

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationFromRegistryParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationViewMode
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationFragment
import ru.tensor.sbis.design.utils.DebounceActionHandler

/**
 * Роутер для управления результатами, передаваемыми от ConversationPreviewFragment родительскому фрагменту.
 *
 * @author da.zhukov
 */
internal class ConversationPreviewRouter(
    fragment: DialogFragment,
    private val conversationParams: ConversationParams
) {

    private val fragmentManager = fragment.parentFragmentManager

    private val debounceActionHandler = DebounceActionHandler(2000)

    /**
     * Устанавливает результат для родительского фрагмента.
     * @param result Результат выбора элемента меню.
     */
    fun setResult(result: ConversationPreviewMenuAction) {
        fragmentManager.setFragmentResult(CONVERSATION_PREVIEW_RESULT, Bundle().apply {
            putSerializable(CONVERSATION_PREVIEW_RESULT, result)
        })
    }

    /**
     * Метод для открытия полноэкранного ConversationFragment с анимацией.
     */
    fun openFullConversation() {
        debounceActionHandler.handle {
            if (fragmentManager.findFragmentByTag(PREVIEW_CONVERSATION_TAG) != null) return@handle

            val conversationFragment = ConversationFragment.createConversationFragment(
                (conversationParams as ConversationFromRegistryParams).copy(
                    conversationViewMode = ConversationViewMode.FULL
                )
            ).apply {
                arguments?.putBoolean(IntentAction.Extra.NEED_TO_ADD_FRAGMENT_TO_BACKSTACK, true)
            }
            // Запускаем транзакцию с анимацией на parentFragmentManager
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.conversation_fade_in_scale_up, // enter
                    ru.tensor.sbis.design.R.anim.right_out, // exit
                    R.anim.conversation_fade_in_scale_up, // popEnter
                    ru.tensor.sbis.design.R.anim.right_out  // popExit
                )
                .add(
                    ru.tensor.sbis.common.R.id.overlay_container,
                    conversationFragment,
                    PREVIEW_CONVERSATION_TAG
                )
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}

private const val CONVERSATION_PREVIEW_RESULT = "CONVERSATION_PREVIEW_RESULT"
private const val PREVIEW_CONVERSATION_TAG = "PREVIEW_CONVERSATION_TAG"