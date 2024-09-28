package ru.tensor.sbis.android_ext_decl

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Базовый UI интерфейс android компонента
 *
 * @author sa.nikitin
 */
interface AndroidComponent {

    /**
     * Позволяет получить [Activity], в которой находится компонент
     *
     * @return [Activity], в которой находится компонент
     */
    fun getActivity(): Activity?

    /**
     * Позволяет получить [Fragment], в которой находится компонент
     *
     * @return [Fragment], в которой находится компонент
     */
    fun getFragment(): Fragment?

    /**
     * Позволяет получить [FragmentManager], для данного компонента
     *
     * @return [FragmentManager], для данного компонента
     */
    fun getSupportFragmentManager(): FragmentManager

}

fun AndroidComponent.getContext(): Context =
    getFragment()?.context
        ?: getActivity()
        ?: throw IllegalArgumentException("Context is null, fragment or activity not found")