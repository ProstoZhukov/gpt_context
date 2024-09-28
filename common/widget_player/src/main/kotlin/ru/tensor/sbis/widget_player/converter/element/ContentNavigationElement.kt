package ru.tensor.sbis.widget_player.converter.element

import ru.tensor.sbis.widget_player.converter.WidgetID

/**
 * Маркерный интерфейс для элементов, по которым производится поиск и навигация
 * в рамках страницы (к примеру работа с элементами оглавления).
 *
 * @author am.boldinov
 */
interface ContentNavigationElement {

    val id: WidgetID
}