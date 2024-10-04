package ru.tensor.sbis.design.navigation.view.model

/**
 * Базовый класс состояние элемента меню.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
internal sealed class NavigationItemState

/**
 * Состояние элемента меню, который не выбран.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
internal object UnselectedState : NavigationItemState()

/**
 * Состояние элемента меню, который выбран программно.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
internal object SelectedState : NavigationItemState()

/**
 * Состояние элемента меню, который выбран пользователем.
 *
 * @param sourceName название источника, который изменил состояние.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
internal data class SelectedByUserState(
    val sourceName: String
) : NavigationItemState()

/**
 * Состояние, при котором произошло нажатие на уже выбранный элемент меню.
 *
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
internal object SelectedSame : NavigationItemState()