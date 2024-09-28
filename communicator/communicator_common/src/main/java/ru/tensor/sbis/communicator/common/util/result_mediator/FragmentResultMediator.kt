package ru.tensor.sbis.communicator.common.util.result_mediator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Абстрактный класс для передачи результатов между фрагментами через FragmentManager.
 *
 * @param T Тип данных результата, который будет передаваться.
 *
 * @author da.zhukov.
 */
abstract class FragmentResultMediator<T> {

    /**
     * Абстрактное свойство, которое должно быть реализовано в подклассах для предоставления ключа результата.
     */
    abstract val key: String

    /**
     * Устанавливает слушатель для получения результата в родительском фрагменте.
     *
     * @param parentFragment Родительский фрагмент, который будет слушать результат.
     * @param onResult Лямбда-функция, вызываемая при получении результата.
     */
    fun setResultListener(parentFragment: Fragment, onResult: (T) -> Unit) {
        parentFragment.childFragmentManager.setFragmentResultListener(key, parentFragment) { _, bundle ->
            @Suppress("UNCHECKED_CAST")
            val result = bundle.getSerializable(key) as? T
            result?.let {
                parentFragment.lifecycleScope.launch {
                    onResult(it)
                }
            }
        }
    }

    /**
     * Передает результат в родительский FragmentManager.
     *
     * @param childFragment Дочерний фрагмент, из которого передается результат.
     * @param result Результат, который необходимо передать.
     */
    fun provideResult(childFragment: Fragment, result: T) {
        val bundle = Bundle().apply {
            putSerializable(key, result as java.io.Serializable)
        }
        childFragment.parentFragmentManager.setFragmentResult(key, bundle)
    }
}