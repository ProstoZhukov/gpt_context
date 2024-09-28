package ru.tensor.sbis.main_screen_decl.content

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenOptions

/**
 * [ContentController] для автоматически добавляемого экрана с дашбордом.
 * Определяет только создание экрана, за остальное отвечает конкретная реализация.
 * По умолчанию используется стандартный экран с дашбордом, но можно и переопределить метод [createScreen].
 *
 * @author us.bessonov
 */
abstract class BaseDashboardScreenAddon : DashboardContentController, MainScreenAddon, SimplifiedContentController() {

    @CallSuper
    override fun setup(mainScreen: ConfigurableMainScreen) {
        mainScreen.registerDashboardContentController(this)
    }

    override fun reset(mainScreen: ConfigurableMainScreen) = Unit

    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        val fragment = mainScreen.getDashboardScreenProvider()?.getDashboardScreenFragment(
            request = dashboardConfig.customDashboardSize?.let {
                DashboardRequest.NavxId(navxId, it)
            } ?: DashboardRequest.NavxId(navxId),
            options = dashboardConfig.customOptions ?: DashboardScreenOptions()
        )
        return ContentInfo(checkNotNullSafe(fragment) {
            "Cannot create dashboard page. Add DashboardPlugin to plugin system or implement createScreen() manually"
        } ?: Fragment())
    }
}