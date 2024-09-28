package ru.tensor.sbis.list.view.binding

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleOwnerCompat
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * Реализация с использованием Android Data binding. @see [ViewHolder]
 * Реализует работу с [LifecycleOwner].
 *
 * @property binding ViewDataBinding
 * @constructor создает объект, который будет использовать переданный объект [View] для биндинга
 */
class DataBindingViewHolder(view: View) : ViewHolder(view), LifecycleOwnerCompat {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
    private var variable: MutableMap<Int, Any?> = HashMap()

    init {
        binding.lifecycleOwner = this
        lifecycleRegistry.currentState = State.CREATED
    }

    /**
     * Переводит [lifecycleRegistry] в состояние [State.STARTED] (Тут можно переводить и в состояние [State.RESUMED] особой разницы нет т.к. [ViewDataBinding] важен лишь тот факт, что состояние [State.STARTED] было пройдено)
     * В этом состоянии при изменении в [LiveData] произойдут изменения во вью.
     * Будут вызваны все необходимые события [Lifecycle.Event] для достижения текущего состояния.
     * Например: Если состояние было [State.CREATED], то при переходе в [State.STARTED] будет вызов [Event.ON_START]
     * Если изменения в [LiveData] произошли в состоянии [State.CREATED], то при вызове события [Event.ON_START] необходимые изменения применятся к вью.
     */
    fun onAttach() {
        lifecycleRegistry.currentState = State.STARTED
    }

    /**
     * Переводит [lifecycleRegistry] в состояние [State.CREATED].
     * В этом состоянии при изменении в [LiveData] не произойдет никаких изменений во вью.
     * Будут вызваны все необходимые события [Lifecycle.Event] для достижения текущего состояния.
     * Например: Если состояние было [State.STARTED], то при переходе в [State.CREATED] будет вызов [Event.ON_STOP]
     */
    fun onDetach() {
        lifecycleRegistry.currentState = State.CREATED
    }

    /**
     * Переводит [lifecycleRegistry] в состояние [State.DESTROYED].
     * Дальнейшее использование [lifecycleRegistry] не возможно.
     */
    fun destroy() {
        lifecycleRegistry.currentState = State.DESTROYED
    }

    override fun getLifecycleCompat() = lifecycleRegistry

    /**
     * @see [ViewDataBinding.setVariable]
     */
    fun setVariable(viewModel: Int, data: Any?, doIfSet: () -> Unit = {}) {
        if (variable[viewModel] != data) {
            binding.setVariable(viewModel, data)
            doIfSet()
            variable[viewModel] = data
        }
    }

    fun getVariable(viewModel: Int) = variable[viewModel]

    /**
     * @see [ViewDataBinding.executePendingBindings]
     */
    fun executePendingBindings() {
        binding.executePendingBindings()
    }
}