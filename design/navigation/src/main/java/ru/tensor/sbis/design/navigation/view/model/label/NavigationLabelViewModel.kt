package ru.tensor.sbis.design.navigation.view.model.label

import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

/**
 * Модель надписей элементов меню.
 *
 * @author ma.kolpakov
 */
interface NavigationLabelViewModel {

    /** @SelfDocumented */
    val navigationLabel: Observable<NavigationItemLabel>

    /** @SelfDocumented */
    fun updateLabel(navigationItemLabel: NavigationItemLabel)
}