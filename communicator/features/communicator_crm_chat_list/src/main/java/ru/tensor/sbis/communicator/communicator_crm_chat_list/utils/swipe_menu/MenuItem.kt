package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu

import java.util.UUID

/**
 * Элемент свайп меню.
 *
 * @author da.zhukov
 */
internal sealed interface MenuItem {
    /**@SelfDocumented*/
    val consultationId: UUID
}
/**@SelfDocumented*/
internal class DeleteMenuItem(override val consultationId: UUID) : MenuItem
/**@SelfDocumented*/
internal class TakeMenuItem(override val consultationId: UUID) : MenuItem
/**@SelfDocumented*/
internal class CompleteMenuItem(override val consultationId: UUID) : MenuItem