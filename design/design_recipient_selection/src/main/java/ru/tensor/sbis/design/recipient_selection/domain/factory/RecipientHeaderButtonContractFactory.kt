package ru.tensor.sbis.design.recipient_selection.domain.factory

import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import javax.inject.Inject

/**
 * Фабрика для создания контракта головной кнопки компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientHeaderButtonContractFactory @Inject constructor() {

    /**
     * Создать контракт [HeaderButtonContract].
     *
     * @param config настройка компонента выбора получателей.
     */
    fun createContact(config: RecipientSelectionConfig): HeaderButtonContract<RecipientItem, *>? =
        if (config.useCase is RecipientSelectionUseCase.NewPrivateChat) {
            PrivateChatHeaderButtonContract()
        } else {
            null
        }
}