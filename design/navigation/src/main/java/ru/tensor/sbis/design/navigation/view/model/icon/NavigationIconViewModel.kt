package ru.tensor.sbis.design.navigation.view.model.icon

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon

/**
 * Модель иконок элементов меню.
 *
 * @author ma.kolpakov
 */
internal interface NavigationIconViewModel {
    /**
     * Иконка элемента меню ННП.
     */
    val tabNavViewIcon: ReplaySubject<Int>

    /**
     * Иконка элемента меню аккордеона
     */
    val navViewIcon: ReplaySubject<Int>

    /**
     * Число дня для иконки календаря.
     */
    val calendarDayNumber: Observable<Int>

    /**
     * Состояние видимости иконки.
     */
    val iconVisible: Observable<Boolean>

    /**
     * Модель иконки пункта навигации с контроллера.
     */
    val controllerIcon: BehaviorSubject<ControllerNavIcon>
}