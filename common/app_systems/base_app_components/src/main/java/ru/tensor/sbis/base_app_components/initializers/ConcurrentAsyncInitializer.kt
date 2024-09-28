package ru.tensor.sbis.base_app_components.initializers

import android.app.Application
import android.widget.FrameLayout
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.app_init.LaunchFragment
import ru.tensor.sbis.app_init.R
import ru.tensor.sbis.base_app_components.error_check.NoMemoryOperator.isInternalMemoryNotSufficient
import ru.tensor.sbis.controller_utils.loading.StartupExposer
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus.NotInitialized
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException
import kotlin.system.measureTimeMillis

/**
 * Инициализатор МП.
 * Производит инициализацию на worker thread.
 *
 * @param controllerAction действие инициализации контроллера.
 * @param doInit действие инициализациии плагинной системы и всего остального.
 * @param doAfterInit действие после инициализации плагинной системы.
 * @param withFreeDiskSpaceCheck с проверкой свободного пространства на диске.
 * @param withUiStatus нужно ли инициализировать работу [StartupExposer], посылает статусы загрузки на сплеш.
 *
 * @author aa.mezencev
 */
internal class ConcurrentAsyncInitializer(
    @WorkerThread private val controllerAction: () -> Unit,
    @WorkerThread private val doInit: () -> Unit,
    @WorkerThread private val doAfterInit: () -> Unit,
    private val withFreeDiskSpaceCheck: Boolean = true,
    private val withUiStatus: Boolean = true
) : AppInitializer<ConcurrentAsyncInitializer.ProgressState> {

    override val initStatus = MutableStateFlow<InitStatus>(NotInitialized)
    override val progressStatus = MutableStateFlow<ProgressState>(ProgressState.NotStarted)

    override fun isProgressStatusRequireUserInteraction(state: ProgressState): Boolean = false

    override fun initialize(application: Application, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            log("async init begin")
            progressStatus.update { ProgressState.InProgress }
            StartupExposer.start()

            val state = try {
                if (withFreeDiskSpaceCheck) launchDiskSpaceCheck(application)

                coroutineScope {
                    val controllerDeferred = async {
                        measureTimeMillis {
                            controllerAction()
                        }.also { log("$it ms", CTRL_INIT_TIME) }
                    }

                    val pluginSystemDeferred =
                        async(Dispatchers.Default) {
                            measureTimeMillis { doInit() }
                        }.also { log("$it ms", PLUG_INIT_TIME) }

                    awaitAll(controllerDeferred, pluginSystemDeferred)
                }

                withContext(Dispatchers.Default) {
                    doAfterInit.invoke()
                }

                InitStatus.InitCompleted
            } catch (error: Exception) {
                if (error is CancellationException) {
                    throw error
                }
                Timber.w(error, "error during initialization")
                InitStatus.InitFailure(error.message.orEmpty())
            } finally {
                StartupExposer.stop()
            }
            initStatus.update { state }.also { log("init end with $state") }
        }
    }

    override val progressHandler: AppInitializer.ProgressHandler<ProgressState> by lazy {
        object : AppInitializer.ProgressHandler<ProgressState> {
            override fun <A> handle(
                activity: A,
                container: FrameLayout,
                state: ProgressState
            ) where A : AppCompatActivity, A : EntryPointGuard.EntryPoint {
                if (state == ProgressState.InProgress) {
                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            container.id,
                            LaunchFragment.newInstance(withUiStatus = withUiStatus)
                        )
                        .commitNow()
                }
            }
        }
    }

    /**
     * Состояние прогресса инициализации.
     */
    sealed class ProgressState {
        /** Не начата. */
        object NotStarted : ProgressState()

        /** В процессе. */
        object InProgress : ProgressState()
    }

    private fun launchDiskSpaceCheck(application: Application) {
        if (isInternalMemoryNotSufficient(application)) {
            log("Low disk space detected")
            throw Exception(application.getString(R.string.app_init_no_memory_details_msg))
        }
    }

    private fun log(message: String, tag: String? = null) {
        if (tag != null) {
            crashlytics?.setCustomKey(tag, message)
        } else {
            crashlytics?.log(message)
        }
        Timber.i("${tag.orEmpty()} $message")
    }

    private val crashlytics by lazy {
        runCatching { FirebaseCrashlytics.getInstance() }
            .onFailure { Timber.i("FirebaseCrashlytics was not initialized.") }
            .getOrNull()
    }
}
