package ru.tensor.sbis.viper.helper

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

/**
 * Расширение для получения ViewModel из Activity.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VM : ViewModel> FragmentActivity.getViewModel(): VM =
    ViewModelProviders.of(this).get(VM::class.java)

