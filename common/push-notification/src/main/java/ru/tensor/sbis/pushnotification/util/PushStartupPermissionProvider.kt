package ru.tensor.sbis.pushnotification.util

import android.Manifest
import android.os.Build
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermission
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider

/**
 * Поставщик разрешений, запрашиваемых на старте главного экрана приложения, для модуля пуш-уведомлений.
 *
 * @author am.boldinov
 */
internal class PushStartupPermissionProvider : StartupPermissionProvider {

    override val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            StartupPermission(
                name = Manifest.permission.POST_NOTIFICATIONS,
                oneTime = true
            )
        )
    } else {
        emptyList()
    }
}