package ru.tensor.sbis.design.recipient_selection.domain.factory

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design.recipient_selection.R
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin.singletonComponent
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonStrategy

/**
 * Реализация контракта кнопки для перехода к созданию группового канала.
 *
 * @author vv.chekurda
 */
internal class PrivateChatHeaderButtonContract : HeaderButtonContract<RecipientItem, FragmentActivity> {

    override val layout: Int = R.layout.design_recipient_selection_private_chat_header_button

    override fun onButtonClicked(
        activity: FragmentActivity,
        selectedItems: List<RecipientItem>,
        config: SelectionConfig
    ): HeaderButtonStrategy {
        singletonComponent.recipientSelectionResultDelegate.onSuccess(requestKey = config.requestKey)
        val newUseCase = RecipientSelectionUseCase.NewChat
        return HeaderButtonStrategy(
            newConfig = (config as RecipientSelectionConfig).copy(
                useCase = newUseCase,
                selectionMode = newUseCase.selectionMode,
                doneButtonMode = newUseCase.doneButtonMode,
                unfoldDepartments = newUseCase.unfoldDepartments,
                isDepartmentsSelectable = newUseCase.isDepartmentsSelectable
            )
        )
    }
}