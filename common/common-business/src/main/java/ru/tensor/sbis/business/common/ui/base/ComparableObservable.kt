package ru.tensor.sbis.business.common.ui.base

import androidx.databinding.BaseObservable

/**
 * Интерфейс определяющий эквивалентность экземпляров [BaseObservable] для сравнения и слияния вью моделй
 */
abstract class ComparableObservable : BaseObservable() {

    /** Проверяет представляет ли [old] ту же модель данных что и текущий экземпляр */
    abstract fun isTheSame(old: ComparableObservable): Boolean

    /** Определяет какую из вью моделей использовать в дальнейшем. По умолчанию текущую новую */
    open fun oneOf(old: ComparableObservable): ComparableObservable = this
}