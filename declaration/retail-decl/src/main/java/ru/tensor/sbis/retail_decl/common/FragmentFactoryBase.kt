package ru.tensor.sbis.retail_decl.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager

/** Интерфейс для обозначения типа фрагмента экземпляр которого мы хотим получить через [FragmentFactory]. */
interface FragmentType {
    /** Класс фрагмента, который хотим создавать через [FragmentFactory]. */
    val type: Class<out Fragment>

    /** Метод создания экземпляра фрагмента, используется [FragmentFactory]. */
    fun FragmentProviderFactory.buildFragment(): Fragment
}

/** Интерфейс поставщика фрагмента диалога. */
interface DialogFragmentProvider<ARGS> : FragmentType {
    /**
     * Создать и получить экземпляр фрагмента для использования в транзакциях.
     * @param fragmentManager [FragmentManager]
     * @param args аргументы передаваемые фрагменту через [Bundle]
     */
    fun createDialogFragment(fragmentManager: FragmentManager, args: ARGS): DialogFragment

    /**
     * Превращает набор информации [args] в [Bundle], который можно передать
     * в качестве аргументов созданному фрагменту. Например, для создания его
     * через [FragmentFactory.instantiate], а не через [createDialogFragment].
     */
    fun buildFragmentArguments(args: ARGS): Bundle?
}

/** Интерфейс поставщика фрагмента. */
interface FragmentProvider<ARGS> : FragmentType {
    /**
     * Создать и получить экземпляр фрагмента для использования в транзакциях.
     * @param fragmentManager [FragmentManager]
     * @param args аргументы передаваемые фрагменту через [Bundle]
     */
    fun createFragment(fragmentManager: FragmentManager, args: ARGS): Fragment

    /**
     * Превращает набор информации [args] в [Bundle], который можно передать
     * в качестве аргументов созданному фрагменту. Например, для создания его
     * через [FragmentFactory.instantiate], а не через [createFragment].
     */
    fun buildFragmentArguments(args: ARGS): Bundle?
}

/**
 * Фабрика поставщиков фрагментов.
 * Основана на [FragmentFactory].
 */
open class FragmentProviderFactory(
    private val providers: Set<FragmentType>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return providers
            .firstOrNull { it.type.name == className }
            ?.let {
                with(it) {
                    buildFragment() // restore
                }
            } ?: super.instantiate(classLoader, className)
    }
}