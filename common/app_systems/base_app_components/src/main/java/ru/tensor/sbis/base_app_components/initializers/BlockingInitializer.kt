package ru.tensor.sbis.base_app_components.initializers

import android.app.Application
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.tensor.sbis.base_app_components.error_check.NoMemoryOperator.isInternalMemoryNotSufficient
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus.NotInitialized
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import timber.log.Timber
import ru.tensor.sbis.app_init.R
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureTimeMillis

/**
 * Блокирующий инициализатор МП.
 * Производит инициализацию на [MainThread].
 * Необходимо для корректной настройки [EntryPointGuard] в МП с блокирующей инициализацией и установки статуса инициализации.
 *
 * @param controllerAction действие инициализации контроллера.
 * @param uiAction действие инициализациии плагинной системы и всего остального.
 *
 * @author ar.leschev
 */
internal class BlockingInitializer(
    @MainThread private val controllerAction: () -> Unit,
    @MainThread private val uiAction: () -> Unit
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
        val state = try {
            measureTimeMillis { controllerAction() }.also { log("$it ms", CTRL_INIT_TIME) }
            checkDiskSpace(application)
            measureTimeMillis { uiAction() }.also { log("$it ms", PLUG_INIT_TIME) }
            checkDiskSpace(application)

            InitStatus.InitCompleted
        } catch (error: Exception) {
            Timber.w(error, "error during initialization")
            InitStatus.InitFailure(error.message.orEmpty())
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
