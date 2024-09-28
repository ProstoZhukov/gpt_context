package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.model

import ru.tensor.sbis.design.view.input.base.ValidationStatus

/**
 * Дата класс для передачи состояния поля ввода названия чата.
 *
 * @param value значение поля.
 * @param isEnabled доступность к изменению.
 * @param validationErrorStatus состояние ошибочной валидации.
 * @param onValueChanged действие на изменение.
 *
 * @author dv.baranov
 */
internal data class ChatSettingsEditChatNameData(
    val value: String = "",
    val isEnabled: Boolean = false,
    val validationErrorStatus: ValidationStatus.Error? = null,
    val onValueChanged: ((name: String) -> Unit) = {},
)
