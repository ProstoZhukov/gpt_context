package ru.tensor.sbis.communicator.themes_registry.ui.communication

import android.content.Context
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.ui.ChatCreationFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorWithContext

/**
 * Реализация создателя фрагмента выбора получателей для отображения в диалоговом окне планшета
 *
 * @author vv.chekurda
 */
internal class ConversationRecipientSelectionFragmentCreator : ContentCreatorWithContext {

    companion object {
        const val TABLET_NEW_CONVERSATION_RECIPIENT_SELECTION = "TABLET_NEW_CONVERSATION_RECIPIENT_SELECTION"
    }

    /** @SelfDocumented */
    override fun createFragment(context: Context): Fragment =
        themesRegistryDependency.getRecipientSelectionFragment(
            RecipientSelectionConfig(
                useCase = RecipientSelectionUseCase.NewDialog,
                requestKey = TABLET_NEW_CONVERSATION_RECIPIENT_SELECTION,
                closeOnComplete = false,
                closeOnCancel = true
            )
        )
}

/**
 * Реализация создателя фрагмента создания чата для отображения в диалоговом окне планшета
 */
internal class ConversationTabChatCreationFragmentCreator : ContentCreatorWithContext {

    /** @SelfDocumented */
    override fun createFragment(context: Context?): Fragment =
        ChatCreationFragment.newInstance()
}
