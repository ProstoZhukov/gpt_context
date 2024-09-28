package ru.tensor.sbis.appdesign.combined_multiselection.datasource

import ru.tensor.sbis.appdesign.combined_multiselection.data.DemoCombinedRecipientServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact.DemoContactDataMapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog.DemoDialogDataMapper
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.DefaultTitleItemModel

/**
 * @author ma.kolpakov
 */
class DemoCombinedRecipientMapper(
    private val contactsMapper: DemoContactDataMapper,
    private val dialogsMapper: DemoDialogDataMapper,
) : ListMapper<DemoCombinedRecipientServiceResult, SelectorItemModel> {

    override fun invoke(serviceResult: DemoCombinedRecipientServiceResult): List<SelectorItemModel> {
        val contactsTitle = DefaultTitleItemModel("001", "Недавние контакты")
        val dialogsTitle = DefaultTitleItemModel("002", "Недавние диалоги")

        val contacts = contactsMapper(serviceResult.contacts)
        val dialogs = dialogsMapper(serviceResult.dialogs)

        return mutableListOf<SelectorItemModel>().apply {
            if (contacts.isNotEmpty()) {
                add(contactsTitle)
                addAll(contacts)
            }

            if (dialogs.isNotEmpty()) {
                add(dialogsTitle)
                addAll(dialogs)
            }
        }
    }
}
