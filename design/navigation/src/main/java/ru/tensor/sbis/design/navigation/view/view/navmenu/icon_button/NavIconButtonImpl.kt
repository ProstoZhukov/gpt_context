package ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button

import io.reactivex.Observable

/**
 * Модель кнопки с иконкой для элементов аккордеона.
 *
 * @author ma.kolpakov
 */
class NavIconButtonImpl(
    override val icon: Observable<Int>,
    override val clickListener: IconButtonClickListener
) : NavIconButton