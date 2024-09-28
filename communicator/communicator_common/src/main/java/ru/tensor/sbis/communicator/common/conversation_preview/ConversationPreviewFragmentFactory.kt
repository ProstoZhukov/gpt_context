package ru.tensor.sbis.communicator.common.conversation_preview

import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communicator.declaration.ConversationPreviewMode
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания экземпляров ConversationPreviewFragment.
 *
 * @author da.zhukov
 */
interface ConversationPreviewFragmentFactory : Feature {
    fun create(
        menuItems: List<ConversationPreviewMenuAction>,
        mode: ConversationPreviewMode,
        params: ConversationParams
    ): DialogFragment
}