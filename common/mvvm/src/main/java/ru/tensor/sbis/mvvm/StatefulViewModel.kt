package ru.tensor.sbis.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Базовый класс вьюмодели экрана с утилитами для сохранения состояния
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class StatefulViewModel(val state: SavedStateHandle) : ViewModel() {

    /**
     * Проперти-делегат для получения значений из аргументов фрагмента
     * @param key ключ по которому лежит нужное значение в хранилище
     * @param default значение по умолчанию
     */
    inline fun <reified T> arg(
        key: String,
        default: T? = null
    ) = ReadOnlyProperty<StatefulViewModel, T> { thisRef, _ ->
        val result = thisRef.state.get<T>(key)
        if (result == null && default != null) default else result as T
    }

    /**
     * Проперти-делегат для сохранения и получения значений по ключу.
     * Данные присутствуют после восстановление активности после вытеснения.
     * @param key ключ по которому лежит нужное значение в хранилище.
     * @param default значение по умолчанию
     */
    inline fun <reified T> stateful(
        default: T? = null,
        crossinline key: (KProperty<*>) -> String = KProperty<*>::name
    ) = object : ReadWriteProperty<StatefulViewModel, T> {

        override fun getValue(thisRef: StatefulViewModel, property: KProperty<*>): T {
            val result = state.get<T>(key(property))
            return if (result == null && default != null) default else result as T
        }

        override fun setValue(thisRef: StatefulViewModel, property: KProperty<*>, value: T) =
            thisRef.state.set(key(property), value)
    }

    /**
     * Проперти-делегат для сохранения и получения значений по ключу через SavingStateLiveData.
     * Данные присутствуют после восстановление активности после вытеснения.
     * @param key ключ по которому лежит нужное значение в хранилище.
     * @param default значение по умолчанию
     */
    inline fun <T> statefulLiveData(
        default: T,
        crossinline key: (KProperty<*>) -> String = KProperty<*>::name
    ) = ReadOnlyProperty<StatefulViewModel, MutableLiveData<T>> { thisRef, property ->
        (thisRef.state.getLiveData(key(property), default))
    }
}