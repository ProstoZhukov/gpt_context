package ru.tensor.sbis.entrypoint_guard

import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant
import ru.tensor.sbis.entrypoint_guard.activity.LegacyDetectorCallback
import ru.tensor.sbis.entrypoint_guard.bcr.BroadCastAssistant
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import ru.tensor.sbis.entrypoint_guard.init.AppInitializerStub
import ru.tensor.sbis.entrypoint_guard.service.ServiceAssistant
import ru.tensor.sbis.entrypoint_guard.util.InitActivityHolderCallback
import ru.tensor.sbis.entrypoint_guard.util.waitTerminalState
import ru.tensor.sbis.entrypoint_guard.work.WorkerAssistant

/**
 * Компонент, контролирующий все внешние точки входа:
 * - Activity;
 * - BroadcastReceiver;
 * - Service;
 * - Worker of WorkManager.
 * Предоставляет возможность делегировать вызовы жизненного цикла компонентов специальным перехватчикам
 * ([ActivityAssistant], [BroadCastAssistant], [ServiceAssistant], [WorkerAssistant]),
 * которые учитывают специфику инициализации конкретных приложений (настройка выполняется на уровне приложения).
 *
 * @author kv.martyshenko
 */
object EntryPointGuard {
    private var application: Application? = null

    private val coroutineScope = MainScope()

    private var appInitializer: AppInitializer<*> = AppInitializerStub

    private val cachedActivityCallbacks: MutableList<ActivityLifecycleCallbacks> = mutableListOf()
    private val cbMutex = Mutex()

    /**
     * Экземпляр [BaseContextPatcher].
     */
    var baseContextPatcher: BaseContextPatcher = BaseContextPatcher { it }
        private set

    /**
     * Экземпляр [ActivityAssistant].
     */
    val activityAssistant: ActivityAssistant by lazy {
        ActivityAssistant(
            applicationProvider = { application },
            appStateProvider = { appInitializer as AppInitStateHolder<Any?> },
            progressHandlerProvider = { appInitializer.progressHandler as AppInitializer.ProgressHandler<Any?> },
            baseContextPatcherProvider = { baseContextPatcher }
        )
    }

    /**
     * Экземпляр [BroadCastAssistant].
     */
    val broadCastAssistant: BroadCastAssistant by lazy {
        BroadCastAssistant(
            coroutineScope = coroutineScope,
            applicationProvider = { application },
            appStateProvider = { appInitializer }
        )
    }

    /**
     * Экземпляр [ServiceAssistant].
     */
    val serviceAssistant: ServiceAssistant by lazy {
        ServiceAssistant(
            coroutineScope = coroutineScope,
            applicationProvider = { application },
            appStateProvider = { appInitializer },
            baseContextPatcherProvider = { baseContextPatcher }
        )
    }

    /**
     * Экземпляр [WorkerAssistant].
     */
    val workerAssistant: WorkerAssistant by lazy {
        WorkerAssistant(
            applicationProvider = { application },
            appStateProvider = { appInitializer }
        )
    }

    /**
     * Запустить процесс инициализации приложения.
     *
     * @param initializer специфический инициализатор для приложения.
     */
    @MainThread
    fun <T> T.initializeApp(initializer: AppInitializer<*>) where T : Application, T : EntryPoint {
        appInitializer = initializer
        appInitializer.initialize(this, coroutineScope)
        application = this
        registerActivityLifecycleCallbacks(LegacyDetectorCallback())
        restoreSkippedActivityCallbacks(this, initializer)
    }

    private fun restoreSkippedActivityCallbacks(app: Application, initStateHolder: AppInitStateHolder<*>) {
        val holder = InitActivityHolderCallback()
        app.registerActivityLifecycleCallbacks(holder)

        fun clearHolder() {
            holder.clear()
            app.unregisterActivityLifecycleCallbacks(holder)
        }

        coroutineScope.launch(Dispatchers.IO) {
            val initStatus = initStateHolder.waitTerminalState().getOrElse {
                clearHolder()
                return@launch
            }

            if (initStatus == InitStatus.InitCompleted) {
                val activity: AppCompatActivity? = holder.get()

                /**
                 * Если активности еще нет - это косвенный признак, что происходит восстановление процесса, т.к оно блокирующее.
                 * В таком случае, отдаем колбеки на откуп системе.
                 */
                if (activity == null) {
                    cbMutex.withLock {
                        cachedActivityCallbacks.forEach(app::registerActivityLifecycleCallbacks)
                        cachedActivityCallbacks.clear()
                    }
                    clearHolder()
                    return@launch
                }

                clearHolder()

                cbMutex.withLock {
                    withContext(Dispatchers.Main.immediate) {
                        val activityState = activity.lifecycle.currentState
                        cachedActivityCallbacks.forEach { cb: ActivityLifecycleCallbacks ->
                            activityState.restoreLifecycleFor(cb, activity)
                            app.registerActivityLifecycleCallbacks(cb)
                        }
                    }
                    cachedActivityCallbacks.clear()
                }
            } else if (initStatus is InitStatus.InitFailure) {
                clearHolder()
            }
        }
    }

    /**
     * Попытаемся восстановить ЖЦ в связи с текущим состоянием активности, который мог быть пропущен из-за асинхронной инициализации.
     */
    private fun Lifecycle.State.restoreLifecycleFor(cb: ActivityLifecycleCallbacks, activity: AppCompatActivity) {
        when (this) {
            Lifecycle.State.CREATED -> {
                cb.onActivityCreated(activity, null)
            }
            Lifecycle.State.STARTED -> {
                cb.onActivityCreated(activity, null)
                cb.onActivityStarted(activity)
            }

            Lifecycle.State.RESUMED -> {
                cb.onActivityCreated(activity, null)
                cb.onActivityStarted(activity)
                cb.onActivityResumed(activity)
            }

            else -> Unit
        }
    }

    /**
     * Изменить [BaseContextPatcher].
     *
     * @param contextPatcher
     */
    @MainThread
    fun <T> T.setBaseContextPatcher(contextPatcher: BaseContextPatcher) where T : Application, T : EntryPoint {
        baseContextPatcher = contextPatcher
    }

    /**
     * Зарегистрировать [callback] с учетом состояния инициализации МП.
     * Синоним [Application.registerActivityLifecycleCallbacks], учитывающий состояние инициализации.
     */
    @AnyThread
    fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks) {
        if (appInitializer.initStatus.value == InitStatus.InitCompleted) {
            coroutineScope.launch(Dispatchers.Main.immediate) {
                application?.registerActivityLifecycleCallbacks(callback)
            }
        } else {
            runBlocking {
                cbMutex.withLock {
                    cachedActivityCallbacks.add(callback)
                }
            }
        }
    }


    /**
     * Разрегистрировать [callback] с учетом состояния инициализации МП.
     * Синоним [Application.unregisterActivityLifecycleCallbacks], учитывающий состояние инициализации.
     */
    @AnyThread
    fun unregisterActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks) {
        if (appInitializer.initStatus.value == InitStatus.InitCompleted) {
            coroutineScope.launch(Dispatchers.Main.immediate) {
                application?.unregisterActivityLifecycleCallbacks(callback)
            }
        } else {
            runBlocking {
                cbMutex.withLock {
                    cachedActivityCallbacks.remove(callback)
                }
            }
        }
    }

    /**
     * Маркерный интерфейс точки входа извне в приложение.
     */
    interface EntryPoint

    /**
     * Маркерный интерфейс legacy точки входа.
     * Полный переход не завершен, осуществлена минимальная поддержка асинхронной инициализации.
     */
    interface LegacyEntryPoint

}
