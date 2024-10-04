package ru.tensor.sbis.design.navigation.view.view.tabmenu

import ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation.OrientationStrategy

/**
 * Класс отвечающий за состояние видимости кнопки меню в ННП.
 *
 * @author da.zolotarev
 */
enum class MenuButtonVisibility(val code: Int) {
    /** Видимость кнопки меню определяется автоматом в [OrientationStrategy]. */
    AUTO(0),

    /** Кнопка меню всегда видима. */
    VISIBLE(1),

    /** Кнопка меню всегда скрыта. */
    HIDDEN(2)
}