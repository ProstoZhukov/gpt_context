package ru.tensor.sbis.design.navigation.view.filter

import androidx.lifecycle.Observer
import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * Фильтр переключения разделов через меню. Позволяет снизить нагрузку на UI при частом переключении разделов меню.
 *
 * @author ma.kolpakov
 * Создан 11/11/2019
 */
interface EventFilter<EventType : NavigationEvent<*>> : Observer<EventType?> {

    val eventObservable: Observable<EventType>
}