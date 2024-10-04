package ru.tensor.sbis.appdesign.combined_multiselection.datasource

import ru.tensor.sbis.appdesign.combined_multiselection.data.DemoCombinedRecipientServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact.DemoContactServiceWrapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog.DemoDialogServiceWrapper
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoCombinedRecipientServiceWrapper(
    private val contactsWrapper: DemoContactServiceWrapper,
    private val dialogsWrapper: DemoDialogServiceWrapper,
) : ServiceWrapper<DemoCombinedRecipientServiceResult, DemoRecipientFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    override fun list(filter: DemoRecipientFilter): DemoCombinedRecipientServiceResult {
        val contacts = contactsWrapper.list(filter)
        val dialogs = dialogsWrapper.list(filter)
        return DemoCombinedRecipientServiceResult(
            DemoContactServiceResult(contacts.data),
            DemoDialogServiceResult(dialogs.data),
            hasMore = contacts.hasMore || dialogs.hasMore,
        )
    }


    override fun refresh(filter: DemoRecipientFilter, params: Map<String, String>): DemoCombinedRecipientServiceResult {
        val contacts = contactsWrapper.refresh(filter, emptyMap())
        val dialogs = dialogsWrapper.refresh(filter, emptyMap())
        return DemoCombinedRecipientServiceResult(
            DemoContactServiceResult(contacts.data),
            DemoDialogServiceResult(dialogs.data),
            hasMore = contacts.hasMore || dialogs.hasMore,
        )
    }
}
