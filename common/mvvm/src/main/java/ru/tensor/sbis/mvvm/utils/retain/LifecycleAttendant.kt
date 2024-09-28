package ru.tensor.sbis.mvvm.utils.retain

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.Delegates

/** retain-обертка над фрагментом */
typealias FragmentAttendant = LifecycleAttendant<out Fragment>

/**
 * Интерфейс retain-обертки над lifecycle-компонентом [OWNER].
 * Используется как альтетнатива [Fragment.mRetainInstance] для получения текущего lifecycle-компонента и
 * инжектирования в lifecycle-осведомленные решения, например роутеры
 *
 * Пример создания:
 * val wrapper by retain()
 *
 * Пример использования:
 * class Router @Inject constructor(
fragmentWrapper: FragmentAttendant
 * ) {
 *      init { fragmentWrapper.bind(::manageBy) }
 * }
 *
 * @author as.chadov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface LifecycleAttendant<OWNER : LifecycleOwner> {

    /** Ссылка на текущий экземпляр LifecycleOwner компонента */
    val component: OWNER?

    /** Подписаться на изменения экземпляра компонента */
    fun bind(onChange: (OWNER) -> Unit)
}

/**
 * Реализация [LifecycleAttendant]
 */
internal class LifecycleAttendantImpl<OWNER : LifecycleOwner>(fragment: OWNER? = null) :
    LifecycleAttendant<OWNER>,
    LifecycleObserver {

    private var _value: OWNER? by Delegates.observable(null) { _, _, newValue ->
        if (newValue == null) return@observable
        newValue.lifecycle.addObserver(this@LifecycleAttendantImpl)
        _onChange.forEach { callback ->
            callback(newValue)
        }
    }
    private var _onChange = mutableListOf<((OWNER) -> Unit)>()

    init {
        _value = fragment
    }

    override val component get() = _value

    override fun bind(onChange: (OWNER) -> Unit) {
        _onChange.add(onChange)
        _value?.let { onChange(it) }
    }

    fun attach(component: OWNER) {
        _value = component
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroy() {
        _value = null
    }
}

