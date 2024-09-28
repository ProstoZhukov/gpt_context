package ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option

import android.view.View
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.modalwindows.bottomsheet.resourceprovider.OptionSheetItemViewProvider

/**
 * Поставщик вью для вью-холдера опции
 *
 * @author sr.golovkin
 */
@Suppress("unused")
internal class ConversationOptionItemViewProvider : OptionSheetItemViewProvider {

    override fun provideOptionLayoutRes(): Int = R.layout.communicator_conversation_option_item

    override fun provideTitleView(root: View): SbisTextView = root.findViewById(R.id.communicator_conversation_menu_item_text)

    override fun provideIconView(root: View): SbisTextView = root.findViewById(R.id.communicator_conversation_menu_icon)
}