package ru.tensor.sbis.recipient_selection.profile.data.factory_models.single

import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import javax.inject.Inject

/**
 * Реализация подстановки уже выбранных получателей при инициализирующей загрузке одиночного выбора
 *
 * @author vv.chekurda
 */
internal class SingleRecipientSelectionLoader @Inject constructor() : SingleSelectionLoader<DefaultPersonSelectorItemModel> {

    /**
     * Реализация - заглушка, на данный момент нет сценариев использования
     */
    override fun loadSelectedItem(): DefaultPersonSelectorItemModel? = null
}