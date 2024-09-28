package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.action_buttons

import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import java.util.UUID

/**
 * API группы кнопок действий над чатом CRM со стороны оператора.
 *
 * @author dv.baranov
 */
internal interface CRMActionButtonsAPI {

    /** Интерфейс действий кнопок. */
    var listener: CRMActionButtonsClickListener?

    /** Видимость кнопки *Забрать чат*. */
    val isTakeButtonVisible: Boolean

    /** Установить слушатель для кнопки *пропустить*. */
    fun setNextButtonClickListener(uuid: UUID, viewId: UUID)

    /** Показать кнопку. */
    fun showActionButton(type: ActionButtonType)

    /** Спрятать кнопку. */
    fun hideActionButton(type: ActionButtonType)
}

/**
 * Виды кнопок действий.
 */
enum class ActionButtonType {

    /** Возобновить чат. */
    REOPEN,

    /** Забрать чат. */
    TAKE,

    /** Пропустить чат. */
    NEXT
}

/**
 * Интерфейс действий при клике на кнопки.
 */
interface CRMActionButtonsClickListener {

    /**
     * Открыть следующую консультацию.
     */
    fun openNextConsultation(chatParams: CRMConsultationParams)

    /**
     * Взять в обработку чат тех. поддержки.
     */
    fun takeConsultation()

    /**
     * Возобновить чат тех. поддержки.
     */
    fun reopenConsultation()
}
