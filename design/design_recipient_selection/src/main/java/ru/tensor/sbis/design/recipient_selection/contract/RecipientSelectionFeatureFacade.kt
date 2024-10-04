package ru.tensor.sbis.design.recipient_selection.contract

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionSingletonComponent
import ru.tensor.sbis.design.recipient_selection.ui.RecipientSelectionActivity
import ru.tensor.sbis.design.recipient_selection.ui.RecipientSelectionFragmentFactory
import ru.tensor.sbis.design.recipient_selection.ui.menu.RecipientSelectionMenuFactory

/**
 * Реализация фичи модуля компонента выбора получателей [RecipientSelectionFeature].
 *
 * @author vv.chekurda
 */
internal object RecipientSelectionFeatureFacade : RecipientSelectionFeature {

    private val singletonComponent: RecipientSelectionSingletonComponent
        get() = RecipientSelectionPlugin.singletonComponent

    override fun getRecipientSelectionFragment(config: RecipientSelectionConfig): Fragment =
        RecipientSelectionFragmentFactory.createRecipientSelectionFragment(config)

    override fun getRecipientSelectionIntent(context: Context, config: RecipientSelectionConfig): Intent =
        RecipientSelectionActivity.newIntent(context, config)

    override fun getRecipientSelectionMenu(config: RecipientSelectionMenuConfig): SelectionMenu =
        RecipientSelectionMenuFactory.createRecipientSelectionMenuFragment(config)

    override fun getRecipientSelectionResultManager(): RecipientSelectionResultManager =
        singletonComponent.recipientSelectionManager

    override fun getRecipientSelectionResultDelegate(): RecipientSelectionResultDelegate =
        singletonComponent.recipientSelectionResultDelegate
}