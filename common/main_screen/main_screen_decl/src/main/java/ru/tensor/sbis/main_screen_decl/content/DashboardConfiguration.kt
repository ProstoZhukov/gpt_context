package ru.tensor.sbis.main_screen_decl.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenOptions
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardSize
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Конфигурация экрана с дашбордом, автоматически добавляемого в навигацию.
 *
 * @author us.bessonov
 */
class DashboardConfiguration(
    val navxId: NavxIdDecl,
    val visibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> = { MutableLiveData(true) },
    val customDashboardSize: DashboardSize? = null,
    val customOptions: DashboardScreenOptions? = null
) {
    var menuItem: NavigationItem? = null
}