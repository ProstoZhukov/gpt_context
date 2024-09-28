package ru.tensor.sbis.main_screen_common.permission

import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tensor.sbis.verification_decl.permission.startup.PermissionResult
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermission
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionStrategy

/**
 * Задержка по умолчанию для запроса разрешений.
 */
private const val PERMISSION_REQUEST_DELAY = 700L

/**
 * Обработчик разрешений для запроса на главном экране приложения.
 *
 * @param lifecycleOwner компонент, к жизненному циклу которого происходит привязка разрешений
 * @param permissions список требуемых разрешений
 * @param launcher компонент для запроса [StartupPermission] у пользователя
 * @param requestDelayMillis задержка при запросе разрешений
 *
 * @author am.boldinov
 */
@UiThread
class MainScreenPermissionHandler(
    private val lifecycleOwner: LifecycleOwner,
    private val permissions: List<StartupPermission>,
    private val launcher: StartupPermissionLauncher,
    private val requestDelayMillis: Long = PERMISSION_REQUEST_DELAY
) {

    private val permissionNames by lazy(LazyThreadSafetyMode.NONE) {
        mutableMapOf<String, MutableList<StartupPermission>>().apply {
            permissions.forEach { permission ->
                permission.names.forEach {
                    getOrPut(it) {
                        mutableListOf()
                    }.add(permission)
                }
            }
        }
    }

    private var resumeJob: Job? = null

    init {
        if (permissions.isNotEmpty()) {
            lifecycleOwner.lifecycleScope.launch {
                observeCallbackFlow()
            }
        }
    }

    /**
     * Активирует обработку разрешений, осуществляет подписки.
     */
    fun activate() {
        if (permissions.isNotEmpty()) {
            resumeJob?.cancel()
            resumeJob = lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    delay(requestDelayMillis)
                    launch(StartupPermissionStrategy.MAIN_SCREEN_RESUMED)
                }
            }
        }
    }

    /**
     * Деактивирует обработку разрешений, очищает подписки.
     * Отменяет все ожидающие запросы разрешений.
     */
    fun deactivate() {
        if (permissions.isNotEmpty()) {
            resumeJob?.cancel()
            resumeJob = null
            cancel()
        }
    }

    @Suppress("SameParameterValue")
    private fun launch(strategy: StartupPermissionStrategy) {
        launcher.launch(permissions) {
            it.strategy == strategy
        }
    }

    private fun cancel() {
        launcher.cancel()
    }

    private suspend fun observeCallbackFlow() {
        launcher.callbackFlow.collect { result ->
            result.forEach { entry ->
                permissionNames[entry.key]?.forEach {
                    it.callback?.invoke(
                        entry.key,
                        if (entry.value) PermissionResult.GRANTED else PermissionResult.DENIED
                    )
                }
            }
        }
    }

}