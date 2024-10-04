package ru.tensor.sbis.design.navigation.view.view

import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

/**
 * Описание общего для функционала для меню.
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
interface NavigationView {

    /**
     * Установка шрифта и размера использующегося для иконок ННП и аккордеона.
     */
    fun setIsUsedNavigationIcons(isUsed: Boolean)

    /**
     * @SelfDocumented
     * @see setIsUsedNavigationIcons
     */
    fun isUsedNavigationIcons(): Boolean

    /**
     * Установка адаптера со списком элементов меню. Возможна установка пустого адаптера с
     * последующим добавлением элементов.
     */
    fun setAdapter(navAdapter: NavAdapter<out NavigationItem>, lifecycleOwner: LifecycleOwner)

    /**
     * Скрытие элемента меню.
     */
    fun hideItem(item: NavigationItem)

    /**
     * Отображение элемента меню, если присутствует.
     */
    fun showItem(item: NavigationItem)

    /**
     * Смена иконки элемента меню на новую.
     *
     * Создано для обработки иконок с контроллера (аля internal).
     */
    fun changeItemIcon(item: NavigationItem, icons: ControllerNavIcon)

    /**
     * Обновить текст элемента меню.
     */
    fun changeItemLabel(item: NavigationItem, label: NavigationItemLabel)

    /**
     * Установка конфигурации аккордеона.
     */
    fun setConfiguration(configuration: NavViewConfiguration)
}