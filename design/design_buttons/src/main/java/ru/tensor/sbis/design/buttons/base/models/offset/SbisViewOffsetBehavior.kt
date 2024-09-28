package ru.tensor.sbis.design.buttons.base.models.offset

import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Интерфейс, отвечающий за смещение вертикального положения компонента на указанное количество пикселей
 * в зависимости от поведения компонента.
 *
 * @author mb.kruglova
 */
interface SbisViewOffsetBehavior {

    /**
     * Проверить условие невозможности применить смещение вертикального положения.
     */
    fun checkOffsetTopAndBottomInability(offset: Int): Boolean

    /**
     * Получить поведение.
     */
    fun getSbisViewBehavior(): CoordinatorLayout.Behavior<*>
}