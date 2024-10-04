package ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button

import io.reactivex.Observable

/**
 * Модель кнопки с иконкой для элементов аккордеона.
 *
 * @author ma.kolpakov
 */
interface NavIconButton {
    /** @SelfDocumented */
    val icon: Observable<Int>

    /** @SelfDocumented */
    val clickListener: IconButtonClickListener
}
