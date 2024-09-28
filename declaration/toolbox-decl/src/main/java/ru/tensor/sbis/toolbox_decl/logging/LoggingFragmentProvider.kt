package ru.tensor.sbis.toolbox_decl.logging

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

interface LoggingFragmentProvider : Feature {

    /**
     * Предоставляет хост фрагмент модуля логирования.
     */
    fun getLoggingHostFragment(withNavigation: Boolean = true): Fragment

    /**
     * Предоставляет фрагмент с элементами управления записанными файлами логов
     */
    fun getLoggingFragment(): Fragment

    /**
     * Предоставляет фрагмент с настройками экрана логов.
     */
    fun getLoggingSettingsFragment(): Fragment

    /**
     * Предоставляет BottomSheet фрагмент с настройками экрана логов.
     */
    fun getLoggingSettingsDialogFragment(): DialogFragment
}