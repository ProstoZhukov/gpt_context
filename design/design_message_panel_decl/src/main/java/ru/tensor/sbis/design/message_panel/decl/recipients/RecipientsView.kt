package ru.tensor.sbis.design.message_panel.decl.recipients

import android.content.Context
import android.view.ViewGroup
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem

/**
 * Панель получателей.
 *
 * @author vv.chekurda
 */
abstract class RecipientsView(context: Context) : ViewGroup(context) {

    /**
     * Данные панели получателей.
     *
     * @property recipients список получателей.
     * @property isHintEnabled true, если для пустого списка получателей доступна подсказка [hintText],
     * в ином случае будет использоваться текст [allChosenText].
     */
    data class RecipientsViewData(
        val recipients: List<RecipientItem> = emptyList(),
        val isHintEnabled: Boolean = true
    ) {
        /**
         * Признак наличия получателей в модели.
         */
        val hasRecipients: Boolean
            get() = recipients.isNotEmpty()
    }

    /**
     * Установить/получить данные панели получателей.
     */
    abstract var data: RecipientsViewData

    /**
     * Установить/получить текст для отображения в сценарии, когда выбраны все получатели.
     */
    abstract var allChosenText: String

    /**
     * Установить/получить текст подсказки, который будет отображаться при пустом списке получателей,
     * если подсказка доступна [RecipientsViewData.isHintEnabled].
     */
    abstract var hintText: String

    /**
     * Установить/получить слушателя на очистку панели получателей.
     */
    abstract var recipientsClearListener: (() -> Unit)?
}