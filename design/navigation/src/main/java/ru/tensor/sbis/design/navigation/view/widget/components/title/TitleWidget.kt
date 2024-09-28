package ru.tensor.sbis.design.navigation.view.widget.components.title

import io.reactivex.Observable

/**
 * Модель виджета, у которого есть заголовок.
 *
 * @author ma.kolpakov
 */
internal interface TitleWidget {

    /**
     * Текст заголовка.
     */
    val title: Observable<String>
}