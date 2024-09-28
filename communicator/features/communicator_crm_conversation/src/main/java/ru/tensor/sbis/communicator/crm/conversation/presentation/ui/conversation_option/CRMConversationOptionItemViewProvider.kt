package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option

import android.view.View
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.modalwindows.bottomsheet.resourceprovider.OptionSheetItemViewProvider

/**
 * Поставщик вью для вью-холдера опции.
 *
 * @author dv.baranov
 */
@Suppress("unused")
internal class CRMConversationOptionItemViewProvider : OptionSheetItemViewProvider {

    override fun provideOptionLayoutRes(): Int = R.layout.communicator_crm_conversation_option_item

    override fun provideTitleView(root: View): SbisTextView = root.findViewById(R.id.communicator_crm_conversation_menu_item_text)

    override fun provideIconView(root: View): SbisTextView = root.findViewById(R.id.communicator_crm_conversation_menu_icon)
}
