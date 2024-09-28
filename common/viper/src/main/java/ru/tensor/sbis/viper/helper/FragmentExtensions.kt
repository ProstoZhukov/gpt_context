package ru.tensor.sbis.viper.helper

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

/**
 * Расширение для получения ViewModel из Fragment.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VM : ViewModel> Fragment.getViewModel(): VM {
    val vm: VM by viewModels()
    return vm
}

/**
 * Расширение для получения ViewModel из Activity.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VM : ViewModel> Fragment.getViewModelFromActivity(): VM {
    val vm: VM by requireActivity().viewModels()
    return vm
}