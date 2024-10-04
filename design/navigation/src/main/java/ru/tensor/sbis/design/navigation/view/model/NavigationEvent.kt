package ru.tensor.sbis.design.navigation.view.model

/**
 * Событие компонента навигации.
 *
 * @param T пользовательский тип элемента меню [NavigationItem]
 *
 * @author ma.kolpakov
 * Создан 2/4/2019
 */
sealed class NavigationEvent<out T : NavigationItem>

/**
 * Событие программного выбора элемента меню.
 *
 * @param selectedItem элемент меню, который установлен выбранным.
 * @param selectedItemParent родитель выбранного элемента меню, если он имеется.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
data class ItemSelected<T : NavigationItem>(
    val selectedItem: T,
    val selectedItemParent: T?
) : NavigationEvent<T>()

/**
 * Событие выбора элемента меню пользователем.
 *
 * @param selectedItem элемент меню, который выбрал пользователь.
 * @param selectedItemParent родитель выбранного элемента меню, если он имеется.
 * @param sourceName источник события.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
data class ItemSelectedByUser<T : NavigationItem>(
    val selectedItem: T,
    val selectedItemParent: T?,
    val sourceName: String
) : NavigationEvent<T>()

/**
 * Событие, которое сигнализирует, что все элементы меню установлены невыбранными.
 *
 * @author ma.kolpakov
 * Создан 2/4/2019
 */
object AllItemsUnselected : NavigationEvent<Nothing>()

/**
 * Событие выбора элемента меню пользователем, которое уже выбрано.
 *
 * @param selectedItem элемент меню, который выбрал пользователь.
 * @param selectedItemParent родитель выбранного элемента меню, если он имеется.
 */
data class SelectedSameItem<T : NavigationItem>(
    val selectedItem: T,
    val selectedItemParent: T?
) : NavigationEvent<T>()
