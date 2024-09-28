package ru.tensor.sbis.communication_decl.recipient_selection

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides RecipientSelectionActivity intent
 */
@Deprecated("ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider")
interface RecipientSelectionProvider : Feature {

    /**
     * @param context
     * @param parameters values for recipient selection screen
     * @return intent for RecipientSelectionActivity with the specified options
     */
    fun getRecipientsSelectionActivityIntent(context: Context, parameters: RecipientSelectionFilter): Intent

    fun getRecipientSelectionFragment(parameters: RecipientSelectionFilter): Fragment

}


