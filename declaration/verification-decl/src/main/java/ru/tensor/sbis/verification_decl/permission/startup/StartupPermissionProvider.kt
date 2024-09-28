package ru.tensor.sbis.verification_decl.permission.startup

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик разрешений, которые будут запрошены у пользователя на старте приложения (главного экрана).
 *
 * @property permissions список разрешений для запроса.
 *
 * @author am.boldinov
 */
interface StartupPermissionProvider : Feature {

    val permissions: List<StartupPermission>
}
