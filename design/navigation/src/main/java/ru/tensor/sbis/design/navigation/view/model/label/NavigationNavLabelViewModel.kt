package ru.tensor.sbis.design.navigation.view.model.label

import io.reactivex.Observable

/**
 * Модель надписей элементов меню в аккордеоне.
 *
 * @author ma.kolpakov
 */
internal interface NavigationNavLabelViewModel {
    /**
     * Название элемента меню в Аккордеоне.
     */
    val navViewLabel: Observable<Int>

    /**
     * Текст, по правому краю которого должно выравниваться название элемента меню, при истинном значении
     * [isLabelAlignedRight].
     */
    val labelForRightAlignment: Observable<Int>

    /**
     * Должно ли название элемента меню выравниваться по правому краю [labelForRightAlignment].
     */
    val isLabelAlignedRight: Observable<Boolean>
}