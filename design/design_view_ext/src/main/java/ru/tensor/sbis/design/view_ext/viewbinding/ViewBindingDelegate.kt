package ru.tensor.sbis.design.view_ext.viewbinding

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Создать view binding с помощью делегата
 * Очистка биндинга происходит по средствам подписки на методы жизненного цикла фрагмента
 *
 * @param binder метод создания биндинга
 * @param fragmentViewProvider провайдер [View] фрагмента, которая будет привязана к [ViewBinding]
 *
 * Пример использования:
 *```
 * private val binding: SampleFragmentViewBinding by viewBinding(SampleFragmentViewBinding::bind)
 * ```
 *
 * @author ns.staricyn
 */
fun <T : ViewBinding> Fragment.viewBinding(
    binder: (View) -> T,
    fragmentViewProvider: () -> View = { this.requireView() }
): ReadOnlyProperty<Fragment, T> =
    FragmentViewBindingDelegate(this, binder, fragmentViewProvider)

/**
 * Делегат для создания view binding фрагмента. View Binding создается с помощью метода bind к view фрагмента
 * Имеет подписку на жизненный цикл фрагмента для очистки биндинга
 *
 * @param fragment фрагмент для биндинга
 * @param binder метод создания биндинга
 * @param fragmentViewProvider провайдер [View] фрагмента, которая будет привязана к [ViewBinding]
 *
 * @author ns.staricyn
 */
@PublishedApi
internal class FragmentViewBindingDelegate<T : ViewBinding>(
    fragment: Fragment,
    private val binder: (View) -> T,
    private val fragmentViewProvider: () -> View = { fragment.requireView() },
) :
    ReadOnlyProperty<Fragment, T> {

    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
    private var binding: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    // Вызывается раньше onDestroyView во фрагменте.
                    // Для использования view binding в onDestroyView необходимо выполнить очистку позже
                    clearBindingHandler.post { binding = null }
                }
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class)
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        // onCreateView может вызваться раньше очистки старого биндига, поэтому
        // проверим соответствие корневых вью текущего фрагмента и сохраненного биндинга
        if (binding != null && binding?.root !== fragmentViewProvider.invoke()) {
            binding = null
        }
        return binding ?: binder(fragmentViewProvider.invoke()).also { binding = it }
    }
}