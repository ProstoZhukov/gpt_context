package ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button

/**
 * Подписка на нажатия по иконке виджета.
 *
 * @author ma.kolpakov
 */
fun interface IconButtonClickListener {

    /**
     * Обработка нажатия на иконку виджета.
     */
    fun onIconClicked()
}