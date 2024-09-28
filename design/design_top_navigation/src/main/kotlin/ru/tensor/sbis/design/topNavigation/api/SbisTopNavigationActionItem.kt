package ru.tensor.sbis.design.topNavigation.api

import android.view.View
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton

/**
 * Управляющий элемент в шапке.
 *
 * @author da.zolotarev
 */
sealed interface SbisTopNavigationActionItem {
    /**
     * Кнопка-иконка. Преобразуется в платформенную кнопку в едином для всех экранов стиле.
     * Будет помещена в контейнер.
     *
     * Для управления видимостью кнопки и счетчиком, можно использовать [SbisTopNavigationIconButtonViewConfigurator].
     */
    data class IconButton(val button: SbisTopNavigationIconButton) : SbisTopNavigationActionItem

    /**
     * Платформенная кнопка.
     *
     * Будет помещена в контейнер.
     *
     * ВАЖНО! для управления видимостью такой кнопки, нужно скрывать не саму кнопку, а ее родителя.
     */
    data class Button(val button: AbstractSbisButton<*, *>) : SbisTopNavigationActionItem

    /**
     * Кастомная вью.
     */
    data class CustomView(val view: View) : SbisTopNavigationActionItem
}