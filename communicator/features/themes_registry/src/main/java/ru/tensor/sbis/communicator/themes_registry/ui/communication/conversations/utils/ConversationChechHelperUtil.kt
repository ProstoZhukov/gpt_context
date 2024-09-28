package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.utils

import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper
import ru.tensor.sbis.base_components.adapter.checkable.impl.CheckHelperImpl
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.persons.ConversationRegistryItem

/**
 * Проверить на выбранность элемента в реестре диалогов/каналов.
 *
 * P.S. Костыль, надо переписывать текущий ObservableCheckItemsHelperImpl, чтобы он адекватно мог работать
 * с ключами для сравнения и моделями, сейчас он считает модели - ключами, и изменения переработки нужно поддерживаться
 * в нескольких приложениях.
 */
internal fun isChecked(checkHelper: CheckHelper<ConversationRegistryItem>, item: ConversationRegistryItem): Boolean =
    if (item is ConversationModel && checkHelper is CheckHelperImpl<*, *>) {
        checkHelper.checkedKeys.find {
            it.castTo<ConversationModel>()?.uuid == item.uuid
        } != null
    } else {
        false
    }