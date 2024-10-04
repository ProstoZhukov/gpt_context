package ru.tensor.sbis.message_panel.core.state_machine.event.transition

import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import java.util.*

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
data class SendingQuoteMessageEvent(
    val quotedMessageUuid: UUID
) : SessionStateEvent