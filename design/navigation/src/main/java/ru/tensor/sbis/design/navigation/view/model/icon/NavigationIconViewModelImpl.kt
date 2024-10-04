package ru.tensor.sbis.design.navigation.view.model.icon

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemState
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedSame
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState

/**
 * Реализация [NavigationIconViewModel].
 *
 * @author ma.kolpakov
 */
@SuppressLint("CheckResult")
internal class NavigationIconViewModelImpl(
    iconObservable: Observable<NavigationItemIcon>,
    stateObservable: Observable<NavigationItemState>
) : NavigationIconViewModel {

    data class IconModel(
        val tabNavViewIcon: Int,
        val navViewIcon: Int,
        val calendarDate: Int? = null
    )

    override val tabNavViewIcon = ReplaySubject.create<Int>()

    override val navViewIcon = ReplaySubject.create<Int>()

    override val calendarDayNumber = BehaviorSubject.create<Int>()

    override val controllerIcon = BehaviorSubject.createDefault(ControllerNavIcon())

    init {
        // Disposable не нужен так как управляется на месте использования(ННП или Аккордеон)
        Observable.combineLatest(
            stateObservable,
            iconObservable,
            controllerIcon
        ) { state, model, controllerIcon ->
            when (state) {
                is UnselectedState -> IconModel(
                    controllerIcon.default ?: model.default,
                    controllerIcon.default ?: model.default,
                    model.calendarDate
                )

                is SelectedByUserState, is SelectedState, is SelectedSame ->
                    IconModel(
                        controllerIcon.selected ?: model.selected,
                        controllerIcon.default ?: model.default,
                        model.calendarDate
                    )
            }
        }.subscribe { models ->
            tabNavViewIcon.onNext(models.tabNavViewIcon)
            navViewIcon.onNext(models.navViewIcon)
            models.calendarDate?.let { calendarDayNumber.onNext(it) }
        }
    }

    override val iconVisible: Observable<Boolean> = iconObservable.map(NavigationItemIcon::isVisible)
}