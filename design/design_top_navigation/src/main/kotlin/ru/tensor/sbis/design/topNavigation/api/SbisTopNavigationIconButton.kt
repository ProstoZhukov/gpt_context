package ru.tensor.sbis.design.topNavigation.api

import android.view.View
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Модель кнопки-иконки.
 *
 * @param icon - иконка.
 * @param counter - счетчик.
 * @param action - действие на клик.
 *
 * @author da.zolotarev
 */
data class SbisTopNavigationIconButton(
    val icon: SbisMobileIcon.Icon,
    val counter: Int = 0,
    val action: (View) -> Unit = {}
)