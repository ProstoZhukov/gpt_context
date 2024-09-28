package ru.tensor.sbis.main_screen.widget.dashboard

import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.DashboardConfiguration
import ru.tensor.sbis.main_screen_decl.content.BaseDashboardScreenAddon
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * [ContentController] по умолчанию для автоматически добавляемого экрана с дашбордом.
 *
 * @author us.bessonov
 */
internal class DefaultDashboardContentController(navxId: NavxIdDecl) : BaseDashboardScreenAddon() {

    override val dashboardConfig = DashboardConfiguration(navxId)
}

