package ru.tensor.sbis.design.navigation.view.model.label

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Реализация [NavigationLabelViewModel].
 *
 * @author ma.kolpakov
 */
class NavigationLabelViewModelImpl(
    labelObservable: Observable<NavigationItemLabel>
) : NavigationLabelViewModel {

    private val customLabel = BehaviorSubject.createDefault(EMPTY_LABEL)

    override val navigationLabel: Observable<NavigationItemLabel> =
        Observable.combineLatest(labelObservable, customLabel) { default, custom ->
            custom.takeUnless { it == EMPTY_LABEL }
                ?: default
        }

    override fun updateLabel(navigationItemLabel: NavigationItemLabel) {
        customLabel.onNext(navigationItemLabel)
    }
}

private val EMPTY_LABEL = NavigationItemLabel(PlatformSbisString.Value(""))