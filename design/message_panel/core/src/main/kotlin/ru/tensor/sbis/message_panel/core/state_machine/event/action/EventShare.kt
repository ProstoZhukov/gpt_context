package ru.tensor.sbis.message_panel.core.state_machine.event.action

import ru.tensor.sbis.common.util.statemachine.SessionEvent
import ru.tensor.sbis.message_panel.declaration.data.ShareContent

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
data class EventShare(
    val content: ShareContent
) : SessionEvent