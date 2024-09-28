package ru.tensor.sbis.mvvm

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Ленивое свойство, которое очищается при уничтожении lifecycleOwner.
 *
 * Доступ к этой переменной в уничтоженном lifecycleOwner вызовет [IllegalStateException]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class AutoClearedValue<T : Any>(val lifecycleOwner: LifecycleOwner) :
    ReadWriteProperty<LifecycleOwner, T>,
    LifecycleObserver {

    private var _value: T? by Delegates.vetoable(null) { _, old, new ->
        if (old != new && new != null) {
            /** Удаление коллбэка предотвращает возможный [NullPointerException] вызванный удалением нового [_value], когда
             * [clearBindingHandler] отрабатывает уже после создания нового экземпляра так как не получил времни выполнения на
             * главном потоке сразу после [Fragment.onDestroy] */
            clearBindingHandler.removeCallbacksAndMessages(TOKEN)
            lifecycleOwner.setLifecycleObserver(new)
        }
        true
    }
    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T =
        _value ?: throw IllegalStateException("Should never call auto-cleared-value get when it might not be available")

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        _value = value
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroy() {
        // Вызывается раньше onDestroyView во фрагменте.
        // Для использования view binding в onDestroyView необходимо выполнить очистку позже
        clearBindingHandler.postDelayed(0L, TOKEN) {
            _value = null
        }
    }

    private fun LifecycleOwner.setLifecycleObserver(value: T) {
        val targetLifecycle = if (this is Fragment && value is ViewDataBinding) {
            viewLifecycleOwner.lifecycle
        } else {
            lifecycle
        }
        targetLifecycle.addObserver(this@AutoClearedValue)
    }

    private companion object {
        /**@SelfDocumented*/
        const val TOKEN = "auto_cleared_token"
    }
}

/**
 * Реализация делегата для любого свойства, которое очищается при уничтожении [LifecycleOwner]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun <T : Any> LifecycleOwner.autoCleared() = AutoClearedValue<T>(this)