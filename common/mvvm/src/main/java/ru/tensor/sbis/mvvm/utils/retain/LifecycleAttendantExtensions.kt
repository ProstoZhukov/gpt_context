package ru.tensor.sbis.mvvm.utils.retain

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope

/**
 * Возвращает true если фрагмент в состоянии [Lifecycle.State.CREATED] или
 * уже прошел его, т.е. состояния [Lifecycle.State.STARTED] и [Lifecycle.State.RESUMED]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
val Fragment.isCreated: Boolean
    get() = lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)

/** @SelfDocumented */
internal class LifecycleAttendantRetainHolder(
    val wrapper: LifecycleAttendant<*>
) : ViewModel()

/**
 * Создает обертку [LifecycleAttendant] над фрагментом
 *
 * @throws IllegalStateException если вызов [Lazy.getValue] предшествует [Fragment.onAttach].
 * Подробнее: [Fragment.getViewModelStore]
 *
 * @author as.chadov
 */
inline fun <reified OWNER : Fragment> OWNER.retain(): Lazy<LifecycleAttendant<OWNER>> = lazy {
    retain { this }
}.also { delegate ->
    if (isCreated) {
        delegate.value
    } else {
        lifecycleScope.launchWhenCreated { delegate.value }
    }
}

/**
 * Создает обертку [LifecycleAttendant] над lifecycle-компонентом [OWNER]
 *
 * @param OWNER тип элементов, среди которых определяется строка
 * @param factory фабрика [OWNER]
 * @return retain-обертку [LifecycleAttendant]
 *
 * @author as.chadov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun <OWNER : LifecycleOwner> ViewModelStoreOwner.retain(
    factory: () -> OWNER
): LifecycleAttendant<OWNER> {
    val holder = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val wrapper = LifecycleAttendantImpl(factory())
            return LifecycleAttendantRetainHolder(wrapper) as T
        }
    })[LifecycleAttendantRetainHolder::class.java]

    @Suppress("UNCHECKED_CAST")
    return (holder.wrapper as LifecycleAttendantImpl<OWNER>).also {
        if (it.component == null) {
            it.attach(factory())
        }
    }
}