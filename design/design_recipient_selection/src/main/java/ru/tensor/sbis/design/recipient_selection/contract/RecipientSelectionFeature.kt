package ru.tensor.sbis.design.recipient_selection.contract

import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuProvider

/**
 * Фичи модуля компонента выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionFeature :
    RecipientSelectionProvider,
    RecipientSelectionMenuProvider,
    RecipientSelectionResultDelegate.Provider