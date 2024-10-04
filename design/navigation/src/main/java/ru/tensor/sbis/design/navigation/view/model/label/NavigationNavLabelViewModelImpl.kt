package ru.tensor.sbis.design.navigation.view.model.label

import androidx.core.content.res.ResourcesCompat
import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Реализация [NavigationNavLabelViewModel].
 *
 * @author ma.kolpakov
 */
internal class NavigationNavLabelViewModelImpl(
    labelObservable: Observable<NavigationItemLabel>
) : NavigationNavLabelViewModel {

    override val navViewLabel: Observable<Int> = labelObservable.map { it.default.tryGetResourceId() }

    override val labelForRightAlignment: Observable<Int> =
        labelObservable.map { it.labelForRightAlignment.tryGetResourceId() }

    override val isLabelAlignedRight: Observable<Boolean> = labelObservable.map(NavigationItemLabel::isAlignedRight)

    private fun SbisString.tryGetResourceId() = (this as? PlatformSbisString.Res)?.stringRes
        ?: ResourcesCompat.ID_NULL
}