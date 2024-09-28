package ru.tensor.sbis.design.message_view.content.crm_views.greetings_view

import ru.tensor.sbis.design.message_view.model.GreetingsViewData
import ru.tensor.sbis.design.message_view.model.MessageViewData

/**
 * Утилита для получения [GreetingsViewData].
 *
 * @author vv.chekurda
 */
object GreetingsViewDataUtil {

    /** @SelfDocumented */
    fun createViewData(greetings: List<String>): MessageViewData =
        GreetingsViewData(greetings)
}
