package ru.tensor.sbis.design.navigation.view.filter

import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * Расширение [EventFilter] с возможностью получать события навигации по запросу.
 *
 * @author ma.kolpakov
 * Создан 11/11/2019
 */
interface BackpressureEventFilter<EventType : NavigationEvent<*>> : EventFilter<EventType> {

    /**
     * Запрос следующего события, когда UI готов его обработать.
     */
    fun requestNext()
}