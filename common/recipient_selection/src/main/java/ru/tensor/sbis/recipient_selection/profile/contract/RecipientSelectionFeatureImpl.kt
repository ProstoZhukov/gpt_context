package ru.tensor.sbis.recipient_selection.profile.contract

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilter
import ru.tensor.sbis.recipient_selection.profile.RecipientSelectionPlugin
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionSingletonComponent
import ru.tensor.sbis.recipient_selection.profile.ui.createRecipientSelectionFragment
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager

/**
 * Имлпементация интерфейса [RecipientSelectionFeature]
 */
class RecipientSelectionFeatureImpl : RecipientSelectionFeature {

    private val recipientSelectionSingletonComponent: RecipientSelectionSingletonComponent
        get() = RecipientSelectionPlugin.singletonComponent

    override fun getRecipientsSelectionActivityIntent(context: Context, parameters: RecipientSelectionFilter) =
        createIntent(parameters)

    override fun getRecipientSelectionFragment(parameters: RecipientSelectionFilter) =
        createRecipientSelectionFragment(parameters)

    override fun getRecipientSelectionResultManager(): RecipientSelectionResultManager =
        recipientSelectionSingletonComponent.getRecipientSelectionResultManager()

    override fun getRepostRecipientSelectionResultManager(): RecipientSelectionResultManager =
        recipientSelectionSingletonComponent.getContactsSelectionResultManagerForRepost()

    private fun createIntent(parameters: RecipientSelectionFilter) = Intent().apply {
        action = ACTION_RECIPIENT_SELECTION_ACTIVITY
        setPackage(BuildConfig.MAIN_APP_ID)
        putExtras(parameters.bundle)
    }

    companion object {
        const val ACTION_RECIPIENT_SELECTION_ACTIVITY = BuildConfig.MAIN_APP_ID + ".RECIPIENTS_SELECTION_ACTIVITY"
    }
}