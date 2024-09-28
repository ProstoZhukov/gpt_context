package ru.tensor.sbis.base_app_components

import ru.tensor.sbis.base_app_components.initializers.AsyncInitializer
import ru.tensor.sbis.base_app_components.initializers.BlockingInitializer
import ru.tensor.sbis.base_app_components.initializers.ConcurrentAsyncInitializer
import ru.tensor.sbis.base_app_components.initializers.ConcurrentBlockingInitializer
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer

/**
 * Контракт фабрики для создания инициализатора МП.
 *
 * @see AppInitializer
 */
fun interface AppInitializerFactory<T> {

    fun create(
        initStartupExposer: Boolean,
        controllerAction: () -> Unit,
        doInitPluginSystem: () -> Unit,
        doAfterInitPluginSystem: () -> Unit
    ): AppInitializer<T>

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    companion object {


        /** @SelfDocumented */
        @Suppress("FunctionName")
        fun SequenceBlocking(): AppInitializerFactory<*> = AppInitializerFactory { _,
                                                                                   controllerAction,
                                                                                   doInit,
                                                                                   doAfterInit ->
            BlockingInitializer(
                controllerAction = controllerAction,
                uiAction = {
                    doInit.invoke()
                    doAfterInit.invoke()
                }
            )
        }

        /** @SelfDocumented */
        @Suppress("FunctionName")
        fun SequenceAsync(): AppInitializerFactory<*> = AppInitializerFactory { initStartupExposer,
                                                                                controllerAction,
                                                                                doInit,
                                                                                doAfterInit ->
            AsyncInitializer(
                withUiStatus = initStartupExposer,
                controllerAction = controllerAction,
                uiAction = {
                    doInit.invoke()
                    doAfterInit.invoke()
                }
            )
        }

        /** @SelfDocumented */
        @Suppress("FunctionName")
        fun ConcurrentBlocking(): AppInitializerFactory<*> = AppInitializerFactory { _,
                                                                           controllerAction,
                                                                           doInit,
                                                                           doAfterInit ->
            ConcurrentBlockingInitializer(
                controllerAction = controllerAction,
                doInit = doInit,
                doAfterInit = doAfterInit
            )
        }

        /** @SelfDocumented */
        @Suppress("FunctionName")
        fun ConcurrentAsync(): AppInitializerFactory<*> = AppInitializerFactory { initStartupExposer,
                                                                        controllerAction,
                                                                        doInit,
                                                                        doAfterInit ->
            ConcurrentAsyncInitializer(
                withUiStatus = initStartupExposer,
                controllerAction = controllerAction,
                doInit = doInit,
                doAfterInit = doAfterInit
            )
        }
    }
}
