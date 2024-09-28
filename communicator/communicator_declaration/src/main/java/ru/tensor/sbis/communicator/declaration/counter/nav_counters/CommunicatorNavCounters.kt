package ru.tensor.sbis.communicator.declaration.counter.nav_counters

import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Счетчики для элементов меню модуля коммуникатор.
 *
 * @author vv.chekurda
 */
interface CommunicatorNavCounters {

    /**
     * Модель общего счетчика раздела сообщений (диалоги + чаты).
     */
    val messagesCounter: NavigationCounter

    /**
     * Модель счетчика реестра диалогов.
     */
    val dialogsCounter: NavigationCounter

    /**
     * Модель счетчика реестра чатов.
     */
    val chatsCounter: NavigationCounter

    interface Provider : Feature {

        val communicatorNavCounters: CommunicatorNavCounters
    }
}