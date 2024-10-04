package ru.tensor.sbis.design.navigation.view.widget.components.icon

import io.reactivex.Observable

/**
 * Модель виджета, у которого есть иконка.
 *
 * @author ma.kolpakov
 */
internal interface IconWidget {

    /**
     * Иконка виджета.
     */
    val icon: Observable<Int>

    /**
     * Ресурс цвета иконки.
     */
    val iconColor: Observable<Int>
}