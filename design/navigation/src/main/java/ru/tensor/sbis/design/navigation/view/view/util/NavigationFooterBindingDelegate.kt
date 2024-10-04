package ru.tensor.sbis.design.navigation.view.view.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

/**
 * Предназначен для привязки данных ко view "подвала" аккордеона.
 *
 * @author us.bessonov
 */
interface NavigationFooterBindingDelegate<VB : ViewBinding> : FooterDelegateOptional<VB> {

    /** @SelfDocumented */
    fun getViewBinding(view: View): VB

    /**
     * Устанавливает обработчик нажатий для индикации выбора элемента.
     */
    fun setClickSelectionListener(binding: VB, listener: () -> Unit)

    /**
     * Привязывает [LiveData], определяющую состояние выбора элемента.
     */
    fun setSelectionLiveData(binding: VB, lifecycleOwner: LifecycleOwner, liveData: LiveData<Boolean>)

    /** @SelfDocumented */
    fun bind(binding: VB)
}