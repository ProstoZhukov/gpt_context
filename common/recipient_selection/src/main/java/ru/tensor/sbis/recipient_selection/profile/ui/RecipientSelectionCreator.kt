package ru.tensor.sbis.recipient_selection.profile.ui

import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilter
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode.*
import ru.tensor.sbis.design.selection.ui.factories.*
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys.*
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi.MultiRecipientSelectionDependencyFactory
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.single.SingleRecipientSelectionDependencyFactory
import ru.tensor.sbis.recipient_selection.profile.data.listeners.CancelListener
import ru.tensor.sbis.recipient_selection.profile.data.listeners.MultiCompleteListener
import ru.tensor.sbis.recipient_selection.profile.data.listeners.RecipientSelectionNewGroupClickListener
import ru.tensor.sbis.recipient_selection.profile.data.listeners.RecipientsPrefetchCheckFunction
import ru.tensor.sbis.recipient_selection.profile.data.listeners.SingleCompleteListener
import ru.tensor.sbis.design.selection.R as RSelection

/**
 * Общая реализация фрагмента выбора получателей на базе *SingleSelectorFragment и *MultiSelectorFragment
 * в зависимости от настройки *isSingleChoice в фильтре [RecipientSelectionFilter]
 *
 * @author vv.chekurda
 */
internal const val RECIPIENT_SELECTION_LIST_SIZE = 20
internal const val DEFAULT_RECIPIENT_SELECTION_LIMIT = 300

fun createRecipientSelectionFragment(
    parameters: RecipientSelectionFilter
): Fragment {
    val filter = RecipientsSearchFilter(parameters)
    val needCloseOnComplete = parameters.needCloseOnComplete()
    val needCloseButton = parameters.needCloseButton()
    val isSwipeBackEnabled = parameters.isSwipeBackEnabled
    val needAlwaysAdd = parameters.bundle[ALWAYS_ADD_MODE.key()] == true
    @StyleRes val themeRes: Int =
        if (parameters.themeRes != 0) parameters.themeRes
        else RSelection.style.SelectionDefaultTheme_Recipient

    return if (parameters.isSingleChoice) {
        val newGroupListener = parameters.bundle[CALL_FROM.key()]?.let { target ->
            when (target) {
                CHAT.key() -> RecipientSelectionNewGroupClickListener(filter)
                DIALOG.key() -> null
                else -> errorSafe("Unexpected recipient selection target: $target")
            }
        }
        createSingleRecipientSelector(
            SingleRecipientSelectionDependencyFactory(filter),
            SingleCompleteListener(filter, needCloseOnComplete),
            CancelListener(),
            newGroupClickListener = newGroupListener,
            needCloseButton = needCloseButton,
            isSwipeBackEnabled = isSwipeBackEnabled,
            themeRes = themeRes
        )
    } else createMultiRecipientSelector(
            MultiRecipientSelectionDependencyFactory(filter),
            MultiCompleteListener(filter, needCloseOnComplete),
            CancelListener(),
            RecipientsPrefetchCheckFunction(),
            selectionMode = if (needAlwaysAdd) ALWAYS_ADD else filter.selectionModeRule,
            doneButtonVisibilityMode = filter.doneButtonVisibilityRule,
            limit = DEFAULT_RECIPIENT_SELECTION_LIMIT,
            isSwipeBackEnabled = isSwipeBackEnabled,
            themeRes = themeRes
    )
}

internal val RecipientsSearchFilter.doneButtonVisibilityRule: SelectorDoneButtonVisibilityMode
    get() = when {
        isNewConversation || !canResultBeEmpty -> SelectorDoneButtonVisibilityMode.AT_LEAST_ONE
        else                                   -> SelectorDoneButtonVisibilityMode.VISIBLE
    }

/**
 * Правило автоматического определения режима работы селектора:
 * При выборе получателей из нового диалога(до отправки первого сообщения) и в процессе выбора участников
 * для нового чата - не соблюдать условие спецификации (см. [REPLACE_ALL_IF_FIRST]) и по клику всегда добавлять к
 * уже выбранным.
 */
private val RecipientsSearchFilter.selectionModeRule: SelectorSelectionMode
    get() {
        val isOpenedFromNewDialog = isNewConversation && !isChat && dialogUuid != null
        val isOpenedFromChatCreation = isNewConversation && isChat && dialogUuid == null
        return when {
            isOpenedFromNewDialog || isOpenedFromChatCreation -> ALWAYS_ADD
            else                                              -> REPLACE_ALL_IF_FIRST
        }
    }