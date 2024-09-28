package ru.tensor.sbis.design.navigation.view

/**
 * Интерфейс компонентов навигации, которые поддерживают скрытие.
 *
 * @author ma.kolpakov
 */
@Suppress("unused")
interface BottomNavigationVisibility {

    /**
     * Запрос изменение видимости нижней панели меню.
     *
     * @param isVisible если `false` - панель скроется, иначе покажется.
     */
    fun setBottomNavigationVisibility(isVisible: Boolean)
}
