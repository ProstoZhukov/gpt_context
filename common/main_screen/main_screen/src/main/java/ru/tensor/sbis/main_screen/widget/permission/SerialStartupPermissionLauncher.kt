package ru.tensor.sbis.main_screen.widget.permission

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermission
import timber.log.Timber
import java.util.LinkedList

/**
 * Реализация компонента для последовательного выполнения запросов разрешений.
 * Необходимо создавать не позднее [Activity.onCreate] или [Fragment.onCreate],
 * в противном случае будет выброшено исключение.
 *
 * @param resultCaller протокол для запроса системных разрешений и получения результатов запроса
 * @param lifecycleOwner компонент, к жизненному циклу которого происходит привязка разрешений
 * @param host активити, на которой происходит запрос разрешений
 *
 * @author am.boldinov
 */
@UiThread
class SerialStartupPermissionLauncher(
    resultCaller: ActivityResultCaller,
    private val lifecycleOwner: LifecycleOwner,
    private val host: () -> Activity
) : StartupPermissionLauncher {

    constructor(fragment: Fragment) : this(fragment, fragment, { fragment.requireActivity() })

    constructor(activity: FragmentActivity) : this(activity, activity, { activity })

    companion object {

        private val oneTimes = mutableSetOf<String>()
    }

    private val queue = LinkedList<StartupPermission>()
    private var rationaleJob: Job? = null

    private val _callbackFlow = MutableSharedFlow<Map<String, Boolean>>(extraBufferCapacity = 1)

    private val launcher = try {
        resultCaller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            _callbackFlow.tryEmit(result)
            requestHead()
        }
    } catch (e: Exception) {
        Timber.w(e, "StartupPermissionLauncher registration failed")
        registerFallbackLauncher {
            requestHead()
        }
    }

    override val callbackFlow: Flow<Map<String, Boolean>> = _callbackFlow

    override fun launch(permissions: List<StartupPermission>, predicate: (StartupPermission) -> Boolean) {
        val request = queue.isEmpty()
        queue.addAll(permissions.filterNotGranted(predicate))
        if (request && queue.isNotEmpty()) {
            requestHead()
        }
    }

    override fun cancel() {
        queue.clear()
    }

    private fun requestHead() {
        rationaleJob?.cancel()
        queue.poll()?.let { permission ->
            permission.rationaleAction?.let { action ->
                rationaleJob = lifecycleOwner.lifecycleScope.launch {
                    action.request(host()).collect { result ->
                        if (result) {
                            launcher.launch(permission.names)
                        }
                    }
                }
            } ?: run {
                launcher.launch(permission.names)
            }
        }
    }

    private inline fun List<StartupPermission>.filterNotGranted(
        predicate: (StartupPermission) -> Boolean
    ): List<StartupPermission> = filter { permission ->
        predicate.invoke(permission) && (!permission.oneTime || oneTimes.add(permission.key())) &&
            permission.names.find {
                (!permission.oneTime || !ActivityCompat.shouldShowRequestPermissionRationale(
                    host(),
                    it
                )) && ActivityCompat.checkSelfPermission(
                    host(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            } != null
    }

    private fun registerFallbackLauncher(callback: () -> Unit): ActivityResultLauncher<Array<String>> {
        return object : ActivityResultLauncher<Array<String>>() {
            override fun launch(input: Array<String>, options: ActivityOptionsCompat?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    host().requestPermissions(input, 1)
                    host().window.decorView.post {
                        callback.invoke()
                    }
                }
            }

            override fun unregister() {

            }

            override fun getContract(): ActivityResultContract<Array<String>, *> {
                error("Unused")
            }
        }
    }


    private fun StartupPermission.key() = names.contentToString()
}

/** @SelfDocumented */
fun StartupPermissionLauncher.Companion.from(fragment: Fragment): StartupPermissionLauncher {
    return SerialStartupPermissionLauncher(fragment)
}

/** @SelfDocumented */
fun StartupPermissionLauncher.Companion.from(activity: FragmentActivity): StartupPermissionLauncher {
    return SerialStartupPermissionLauncher(activity)
}
