package ru.tensor.sbis.design.utils

import androidx.annotation.MainThread
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 *      Поддержка лаконичной делегации параметров для внутренних классов.
 *
 *      Использование:
 *      private val delegate = DelegatedClass()
 *      var param by delegateProperty(delegate::param)
 *
 *      param после этого можно использовать точно также как если бы delegate был публичным.
 *
 *      NB: Этот метод создает активную ссылку на класс - делегат, которая будет существовать пока существует базовый класс.
 * @param delegate рефлексивная ссылка на делегируемый параметр
 */
fun <T> delegateProperty(delegate: KMutableProperty0<T>): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = delegate.get()
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = delegate.set(value)
    }

/**
 *      Поддержка лаконичной делегации параметров для внутренних классов, определенных через геттер и сеттер.
 *
 *      Использование:
 *      private val delegate = DelegatedClass()
 *      var param by delegatePropertyMT(delegate::getParam, delegate::setParam)
 *
 *      param после этого можно использовать точно также как если бы delegate был публичным.
 *
 *      NB: Этот метод создает активную ссылку на класс - делегат, которая будет существовать пока существует базовый класс.
 * @param getter ссылка на геттер
 * @param setter ссылка на сеттер
 */
inline fun <T> delegateProperty(
    crossinline getter: () -> T,
    crossinline setter: (T) -> Unit
): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter(value)
}

/**
 *      Поддержка лаконичной делегации параметров для внутренних классов.
 *      Эта версия функции допускает установку параметра только с MainThread. Хорошо подходит для View.
 *
 *      Использование:
 *      private val delegate = DelegatedClass()
 *      var param by delegateProperty(delegate::param)
 *
 *      param после этого можно использовать точно также как если бы delegate был публичным.
 *
 *      NB: Этот метод создает активную ссылку на класс - делегат, которая будет существовать пока существует базовый класс.
 * @param delegate рефлексивная ссылка на делегируемый параметр
 */
fun <T> delegatePropertyMT(delegate: KMutableProperty0<T>): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = delegate.get()

        @MainThread
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = delegate.set(value)
    }

/**
 *      Поддержка лаконичной делегации параметров для внутренних классов, определенных через геттер и сеттер.
 *      Эта версия функции допускает установку параметра только с MainThread. Хорошо подходит для View.
 *
 *      Использование:
 *      private val delegate = DelegatedClass()
 *      var param by delegatePropertyMT(delegate::getParam, delegate::setParam)
 *
 *      param после этого можно использовать точно также как если бы delegate был публичным.
 *
 *      NB: Этот метод создает активную ссылку на класс - делегат, которая будет существовать пока существует базовый класс.
 * @param getter ссылка на геттер
 * @param setter ссылка на сеттер
 */
inline fun <T> delegatePropertyMT(
    crossinline getter: () -> T,
    crossinline setter: (T) -> Unit
): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()

    @MainThread
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter(value)
}

/**
 * [setter] у делегата вызывается только в том случае, если значение property изменилось.
 *
 * @param initValue начальное значение.
 * @param setter ссылка на сеттер.
 */
inline fun <T> delegateNotEqual(initValue: T, crossinline setter: (T) -> Unit): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        var value = initValue
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (this.value == value) return
            this.value = value
            setter(value)

        }
    }

/**
 * [setter] у делегата вызывается только в том случае, если значение property изменилось.
 *
 * @param initValue начальное значение.
 * @param setter ссылка на сеттер, у которого первое значение - старый value, второе - новый.
 */
inline fun <T> delegateNotEqual(initValue: T, crossinline setter: (T, T) -> Unit): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        var value = initValue
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (this.value == value) return
            val oldValue = this.value
            this.value = value
            setter(oldValue, value)
        }
    }


