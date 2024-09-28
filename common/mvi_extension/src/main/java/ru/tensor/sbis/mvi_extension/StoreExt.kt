package ru.tensor.sbis.mvi_extension

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistry
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.essenty.statekeeper.stateKeeper
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create
import kotlin.reflect.KClass

//region store factory
/**
 * Функция обертка над [StoreFactory.create], служит для подписки на сохранение стейта в [SavedStateRegistry].
 *
 * [stateKeeper] - объект управляющий сохранением state,
 * [saveStateSupplier] - лямбда в которой можно изменить state перед сохранением.
 */
inline fun <Intent : Any, Action : Any, Message : Any, reified State : Parcelable, Label : Any> StoreFactory.create(
    stateKeeper: StateKeeper,
    name: String? = null,
    autoInit: Boolean = true,
    initialState: State,
    bootstrapper: Bootstrapper<Action>? = null,
    noinline executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
    reducer: Reducer<State, Message>,
    noinline saveStateSupplier: (State) -> State = { it }
): Store<Intent, State, Label> {
    val stateKey = State::class.toString()
    val savedState = stateKeeper.consume(stateKey, State::class)
    return create(
        name,
        autoInit,
        savedState ?: initialState,
        bootstrapper,
        executorFactory,
        reducer
    ).apply {
        stateKeeper.register(stateKey) {
            saveStateSupplier.invoke(state)
        }
    }
}
//endregion

//region save store fragment
/**
 * Ищет store, инициализированный в иерархии выше.
 */
fun <T : Store<*, *, *>> Fragment.findStore(keyClass: KClass<T>) =
    lazy(LazyThreadSafetyMode.NONE) { findStoreInternal(keyClass) }

/**
 * Ищет store, инициализированный в иерархии выше. Может понадобиться в случаях:
 * - Ключ не константный, если в бекстеке несколько фрагментов и нужно найти store "правильного". Например, если есть
 * карточка типа A, в ней открыта шторка AA, есть карточка типа B, в ней открыта шторка BA, то через константный ключ
 * из шторки BA получить стор A невозможно.
 * - Поделиться какими-то специфичными экземплярами зависимостей, например объектами бизнес логики без
 * передачи в качестве Parcelize через аргументы Fragment-ов, который накладывает ограничения и их нельзя обойти, либо
 * когда экземпляр контроллера не синглтон и его нужно переиспользовать в разных фрагментах (вообще не является
 * Parcelize). Можно завести интерфейс A, а его геттеры определить в непубличных сторах B : A и С : A в разных модулях,
 * значит можно и получить экземпляр типа A без раскрытия деталей B или C и "не светить" их стейты.
 * @param key ключ экземпляра.
 * @param T тип, к которому нужно привести экземпляр стора.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Fragment.findStore(key: Any) =
    lazy(LazyThreadSafetyMode.NONE) { findStoreInternal<Store<*, *, *>>(key) as T }

/**
 * Функция сохраняет store для переживания переворота.
 *
 * [factory] - лямбда-фабрика, создаюшая store если его нет.
 */
inline fun <reified T : Store<*, *, *>> Fragment.provideStore(noinline factory: (StateKeeper) -> T): T =
    provideStore(key = T::class, factory = factory)

/**
 * Функция сохраняет store для переживания переворота.
 *
 * [key] - ключ для сохранения стора.
 *
 * [factory] - лямбда-фабрика, создаюшая store если его нет.
 */
fun <T : Store<*, *, *>> Fragment.provideStore(key: Any, factory: (StateKeeper) -> T): T =
    instanceKeeper().getOrCreate(key = key) {
        StoreHolder(factory(stateKeeper()))
    }.store
//endregion

//region save store activity
/**
 * Функция сохраняет store для переживания переворота.
 *
 * [factory] - лямбда-фабрика, создаюшая store если его нет.
 */
inline fun <reified T : Store<*, *, *>> AppCompatActivity.provideStore(noinline factory: (StateKeeper) -> T): T =
    provideStore(key = T::class, factory = factory)

/**
 * Функция сохраняет store для переживания переворота.
 *
 * [key] - ключ для сохранения стора.
 *
 * [factory] - лямбда-фабрика, создаюшая store если его нет.
 */
fun <T : Store<*, *, *>> AppCompatActivity.provideStore(key: Any, factory: (StateKeeper) -> T): T =
    instanceKeeper().getOrCreate(key = key) {
        StoreHolder(factory(stateKeeper()))
    }.store
//endregion

//region internal
private fun <T : Store<*, *, *>> Fragment.findStoreInternal(keyClass: KClass<T>): T {
    val instance = instanceKeeper().getInstance<StoreHolder<T>>(keyClass)
    return instance?.store ?: parentFragment?.findStoreInternal(keyClass)
    ?: activity?.instanceKeeper()
        ?.getInstance<StoreHolder<T>>(keyClass)?.store
    ?: error("Can't find store anywere")
}

private fun <T : Store<*, *, *>> Fragment.findStoreInternal(key: Any): T {
    val instance = instanceKeeper().getInstance<StoreHolder<T>>(key)
    return instance?.store ?: parentFragment?.findStoreInternal(key)
    ?: activity?.instanceKeeper()
        ?.getInstance<StoreHolder<T>>(key)?.store
    ?: error("Can't find store anywere")
}

private inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.getInstance(key: Any): T? {
    return get(key) as? T?
}

class StoreHolder<out T : Store<*, *, *>>(
    val store: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        store.dispose()
    }
}
//endregion
