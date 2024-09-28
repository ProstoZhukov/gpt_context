package ru.tensor.sbis.mvvm.utils

import androidx.fragment.app.FragmentManager

/**
 * Команда для выполнения кода с заданными [CONTEXT_PROVIDER] и
 * [FragmentManager]
 *
 * @param <CONTEXT_PROVIDER> класс, предоставляющий [android.content.Context]
</CONTEXT_PROVIDER> */
/**
 * Создаёт команду
 *
 * @param action выполняемый код
 */
internal class BaseCommand<CONTEXT_PROVIDER>(private val action: (CONTEXT_PROVIDER, FragmentManager) -> Unit) {

    var contextProvider: CONTEXT_PROVIDER? = null
    var fragmentManager: FragmentManager? = null

    /**
     * Выполняет [action], передавая в качестве аргументов [.contextProvider] и
     * [.fragmentManager]
     */
    fun run() {
        action.invoke(contextProvider!!, fragmentManager!!)
    }
}