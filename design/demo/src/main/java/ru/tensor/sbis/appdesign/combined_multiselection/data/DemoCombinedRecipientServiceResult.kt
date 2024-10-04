package ru.tensor.sbis.appdesign.combined_multiselection.data

import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResult

/**
 * @author ma.kolpakov
 */
data class DemoCombinedRecipientServiceResult(
    val contacts: DemoContactServiceResult,
    val dialogs: DemoDialogServiceResult,
    val hasMore: Boolean,
) {

    val isEmpty = contacts.data.isEmpty() && dialogs.data.isEmpty()
}
