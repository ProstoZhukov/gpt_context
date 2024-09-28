/**
 * Инструменты для создания фильтров событий от компонентов навигации
 *
 * @author ma.kolpakov
 * Создан 11/11/2019
 */
@file:JvmName("EventFilterFactory")

package ru.tensor.sbis.design.navigation.view.filter

import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * Создать стандартную реализацию[BackpressureEventFilter].
 */
fun <EventType : NavigationEvent<*>> createBackpressureFilter(): BackpressureEventFilter<EventType> =
    BackpressureEventFilterImpl()