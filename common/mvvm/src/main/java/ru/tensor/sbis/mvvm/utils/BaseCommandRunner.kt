package ru.tensor.sbis.mvvm.utils

import androidx.fragment.app.FragmentManager

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Выполняет команды использующие Activity, Fragment или FragmentManager. Комманты будкт выполнены только во врмя того как Activity или Fragment будут в активном состоянии
 * Есть два варианты выполнения:
 * 1) выполнить сразу, если есть возможность
 * 2) выполнить срузу либо при первой возможности - коммнда быдет выполнена когда Activity или Fragment будут в активном состоянии
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class BaseCommandRunner<CONTEXT_PROVIDER> : LifeCycleCallback<CONTEXT_PROVIDER>() {

    /** Присутствует ли обработчик событий жизненного цикла для данного исполнителя команд */
    var hasLifecycleHandler = false
    private var contextProvider: CONTEXT_PROVIDER? = null
    private val postponedCommands = ConcurrentLinkedQueue<BaseCommand<CONTEXT_PROVIDER>>()
    private var fragmentManager: FragmentManager? = null

    /**
     * Привязывает [CONTEXT_PROVIDER] и [FragmentManager]
     *
     * @param lifeCycleCallbackHolder поставщик [android.content.Context], чей жизненный цикл привязывается
     * @param fragmentManager менеджер фрагментов
     */
    override fun resume(
        lifeCycleCallbackHolder: CONTEXT_PROVIDER,
        fragmentManager: FragmentManager
    ) {
        this.contextProvider = lifeCycleCallbackHolder
        this.fragmentManager = fragmentManager
        while (!postponedCommands.isEmpty()) {
            val command = postponedCommands.poll()
            prepareCommand(command)
            command.run()
        }
    }

    /**
     * Попытается выполнить команду сразу, если есть доступ к контексту,
     * иначе выполнит команду при получении доступа к контексту,
     * то есть когда Fragment или Activity появятся на экране
     * @param action код использующий контекст и/или FragmentManager
     */
    fun runCommandSticky(action: (CONTEXT_PROVIDER, FragmentManager) -> Unit) {
        val command = BaseCommand(action)
        if (contextProvider != null) {
            prepareCommand(command)
            command.run()
        } else {
            postponedCommands.add(command)
        }
    }

    /**
     * Попытается выполнить команду сразу если есть доступ к контексту
     * @param action код использующий контекст и FragmentManager
     */
    fun runCommand(action: (CONTEXT_PROVIDER, FragmentManager) -> Unit) {
        val command = BaseCommand(action)
        if (contextProvider != null) {
            prepareCommand(command)
            command.run()
        }
    }

    /**
     * Аннулирует текущие [CONTEXT_PROVIDER] и [FragmentManager]
     */
    override fun pause() {
        contextProvider = null
        fragmentManager = null
    }

    private fun prepareCommand(command: BaseCommand<CONTEXT_PROVIDER>) {
        command.contextProvider = contextProvider
        command.fragmentManager = fragmentManager
    }
}