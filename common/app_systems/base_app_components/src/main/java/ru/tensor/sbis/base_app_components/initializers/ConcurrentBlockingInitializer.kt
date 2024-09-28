package ru.tensor.sbis.base_app_components.initializers

import android.app.Application
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tensor.sbis.app_init.R
import ru.tensor.sbis.base_app_components.error_check.NoMemoryOperator.isInternalMemoryNotSufficient
import ru.tensor.sbis.controller_utils.loading.StartupExposer
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus.NotInitialized
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureTimeMillis

/**
 * Блокирующий инициализатор МП.
 * Производит инициализацию на [MainThread].
 * Необходимо для корректной настройки [EntryPointGuard] в МП с блокирующей инициализацией и установки статуса инициализации.
 *
 * @param controllerAction действие инициализации контроллера.
 * @param doInit действие инициализации плагинной системы.
 * @param doAfterInit действие после инициализации плагинной системы.
 *
 * @author aa.mezencev
 */
internal class ConcurrentBlockingInitializer(
    @WorkerThread private val controllerAction: () -> Unit,
    @WorkerThread private val doInit: () -> Unit,
    @MainThread private val doAfterInit: () -> Unit
) : AppInitializer<Any?> {

    override val initStatus = MutableStateFlow<InitStatus>(NotInitialized)
    override val progressStatus = MutableStateFlow<Any?>(null)

    override fun isProgressStatusRequireUserInteraction(state: Any?): Boolean = false

    private val isLowInternalMemory = AtomicBoolean(false)

    override fun initialize(application: Application, scope: CoroutineScope) {
        log("blocking init begin")
        scope.launch(Dispatchers.Default) {
            isLowInternalMemory.set(isInternalMemoryNotSufficient(application))
        }
        val state = runBlocking {
            try {
                coroutineScope {
                    val controllerDeferred = async(Dispatchers.IO) {
                        runCatching {
                            measureTimeMillis { controllerAction() }
                                .also { log("$it ms", CTRL_INIT_TIME) }
                                .also { checkDiskSpace(application) }
                        }
                    }

                    val pluginSystemDeferred = async(Dispatchers.Default) {
                        runCatching {
                            measureTimeMillis { doInit() }
                                .also { log("$it ms", PLUG_INIT_TIME) }
                                .also { checkDiskSpace(application) }
                        }
                    }

                    awaitAll(controllerDeferred, pluginSystemDeferred)
                }

                doAfterInit.invoke()

                InitStatus.InitCompleted
            } catch (error: Exception) {
                Timber.w(error, "error during initialization")
                InitStatus.InitFailure(error.message.orEmpty())
            }
        }

        initStatus.update { state }.also { Timber.i("init end with $state") }
    }

    private fun checkDiskSpace(application: Application) {
        if (isLowInternalMemory.get()) {
            throw Exception(application.getString(R.string.app_init_no_memory_details_msg))
        }
    }

    override val progressHandler: AppInitializer.ProgressHandler<Any?> =
        object : AppInitializer.ProgressHandler<Any?> {

            override fun <A> handle(
                activity: A,
                container: FrameLayout,
                state: Any?
            ) where A : AppCompatActivity, A : EntryPointGuard.EntryPoint = Unit
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
