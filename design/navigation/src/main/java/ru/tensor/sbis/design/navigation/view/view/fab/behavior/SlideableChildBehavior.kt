package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.view.View

/**
 * Интерфейс, предполагающий возможность перемещения [View] вверх или вниз, с анимацией или без.
 *
 * @author ma.kolpakov
 */
interface SlideableChildBehavior<V : View> {

    /**
     * Определяет расстояние от [View] до нижней границы экрана, исходя из которого определяется позиция для
     * перемещения [View] вниз.
     */
    var spacing: Int

    /**
     * Определяет поведение по перемещению [View] вверх.
     */
    fun slideUp(child: V, animated: Boolean)

    /**
     * Определяет поведение по перемещению [View] вниз.
     */
    fun slideDown(child: V, animated: Boolean)
}