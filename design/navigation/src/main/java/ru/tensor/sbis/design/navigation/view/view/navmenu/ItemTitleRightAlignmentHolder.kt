package ru.tensor.sbis.design.navigation.view.view.navmenu

import io.reactivex.subjects.BehaviorSubject

/**
 * Хранитель позиции правой стороны заголовка элемента аккордеона. Собирает в себе информацию изо всех элементов.
 * Необходим для реализации логики отображения дочерних элементов
 *
 * @author ma.kolpakov
 */
internal class ItemTitleRightAlignmentHolder {
    private val subjects = mutableMapOf<Int, BehaviorSubject<Int>>()

    fun setItemRight(id: Int, value: Int) {
        subjects.getOrPut(id) { BehaviorSubject.create() }.onNext(value)
    }

    fun getSubject(id: Int) = subjects.getOrPut(id) { BehaviorSubject.create() }
}
