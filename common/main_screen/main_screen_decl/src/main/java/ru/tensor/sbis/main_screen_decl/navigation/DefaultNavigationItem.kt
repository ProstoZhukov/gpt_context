package ru.tensor.sbis.main_screen_decl.navigation

import androidx.core.content.res.ResourcesCompat
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.UNDEFINED_NAVX_IDENTIFIER
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Базовая реализация [NavigationItem].
 *
 * @author kv.martyshenko
 */
data class DefaultNavigationItem(
    val navigationItemLabel: NavigationItemLabel?,
    val navigationItemIcon: NavigationItemIcon?,
    override val persistentUniqueIdentifier: String,
    override var ordinal: Int, // нужен для адаптера стандартных компонентов
    @Deprecated(
        "Удалится по https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
        replaceWith = ReplaceWith("navxId")
    )
    override val navxIdentifier: String = UNDEFINED_NAVX_IDENTIFIER,
    override val navxId: NavxId? = NavxId.of(navxIdentifier)
) : NavigationItem {

    /**
     * Текст и иконку можно не указывать, поскольку они должны быть предоставлены сервисом навигации.
     */
    constructor(
        navigationItemLabel: NavigationItemLabel? = null,
        navigationItemIcon: NavigationItemIcon? = null,
        navxIdentifier: NavxId,
        persistentUniqueIdentifier: String = navxIdentifier.ids.first()
    ) : this(
        navigationItemLabel,
        navigationItemIcon,
        persistentUniqueIdentifier,
        navxIdentifier.ordinal,
        navxIdentifier.ids.first(),
        navxIdentifier
    )

    override val labelObservable: Observable<NavigationItemLabel> by lazy {
        Observable.just(navigationItemLabel ?: getLabelStub())
    }

    override val iconObservable: Observable<NavigationItemIcon> by lazy {
        Observable.just(navigationItemIcon ?: getIconStub())
    }

    override val labelFlow: Flow<NavigationItemLabel> by lazy {
        labelObservable.asFlow()
    }

    override val iconFlow: Flow<NavigationItemIcon> by lazy {
        iconObservable.asFlow()
    }

    private fun getLabelStub() = NavigationItemLabel(PlatformSbisString.Value(""))

    private fun getIconStub() = NavigationItemIcon(ResourcesCompat.ID_NULL)
}