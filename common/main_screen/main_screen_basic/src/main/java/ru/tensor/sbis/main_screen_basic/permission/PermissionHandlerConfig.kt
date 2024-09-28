/**
 * Конфигурация разрешений, запрашиваемых приложением на главном экране.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.main_screen_basic.permission

import android.app.Activity
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.main_screen_basic.BasicMainScreenPlugin
import ru.tensor.sbis.main_screen_common.permission.MainScreenPermissionHandler
import ru.tensor.sbis.main_screen_common.permission.SerialStartupPermissionLauncher
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider

/**
 * Активировать обработку разрешений, запрашиваемых на главном экране приложения.
 * Разрешения должны быть поставлены в плагинную систему как [StartupPermissionProvider].
 * Следует вызывать в [Activity.onCreate].
 */
fun setupPermissionHandler(
    activity: AppCompatActivity,
    viewLifecycleOwner: LifecycleOwner = activity,
    resultCaller: ActivityResultCaller = activity
) {
    val permissionHandler = MainScreenPermissionHandler(
        lifecycleOwner = viewLifecycleOwner,
        permissions = BasicMainScreenPlugin.startupPermissions.flatMap {
            it.get().permissions
        },
        launcher = SerialStartupPermissionLauncher(
            resultCaller,
            viewLifecycleOwner
        ) { activity }
    )
    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            permissionHandler.activate()
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            permissionHandler.deactivate()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
        }
    })
}