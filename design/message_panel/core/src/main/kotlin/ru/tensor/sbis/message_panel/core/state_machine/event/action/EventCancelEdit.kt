package ru.tensor.sbis.message_panel.core.state_machine.event.action

import ru.tensor.sbis.common.util.statemachine.SessionEvent
import java.util.*

/**
 * Событие условной отмены редактирования
 *
 * @author ma.kolpakov
 */
data class EventCancelEdit(
    /**
     * [UUID] сообщения, редактрирование которого нужно прервать
     */
    val editingMessage: UUID
) : SessionEvent