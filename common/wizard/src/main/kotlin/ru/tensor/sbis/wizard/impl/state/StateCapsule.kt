package ru.tensor.sbis.wizard.impl.state

import android.os.Bundle
import android.os.Parcelable

/**
 * Реестр поставщиков текущего состояния какой-либо сущности
 * Используется для дальнейшего сохранения текущего состояния посредством [StateSaver]
 *
 * @param T Тип состояния сущности
 *
 * @author sa.nikitin
 */
internal interface StateProviderRegistry<T : Parcelable> {

    /**
     * Зарегистрировать поставщика состояния
     */
    fun registerStateProvider(provider: () -> T)
}

/**
 * Сущность, сохраняющая текущее состояние какой-либо сущности в [Bundle]
 *
 * @author sa.nikitin
 */
internal interface StateSaver {

    /**
     * Сохранить состояние в [Bundle]
     */
    fun saveTo(bundle: Bundle)
}

/**
 * Сущность, восстанавливающая текущее состояние какой-либо сущности
 *
 * @param T Тип состояния сущности
 *
 * @author sa.nikitin
 */
internal interface StateRestorer<T> {

    /**
     * Восстановить состояние
     */
    fun restore(): T?
}

/**
 * "Капсула" для сохранения состояния какой-либо сущности
 * Реализует [StateProviderRegistry], [StateSaver], [StateRestorer] согласно принципу разделения интерфейсов
 *
 * @param T Тип состояния сущности
 * @property key                Ключ для сохранения состояния в [Bundle]
 * @property savedStateBundle   Предыдущее сохранённое состояние в виде [Bundle], см. [StateSaver.saveTo]
 *
 * @author sa.nikitin
 */
internal class StateCapsule<T : Parcelable>(
    private val key: String,
    private var savedStateBundle: Bundle?
) : StateProviderRegistry<T>, StateSaver, StateRestorer<T> {

    private var stateProvider: (() -> T)? = null

    override fun registerStateProvider(provider: () -> T) {
        stateProvider = provider
    }

    override fun saveTo(bundle: Bundle) {
        stateProvider?.let { bundle.putParcelable(key, it.invoke()) }
    }

    override fun restore(): T? =
        savedStateBundle?.let {
            savedStateBundle = null
            it.getParcelable(key)
        }
}