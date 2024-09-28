package ru.tensor.sbis.message_panel.interactor.recipients

import androidx.annotation.WorkerThread
import io.reactivex.Maybe
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import java.util.*

/**
 * API для взаимодействия с прикладными моделями получателей
 *
 * @author vv.chekurda
 * Создан 10/3/2019
 */
interface MessagePanelRecipientsInteractor {

    /**
     * Возвращает список моделей получателей из прикладного контроллера по результату компонента выбора.
     */
    fun loadRecipientModels(selectionData: RecipientSelectionData): Maybe<List<RecipientItem>>

    /**
     * Возвращает список моделей получателей из прикладного контроллера по списку выбранных получателей.
     */
    fun loadRecipientModels(recipients: List<UUID>): Maybe<List<RecipientItem>>

    /**
     * Отметка о том, что для диалога [dialogUuid] выбраны все получатели [selectedRecipients].
     * Например, в диалоге может участвовать 3 человека, а в [selectedRecipients] выбран только
     * один - вернуть `false`.
     */
    @WorkerThread
    fun checkAllMembersSelected(dialogUuid: UUID, selectedRecipients: ArrayList<UUID>): Boolean
}