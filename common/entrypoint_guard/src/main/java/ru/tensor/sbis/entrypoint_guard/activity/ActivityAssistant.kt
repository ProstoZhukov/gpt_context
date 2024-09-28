package ru.tensor.sbis.entrypoint_guard.activity

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.tensor.sbis.entrypoint_guard.BaseContextPatcher
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.R
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import ru.tensor.sbis.entrypoint_guard.util.getVM
import ru.tensor.sbis.entrypoint_guard.util.waitTerminalOrBlockingState
import timber.log.Timber
import kotlin.system.exitProcess

/**
 * Перехватчик событий [Activity].
 */
class ActivityAssistant internal constructor(
    private val applicationProvider: () -> Application?,
    private val appStateProvider: () -> AppInitStateHolder<Any?>,
    private val progressHandlerProvider: () -> AppInitializer.ProgressHandler<Any?>,
    private val baseContextPatcherProvider: () -> BaseContextPatcher
) {
    private val activityListeners = mutableListOf<UserActivityListener>()
    private val activityLifecycleExtendedListeners = mutableListOf<ActivityLifecycleExtendedListener>()

    private var rootContainerFactory: ContainerFactory = DefaultRootContainerFactory
    private var initFailureFactory: InitFailureFactory = DefaultInitFailureFactory
    private var onReadyInterceptor: OnReadyInterceptor? = null

    /**
     * Установить [ContainerFactory], которая используется для формирования корневого элемента в верстке экрана.
     *
     * @param factory
     */
    @MainThread
    fun <T> T.setRootContainerFactory(factory: ContainerFactory)
            where T : Application, T : EntryPointGuard.EntryPoint {
        rootContainerFactory = factory
    }

    /**
     * Установить [InitFailureFactory], которая используется для формирования финального экрана
     * в случае ошибки инициализации.
     *
     * @param factory
     */
    @MainThread
    fun <T> T.setInitFailureFactory(factory: InitFailureFactory)
            where T : Application, T : EntryPointGuard.EntryPoint {
        initFailureFactory = factory
    }

    /**
     * Установить перехватчика [interceptor] запроса onReady.
     * Если не установлен, вызов будет обработан [ActivityAssistant].
     */
    @MainThread
    fun <T> T.setOnReadyInterceptor(interceptor: OnReadyInterceptor) {
        onReadyInterceptor = interceptor
    }

    /**
     * Добавить [UserActivityListener].
     *
     * @param listener
     */
    @MainThread
    fun <T> T.addUserActivityListener(listener: UserActivityListener)
            where T : Application, T : EntryPointGuard.EntryPoint {
        activityListeners.add(listener)
    }

    /**
     * Удалить [UserActivityListener].
     *
     * @param listener
     */
    @MainThread
    fun <T> T.removeUserActivityListener(listener: UserActivityListener)
            where T : Application, T : EntryPointGuard.EntryPoint {
        activityListeners.remove(listener)
    }

    /**
     * Добавить [ActivityLifecycleExtendedListener].
     *
     * @param listener
     */
    @MainThread
    fun <T> T.registerActivityLifecycleExtendedListener(listener: ActivityLifecycleExtendedListener)
            where T : Application, T : EntryPointGuard.EntryPoint {
        activityLifecycleExtendedListeners.add(listener)
    }

    /**
     * Удалить [ActivityLifecycleExtendedListener].
     *
     * @param listener
     */
    @MainThread
    fun <T> T.unregisterActivityLifecycleExtendedListener(listener: ActivityLifecycleExtendedListener)
            where T : Application, T : EntryPointGuard.EntryPoint {
        activityLifecycleExtendedListeners.remove(listener)
    }

    /**
     * Перехватить вызов [AppCompatActivity.attachBaseContext].
     *
     * @param activity
     * @param baseContext
     * @param superMethod вызов [AppCompatActivity.super.attachBaseContext]
     */
    fun <T> interceptAttachBaseContext(
        activity: T,
        baseContext: Context?,
        superMethod: (Context?) -> Unit
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        superMethod(baseContextPatcherProvider().invoke(baseContext))
    }

    /**
     * Перехватить вызов [AppCompatActivity.onUserInteraction].
     *
     * @param activity
     * @param superMethod вызов [AppCompatActivity.super.onUserInteraction]
     */
    fun <T> interceptOnUserInteraction(
        activity: T,
        superMethod: () -> Unit
    ) where T : AppCompatActivity {
        superMethod()

        val stateHolder = appStateProvider()
        if (stateHolder.initStatus.value == AppInitStateHolder.InitStatus.InitCompleted) {
            activityListeners.forEach { it.onUserInteraction(activity) }
        }
    }

    /**
     * Перехватить вызов [AppCompatActivity.onUserLeaveHint].
     *
     * @param activity
     * @param superMethod вызов [AppCompatActivity.super.onUserLeaveHint]
     */
    fun <T> interceptOnUserLeaveHint(
        activity: T,
        superMethod: () -> Unit
    ) where T : AppCompatActivity {
        superMethod()

        val stateHolder = appStateProvider()
        if (stateHolder.initStatus.value == AppInitStateHolder.InitStatus.InitCompleted) {
            activityListeners.forEach { it.onUserLeaveHint(activity) }
        }
    }

    /**
     * Перехватить вызов [AppCompatActivity.onNewIntent].
     * Интент будет проигнорирован, если получен до выполнения метода Activity::onCreate.
     *
     * @param activity
     * @param intent
     * @param superMethod вызов [AppCompatActivity.super.onNewIntent]
     * @param onReady колбэк в случае успешного завершения инициализации.
     */
    fun <T> interceptOnNewIntent(
        activity: T,
        intent: Intent?,
        superMethod: (Intent?) -> Unit,
        onReady: (T, Intent?) -> Unit
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        if (activity.getVM().isCreateFinished) {
            superMethod(intent)
            onReady(activity, intent)
        } else {
            Timber.i("The new intent was skipped during initialization $intent")
        }
    }

    /**
     * Перехватить вызов [AppCompatActivity.attachBaseContext]
     * Legacy вызов для планомерной миграции для активностей с большой иерархией наследования.
     * Учитывает случаи, когда приложение еще не использует механизм единых входных точек.
     *
     * @param baseContext
     * @param superMethod вызов [AppCompatActivity.super.attachBaseContext]
     */
    fun interceptAttachBaseContextLegacy(
        activity: AppCompatActivity,
        baseContext: Context?,
        superMethod: (Context?) -> Unit
    ) {
        val appContext = applicationProvider()
        if (appContext == null) {
            superMethod(baseContext)
            return
        }

        val (status, _) = appStateProvider().waitTerminalOrBlockingState().getOrElse {
            superMethod(baseContext)
            return
        }

        if (status == AppInitStateHolder.InitStatus.InitCompleted) {
            superMethod(baseContextPatcherProvider().invoke(baseContext))
        } else {
            /**
             * Достоверно неизвестно, насколько далеко может уйти активность, пока происходит перезапуск,
             * поэтому используем убийство процесса и бесконечный цикл с предварительным перезапуском.
             */
            val packageManager = appContext.packageManager
            val intent: Intent = packageManager.getLaunchIntentForPackage(appContext.packageName)!!
            intent.component?.let {
                if (it.packageName != appContext.packageName || it.className != activity::class.java.name) {
                    val mainIntent: Intent = Intent.makeRestartActivityTask(it)
                    appContext.startActivity(mainIntent)
                } else {
                    throw IllegalStateException((status as AppInitStateHolder.InitStatus.InitFailure).errorMessage)
                }
            }

            exitProcess(0)
            while (true) {}
        }
    }

    /**
     * Перехватить вызов [AppCompatActivity.onCreate].
     * Расширенная версия.
     *
     * @see FailureDelegate
     * @see SuccessDelegate
     */
    fun <T> interceptOnCreate(
        activity: T,
        savedInstanceState: Bundle?,
        superMethod: (Bundle?) -> Unit,
        onContainerInflated: () -> Unit = {},
        rootContainerFactory: ContainerFactory = this.rootContainerFactory,
        failureDelegate: FailureDelegate<T> = object : FailureDelegate<T> {
            override fun onFailure(activity: T, error: String, parentView: FrameLayout) {
                activity.supportFragmentManager.beginTransaction()
                    .replace(parentView.id, initFailureFactory.createFragment(error))
                    .commitNow()
            }
        },
        successDelegate: SuccessDelegate<T>,
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        activityLifecycleExtendedListeners.forEach {
            it.onBeforeCreate(activity, savedInstanceState)
        }

        val appState = appStateProvider()

        clearCreationFlagOnDestroyOf(activity)

        if (savedInstanceState == null) {
            performInitialOnCreate(
                activity,
                appState,
                superMethod,
                onContainerInflated,
                rootContainerFactory,
                createFlaggedSuccessDelegate(successDelegate),
                failureDelegate
            )
        } else {
            performRestore(
                activity,
                appState,
                savedInstanceState,
                superMethod,
                onContainerInflated,
                rootContainerFactory,
                createFlaggedSuccessDelegate(successDelegate),
                failureDelegate,
            )
        }
    }

    /**
     * Перехватить вызов [AppCompatActivity.onCreate].
     * Базовая версия, подходящая для большинства активностей.
     *
     * @param activity
     * @param savedInstanceState
     * @param superMethod вызов [AppCompatActivity.super.onCreate]
     * @param onContainerInflated зовется сразу после inflate [rootContainerFactory].
     * @param rootContainerFactory [ContainerFactory], использующаяся на данном экране.
     *        Если логика должна не зависеть от места встраивания (конкретного приложения).
     * @param onReady колбэк в случае успешного завершения инициализации.
     */
    fun <T> interceptOnCreate(
        activity: T,
        savedInstanceState: Bundle?,
        superMethod: (Bundle?) -> Unit,
        onContainerInflated: () -> Unit = {},
        rootContainerFactory: ContainerFactory = this.rootContainerFactory,
        onReady: (T, parentView: FrameLayout, savedInstanceState: Bundle?) -> Unit
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {

        val successDelegate = object : SuccessDelegate<T> {
            override fun getFragmentFactory(): FragmentFactory? = null

            override fun doBeforeCreate(activity: T, savedState: Bundle?) = Unit

            override fun onReady(activity: T, parentView: FrameLayout, savedInstanceState: Bundle?) {
                onReady(activity, parentView, savedInstanceState)
            }
        }

        interceptOnCreate(
            activity = activity,
            savedInstanceState = savedInstanceState,
            superMethod = superMethod,
            onContainerInflated = onContainerInflated,
            rootContainerFactory = rootContainerFactory,
            successDelegate = successDelegate
        )
    }

    private fun <T> performInitialOnCreate(
        activity: T,
        appState: AppInitStateHolder<*>,
        superMethod: (Bundle?) -> Unit,
        onContainerInflated: () -> Unit,
        rootContainerFactory: ContainerFactory,
        successDelegate: SuccessDelegate<T>,
        failureDelegate: FailureDelegate<T>
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        superMethod(null)
        val container = activity.inflateDefaultLayout(rootContainerFactory)
        onContainerInflated()

        fun handleCompleteState(state: AppInitStateHolder.InitStatus) {
            activity.cleanupContent(container.id)

            when (state) {
                AppInitStateHolder.InitStatus.InitCompleted -> {
                    with(successDelegate) {
                        doBeforeCreate(activity, null)
                        getFragmentFactory()?.let(activity.supportFragmentManager::setFragmentFactory)
                        onReady(activity, container, null)
                    }
                    activity.getVM().savedStateHandle[STATE_USER_CONTENT_PLACED] = true
                }
                is AppInitStateHolder.InitStatus.InitFailure -> {
                    failureDelegate.onFailure(activity, state.errorMessage, container)
                }
                AppInitStateHolder.InitStatus.NotInitialized -> {
                    throw IllegalStateException("Should never happen")
                }
            }
        }

        val currentState = appState.initStatus.value
        if (currentState != AppInitStateHolder.InitStatus.NotInitialized) {
            handleCompleteState(currentState)
        } else {
            val progressHandler = progressHandlerProvider()

            observeWithOneTimeCheck(activity, appState) { state, progress ->
                if (state != AppInitStateHolder.InitStatus.NotInitialized) {
                    handleCompleteState(state)
                } else {
                    progressHandler.handle(activity, container, progress)
                }
            }
        }
    }

    private fun <T> performRestore(
        activity: T,
        appState: AppInitStateHolder<*>,
        savedInstanceState: Bundle?,
        superMethod: (Bundle?) -> Unit,
        onContainerInflated: () -> Unit,
        rootContainerFactory: ContainerFactory,
        successDelegate: SuccessDelegate<T>,
        failureDelegate: FailureDelegate<T>
    ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        val progressHandler = progressHandlerProvider()

        val (initStatus, _) = appState.waitTerminalOrBlockingState().getOrElse {
            activity.supportFragmentManager.fragmentFactory = StubFragmentFactory
            superMethod(null)
            activity.finish()
            return
        }

        if (initStatus != AppInitStateHolder.InitStatus.NotInitialized) {
            when (initStatus) {
                AppInitStateHolder.InitStatus.InitCompleted -> {
                    with(successDelegate) {
                        doBeforeCreate(activity, savedInstanceState)
                        getFragmentFactory()?.let(activity.supportFragmentManager::setFragmentFactory)
                    }
                    superMethod(savedInstanceState)
                    val container = activity.inflateDefaultLayout(rootContainerFactory)
                    onContainerInflated()

                    val isContentAlreadyPlaced = activity.getVM().savedStateHandle
                        .get<Boolean>(STATE_USER_CONTENT_PLACED) == true

                    if (isContentAlreadyPlaced) {
                        successDelegate.onReady(activity, container, savedInstanceState)
                    } else {
                        activity.cleanupContent(container.id)
                        successDelegate.onReady(activity, container, null)
                        activity.getVM().savedStateHandle[STATE_USER_CONTENT_PLACED] = true
                    }
                }
                is AppInitStateHolder.InitStatus.InitFailure -> {
                    // нужно установить фабрику фрагментов-заглушек, чтобы предотвратить восстановление
                    // состояния фрагментов во [FragmentManager].
                    activity.supportFragmentManager.fragmentFactory = StubFragmentFactory
                    superMethod(null)
                    val container = activity.inflateDefaultLayout(rootContainerFactory)
                    onContainerInflated()
                    failureDelegate.onFailure(activity, initStatus.errorMessage, container)
                }
                AppInitStateHolder.InitStatus.NotInitialized -> {
                    throw IllegalStateException("Should never happen!")
                }
            }
        } else {
            // нужно установить фабрику фрагментов-заглушек, чтобы предотвратить восстановление
            // состояния фрагментов во [FragmentManager].
            activity.supportFragmentManager.fragmentFactory = StubFragmentFactory

            superMethod(null)
            val container = activity.inflateDefaultLayout(rootContainerFactory)
            onContainerInflated()

            observeWithOneTimeCheck(activity, appState) { state, progress ->
                when (state) {
                    AppInitStateHolder.InitStatus.InitCompleted -> {
                        activity.cleanupContent(container.id)
                        with(successDelegate) {
                            doBeforeCreate(activity, null)
                            activity.supportFragmentManager.fragmentFactory = getFragmentFactory() ?: FragmentFactory()
                            onReady(activity, container, null)
                        }
                        activity.getVM().savedStateHandle[STATE_USER_CONTENT_PLACED] = true
                    }

                    is AppInitStateHolder.InitStatus.InitFailure -> {
                        failureDelegate.onFailure(activity, state.errorMessage, container)
                    }

                    AppInitStateHolder.InitStatus.NotInitialized -> {
                        progressHandler.handle(activity, container, progress)
                    }
                }
            }
        }
    }


    private fun AppCompatActivity.inflateDefaultLayout(rootContainerFactory: ContainerFactory): FrameLayout {
        val (root, container) = rootContainerFactory.createContainer(this, R.id.assistant_root_id)
        setContentView(root)

        return container
    }

    private fun AppCompatActivity.cleanupContent(@IdRes containerId: Int) {
        val anyFragment = supportFragmentManager.findFragmentById(containerId)
        if (anyFragment != null) {
            supportFragmentManager
                .beginTransaction()
                .remove(anyFragment)
                .commitNow()
        }
    }

    private fun observeWithOneTimeCheck(
        activity: ComponentActivity,
        appState: AppInitStateHolder<*>,
        action: (state: AppInitStateHolder.InitStatus, progress: Any?) -> Unit
    ) {
        var isInitProcessed = false

        appState.initStatus.combine(appState.progressStatus, ::Pair)
            .distinctUntilChanged()
            .asLiveData(Dispatchers.Default)
            .observe(activity) { (state, progress) ->
                if (isInitProcessed) return@observe

                action(state, progress)
                if (state != AppInitStateHolder.InitStatus.NotInitialized) {
                    isInitProcessed = true
                }
            }
    }

    private fun <T> createFlaggedSuccessDelegate(successDelegate: SuccessDelegate<T>): SuccessDelegate<T> where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        return object : SuccessDelegate<T> by successDelegate {
            override fun onReady(activity: T, parentView: FrameLayout, savedInstanceState: Bundle?) {

                val onReady = { act: T, parent: FrameLayout, state: Bundle? ->
                    successDelegate.onReady(act, parent, state)
                    act.getVM().isCreateFinished = true
                }

                onReadyInterceptor?.let { handler ->
                    if (savedInstanceState == null) {
                        activity.lifecycleScope.launch(Dispatchers.Main.immediate) {
                            handler.interceptOnReady(activity, parentView, null, onReady)
                        }
                    } else {
                        handler.interceptOnRestore(activity, parentView, savedInstanceState, onReady)
                    }

                } ?: onReady(activity, parentView, savedInstanceState)

            }
        }
    }

    private fun <T> clearCreationFlagOnDestroyOf(activity: T) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
        val lifecycle = activity.lifecycle
        val obs = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                activity.getVM().isCreateFinished = false
                lifecycle.removeObserver(this)
            }
        }
        lifecycle.addObserver(obs)
    }

    /**
     * Перехватчик вызова [SuccessDelegate.onReady].
     * Дает возможность не вызывать метод по прикладным критериям.
     */
    interface OnReadyInterceptor {

        /**
         *  Перехватить вызов.
         *  Если необходимо отдать управление, звать [onReady].
         */
        suspend fun <T> interceptOnReady(
            activity: T,
            parentView: FrameLayout,
            savedInstanceState: Bundle?,
            onReady: suspend (T, FrameLayout, Bundle?) -> Unit
        ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint

        /**
         *  Перехватить вызов [onReady] при восстановлении c состоянием [savedInstanceState].
         *  Если необходимо отдать управление, звать [onReady].
         */
        @MainThread
        fun <T> interceptOnRestore(
            activity: T,
            parentView: FrameLayout,
            savedInstanceState: Bundle,
            onReady: (T, FrameLayout, Bundle?) -> Unit
        ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint
    }

    /**
     * Слушатель событий [ComponentActivity.onUserInteraction] и [ComponentActivity.onUserLeaveHint].
     */
    interface UserActivityListener {
        /**
         * Вызывается в момент [ComponentActivity.onUserInteraction].
         */
        @MainThread
        fun onUserInteraction(activity: ComponentActivity)

        /**
         * Вызывается в момент [ComponentActivity.onUserLeaveHint].
         */
        @MainThread
        fun onUserLeaveHint(activity: ComponentActivity) = Unit
    }

    /**
     * Фабрика создания корневого элемента экрана, в который будет встроен контент.
     */
    fun interface ContainerFactory {

        /**
         * Метод для создания контейнера.
         *
         * @param activity
         * @param containerId стабильный идентификатор
         */
        @MainThread
        fun createContainer(activity: ComponentActivity, @IdRes containerId: Int): RootContainer
    }

    /** Результат работы [ContainerFactory] */
    data class RootContainer(
        val root: View,
        val container: FrameLayout
    )

    /**
     * Расширенный слушатель событий ЖЦ [Activity].
     */
    interface ActivityLifecycleExtendedListener {

        /**
         * Вызов до [Activity.super.onCreate]
         *
         * @param activity
         * @param bundle
         */
        @MainThread
        fun onBeforeCreate(activity: ComponentActivity, bundle: Bundle?)
    }

    /**
     * Фабрика создания экрана об ошибке инициализации.
     */
    fun interface InitFailureFactory {

        /**
         * Создать фрагмент с ошибкой.
         *
         * @param error
         */
        @MainThread
        fun createFragment(error: String): Fragment
    }


    /**
     * Расширенный интерфейс успешной инициализации МП.
     */
    interface SuccessDelegate<T : AppCompatActivity> {
        /** [FragmentFactory], использующаяся для создания фрагментов приложения в нормальном состоянии. */
        fun getFragmentFactory(): FragmentFactory?

        /** Действия, выполняемые до вызова super.onCreate(). */
        fun doBeforeCreate(activity: T, savedState: Bundle?)

        /** Инициализация МП успешна. */
        fun onReady(activity: T, parentView: FrameLayout, savedInstanceState: Bundle?)
    }

    /**
     * Интерфейс ошибки инициализации МП.
     */
    interface FailureDelegate<T : AppCompatActivity> {
        /** Произошла ошибка [error] во время инициализации. */
        fun onFailure(activity: T, error: String, parentView: FrameLayout)
    }

}

private const val STATE_USER_CONTENT_PLACED = "user_content_placed"