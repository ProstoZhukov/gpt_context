package ru.tensor.sbis.recipient_selection.profile.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilter
import ru.tensor.sbis.design.selection.ui.factories.createMultiRecipientSelector
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.repost.ContactSelectionForRepostDependencyFactory
import ru.tensor.sbis.recipient_selection.profile.data.listeners.CancelListener
import ru.tensor.sbis.recipient_selection.profile.data.listeners.RecipientsPrefetchCheckFunction
import ru.tensor.sbis.recipient_selection.profile.data.repost.RepostCompleteListener

/**
 * Реализация фрагмента выбора контактов для репоста на базе *MultiSelectorFragment
 *
 * @author vv.chekurda
 */
fun createContactSelectionForRepostFragment(
    parameters: RecipientSelectionFilter,
    selectionMode: SelectorSelectionMode = SelectorSelectionMode.REPLACE_ALL_IF_FIRST
): Fragment {
    val filter = RecipientsSearchFilter(parameters)
    return createMultiRecipientSelector(
        ContactSelectionForRepostDependencyFactory(filter),
        RepostCompleteListener(),
        CancelListener(),
        RecipientsPrefetchCheckFunction(),
        limit = DEFAULT_RECIPIENT_SELECTION_LIMIT,
        isSwipeBackEnabled = true,
        doneButtonVisibilityMode = filter.doneButtonVisibilityRule,
        selectionMode = selectionMode,
        themeRes = R.style.SelectionDefaultThemeRecipientEmbeddedOnTablet
    )
}