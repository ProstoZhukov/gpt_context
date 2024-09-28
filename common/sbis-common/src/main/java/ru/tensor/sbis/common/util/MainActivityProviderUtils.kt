/**
 * Инструменты, связанные с созданием Intent'a MainActivity
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.common.util

import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.android_ext_decl.MainActivityProvider

/**
 * Метод для упрощённого создания [Intent]'а MainActivity на основе [sourceIntent]
 */
fun createMainActivityIntent(
    mainActivityProvider: MainActivityProvider,
    sourceIntent: Intent,
    navigationItem: MenuNavigationItemType,
    navigationMenuArgs: Bundle? = null
): Intent {
    val mainActivityIntent = mainActivityProvider.getMainActivityIntent()
    return Intent().apply {
        action = sourceIntent.action
        data = sourceIntent.data
        clipData = sourceIntent.clipData
        flags = sourceIntent.flags
        `package` = mainActivityIntent.`package`
        component = mainActivityIntent.component
        sourceIntent.extras?.let {
            putExtras(it)
        }
        putExtra(IntentAction.Extra.NAVIGATION_MENU_POSITION, navigationItem)
        navigationMenuArgs?.let {
            putExtra(IntentAction.Extra.NAVIGATION_MENU_ARGS, it)
        }
    }
}